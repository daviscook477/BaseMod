package basemod.patches.com.megacrit.cardcrawl.rooms.AbstractRoom;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.StartBattleEvent;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        clz=AbstractRoom.class,
        method="update"
)
public class StartBattleHook {

    @SpireInsertPatch(
            locator=Locator.class
    )
    public static void Insert(AbstractRoom __instance) {
        BaseMod6.EVENT_BUS.post(new StartBattleEvent(__instance));
        BaseMod.publishStartBattle(__instance);
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "applyStartOfCombatPreDrawLogic");
            return LineFinder.findInOrder(ctBehavior, finalMatcher);
        }
    }
}
