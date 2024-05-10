package basemod.patches.com.megacrit.cardcrawl.screens.stats.StatsScreen;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;

@SpirePatch2(
        clz = StatsScreen.class,
        method = "renderStatScreen"
)
public class AchievementOffset {

    public AchievementOffset() {}

    @SpireInsertPatch(
            rloc = 8,
            localvars = {"renderY"}
    )
    public static void Insert(StatsScreen __instance, @ByRef float[] renderY) {
        int totalAchievements = BaseMod.getTotalAchievements();
        int rowsNeeded = totalAchievements / 5;
        renderY[0] -= 180.0f * rowsNeeded * Settings.scale;
    }
}