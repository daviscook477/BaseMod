package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.random.Random;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;

/*
This patch fixes a crash reported mostly with alternate acts, where the game fails to generate an event. This has been
sporadically reported ever since alternate acts became common.
A typical stack trace looks like the following (taken from a reported crash in Gensokyo):
java.lang.IllegalArgumentException: n must be positive
	at com.badlogic.gdx.math.RandomXS128.nextLong(RandomXS128.java:110) ~[desktop-1.0.jar:?]
	at com.badlogic.gdx.math.RandomXS128.nextInt(RandomXS128.java:99) ~[desktop-1.0.jar:?]
	at com.megacrit.cardcrawl.random.Random.random(Random.java:65) ~[desktop-1.0.jar:?]
	at com.megacrit.cardcrawl.dungeons.AbstractDungeon.getShrine(AbstractDungeon.java:2436) ~[?:?]
	at com.megacrit.cardcrawl.dungeons.AbstractDungeon.generateEvent(AbstractDungeon.java:2360) ~[?:?]
	at com.megacrit.cardcrawl.rooms.EventRoom.onPlayerEntry(EventRoom.java:22) ~[?:?]

The cause of this is as follows:
* The normal logic for event generation is that there is always a chance an event will be a "shrine"
* When that chance is triggered, the game checks that at least one of two lists is non-empty: shrineList and specialOneTimeEventList
* If both are empty, the game falls back on generating a normal event
* If at least one is non-empty, the game then proceeds to generate an event from one of those two lists
* It constructs a pool consisting of all events from the shrineList (none of which are ever filtered out) and a subset of the specialOneTimeEventList (doing a variety of filtering, see AbstractDungeon.getShrine)
* It then assumes the resulting list is non-empty and returns one of those events (with code that will crash with the above error if the list is empty)

The crash arises when:
(1) The player visits an alternate act that has no shrines (this is how most alternate acts work)
(2) There is still at least one event in the specialOneTimeEventList (a variety of mods add to this list)
(3) All events in the specialOneTimeEventList are filtered out
When these conditions are true, the game will crash as soon as the player visits an event node and the event generation
logic determines it should get a shrine (because it says "there's at least one shrine or special event, we can go ahead
with generating one", but then it ends up with an empty list and fails).
Per the above, this typically happens due to alternate acts, but anything that clears the list of shrines could create
the same crash.

This can be reproduced by taking an alternate act 1 such as Menagerie or Gensokyo and adding the following code:
* In initializeLevelSpecificChances: shrineChance = 1.0F;
* In initializeEventList: specialOneTimeEventList.add(FaceTrader.ID);
This will result in a guaranteed crash for the first event encountered (that isn't a fight/shop/treasure instead).

Fixing this is a bit tricky because the event generation code is tangled; many methods already call other methods. We
can't add a fallback directly to getShrine because of that. So we do the following, which is just as good.
(1) Change getShrine to return null if the list of valid shrines it generates is empty
(2) Change the critical call to getShrine in generateEvent to fallback to a normal event if getShrine returns null
Importantly, this doesn't touch the other call to getShrine in generateEvent -- that call is only made when getEvent has
already returned null, and if we added the same logic to it, it would form an infinite loop.
*/
public class PreventGetShrineCrashPatch {
    @SpirePatch(clz = AbstractDungeon.class, method = "getShrine")
    public static class ReturnNullIfNoShrinesPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = "tmp")
        public static SpireReturn<AbstractEvent> ReturnNullIfNoShrines(Random rng, ArrayList<String> tmp) {
            if (tmp.isEmpty()) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(Random.class, "random");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "generateEvent")
    public static class FallbackToGetEventPatch {
        public static class FallbackToGetEventExprEditor extends ExprEditor {
            private static int callCount = 0;

            @Override
            public void edit(MethodCall methodCall) throws CannotCompileException {
                if (methodCall.getClassName().equals(AbstractDungeon.class.getName()) && methodCall.getMethodName().equals("getShrine")) {
                    if (callCount == 0) {
                        methodCall.replace(String.format("{ %1$s shrine = getShrine(rng); $_ = shrine != null ? shrine : getEvent(rng); }", AbstractEvent.class.getName()));
                    }
                    callCount++;
                }
            }
        }

        @SpireInstrumentPatch
        public static ExprEditor FallbackToGetEvent() {
            return new FallbackToGetEventPatch.FallbackToGetEventExprEditor();
        }
    }
}
