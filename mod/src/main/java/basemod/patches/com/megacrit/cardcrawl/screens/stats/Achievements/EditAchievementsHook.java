package basemod.patches.com.megacrit.cardcrawl.screens.stats.Achievements;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.stats.AchievementGrid;

@SpirePatch(
		clz=AchievementGrid.class,
		method=SpirePatch.CONSTRUCTOR
)
public class EditAchievementsHook
{
	public static void Postfix(AchievementGrid __instance)
	{
		BaseMod.publishEditAchievements(__instance);
	}
}
