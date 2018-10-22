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
				scrollUpperBoundField.set(__instance, scrollUpperBoundField.getFloat(__instance) +
						(SIZE_PER_CHARACTER * characterCount * Settings.scale));
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				logger.error("could not calculate updated scroll bounds");
				logger.error("error was: " + e.toString());
				e.printStackTrace();
			}
		}
	}
}
