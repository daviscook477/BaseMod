package basemod.patches.com.megacrit.cardcrawl.screens.stats.StatsScreen;

import basemod.BaseMod;
import basemod.ModAchievementGrid;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;

@SpirePatch(clz = StatsScreen.class, method = "update")
public class UpdatePatch {
    public static void Postfix(StatsScreen __instance) {
        for (ModAchievementGrid grid : BaseMod.modAchievementGrids.values()) {
            grid.update();
        }
    }
}