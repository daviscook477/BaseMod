package basemod.patches.com.megacrit.cardcrawl.screens.stats.StatsScreen;

import basemod.BaseMod;
import basemod.ModAchievementGrid;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public class UpdateStats {
	public static final Logger logger = LogManager.getLogger(BaseMod.class.getName());
	public static final float SIZE_PER_CHARACTER = 400.0F;

	@SpirePatch(
			clz=StatsScreen.class,
			method="calculateScrollBounds"
	)
	public static class ScrollBounds {
		public static void Postfix(StatsScreen __instance) {
			try {
				Field scrollUpperBoundField = __instance.getClass().getDeclaredField("scrollUpperBound");
				scrollUpperBoundField.setAccessible(true);
				float modAchievementsHeight = 0.0F;
				boolean firstGrid = true;
				for (ModAchievementGrid grid : BaseMod.modAchievementGrids.values()) {
					float gridHeight = grid.calculateHeight();
					modAchievementsHeight += gridHeight;
					if (firstGrid) {
						modAchievementsHeight -= 50.0F * Settings.scale;
						firstGrid = false;
					}
					modAchievementsHeight += 100.0F * Settings.scale;
				}
				int characterCount = BaseMod.getModdedCharacters().size();
				float characterHeight = SIZE_PER_CHARACTER * characterCount * Settings.scale;
				float currentUpperBound = scrollUpperBoundField.getFloat(__instance);
				float newUpperBound = currentUpperBound + characterHeight + modAchievementsHeight;
				scrollUpperBoundField.set(__instance, newUpperBound);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				logger.error("could not calculate updated scroll bounds");
				logger.error("error was: " + e.toString());
				e.printStackTrace();
			}
		}
	}
}