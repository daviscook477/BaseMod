package basemod.patches.com.megacrit.cardcrawl.screens.stats.StatsScreen;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public class UpdateStats
{
	public static final Logger logger = LogManager.getLogger(BaseMod.class.getName());

	public static final float SIZE_PER_CHARACTER = 400.0F;
	public static final float SIZE_PER_ACHIEVEMENT_ROW = 180.0F; // Assuming each row of achievements needs 180.0F of space

	@SpirePatch(
			clz=StatsScreen.class,
			method="calculateScrollBounds"
	)
	public static class ScrollBounds
	{
		public static void Postfix(StatsScreen __instance)
		{
			try {
				Field scrollUpperBoundField = __instance.getClass().getDeclaredField("scrollUpperBound");
				scrollUpperBoundField.setAccessible(true);

				int characterCount = BaseMod.getModdedCharacters().size();
				int totalAchievements = BaseMod.getTotalAchievements();
				int achievementRows = totalAchievements / 5;

				float extraHeight = (SIZE_PER_CHARACTER * characterCount + SIZE_PER_ACHIEVEMENT_ROW * achievementRows) * Settings.scale;
				scrollUpperBoundField.set(__instance, scrollUpperBoundField.getFloat(__instance) + extraHeight);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				logger.error("could not calculate updated scroll bounds");
				logger.error("error was: " + e.toString());
				e.printStackTrace();
			}
		}
	}
}
