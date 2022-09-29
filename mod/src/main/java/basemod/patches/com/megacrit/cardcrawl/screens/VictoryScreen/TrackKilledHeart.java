package basemod.patches.com.megacrit.cardcrawl.screens.VictoryScreen;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import javassist.CtBehavior;

@SpirePatch(
        clz = VictoryScreen.class,
        method = SpirePatch.CONSTRUCTOR
)
public class TrackKilledHeart {
    public static final String HEART_KILL = "basemod:HEART_KILL";

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void trackHeartKill(VictoryScreen __instance, MonsterGroup m) {
        if (!BaseMod.isBaseGameCharacter(AbstractDungeon.player)) {
            Prefs charPrefs = AbstractDungeon.player.getPrefs();
            if (charPrefs != null && !charPrefs.getBoolean(HEART_KILL, false)) {
                charPrefs.putBoolean(HEART_KILL, true);
                charPrefs.flush();
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(Prefs.class, "flush");
            return LineFinder.findInOrder(ctBehavior, finalMatcher);
        }
    }
}
