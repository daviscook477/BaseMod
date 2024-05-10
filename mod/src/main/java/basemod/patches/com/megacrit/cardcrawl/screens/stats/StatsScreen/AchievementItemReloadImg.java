package basemod.patches.com.megacrit.cardcrawl.screens.stats.StatsScreen;

import basemod.ModAchievement;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.screens.stats.AchievementItem;

@SpirePatch(
        clz = AchievementItem.class,
        method = "reloadImg"
)
public class AchievementItemReloadImg {
    public AchievementItemReloadImg() {
    }

    @SpirePostfixPatch
    public static void Postfix(AchievementItem __instance) {
        if (__instance instanceof ModAchievement) {
            ((ModAchievement) __instance).currentImg = ((ModAchievement) __instance).atlas.findRegion(((ModAchievement) __instance).currentImg.name);
        }
    }
}
