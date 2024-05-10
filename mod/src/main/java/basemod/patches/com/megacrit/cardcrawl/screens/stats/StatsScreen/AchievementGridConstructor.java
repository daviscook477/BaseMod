package basemod.patches.com.megacrit.cardcrawl.screens.stats.StatsScreen;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.screens.stats.AchievementGrid;

@SpirePatch(clz = AchievementGrid.class, method = "<ctor>")
public class AchievementGridConstructor {
    public AchievementGridConstructor() {}

    @SpirePostfixPatch
    public static void Postfix(AchievementGrid instance) {
        BaseMod.publishEditAchievements(instance);
    }
}