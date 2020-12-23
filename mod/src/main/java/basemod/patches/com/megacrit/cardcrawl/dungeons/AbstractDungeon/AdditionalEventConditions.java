package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.eventUtil.EventUtils;
import basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer.SeenEvents;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import javassist.CtBehavior;

import java.util.ArrayList;

public class AdditionalEventConditions {
    //Normal events: AbstractDungeon line 2467
    /*
        if (tmp.isEmpty()) {// 2467
            return getShrine(rng);// 2468
    }*/
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getEvent"
    )
    public static class NormalEventConditions {
        @SpireInsertPatch(
                locator = NormalEventConditionLocator.class,
                localvars = {"tmp"}
        )
        public static void insert(Random rng, ArrayList<String> tmp) {
            tmp.removeIf((e) -> EventUtils.normalEventBonusConditions.containsKey(e) && !EventUtils.normalEventBonusConditions.get(e).test());
        }

        private static class NormalEventConditionLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "isEmpty");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }


    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getEvent"
    )
    public static class RemoveSeenEvents {
        @SpireInsertPatch(
                locator = RemoveEventLocator.class,
                localvars = {"tmpKey"}
        )
        public static void insert(Random rng, String tmpKey) {
            SeenEvents.seenEvents.get(AbstractDungeon.player).add(tmpKey);
        }

        private static class RemoveEventLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "remove");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    //Shrine events and SpecialOneTimeEvents: AbstractDungeon line 2416
    /*
            String tmpKey = (String)tmp.get(rng.random(tmp.size() - 1));// 2416
     */
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getShrine"
    )
    public static class SpecialEventConditions {
        @SpireInsertPatch(
                locator = SpecialEventConditionLocator.class,
                localvars = {"tmp"}
        )
        public static void insert(Random rng, ArrayList<String> tmp) {
            tmp.removeIf((e) -> EventUtils.specialEventBonusConditions.containsKey(e) && !EventUtils.specialEventBonusConditions.get(e).test());
        }

        private static class SpecialEventConditionLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "get");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
