package basemod.patches.com.megacrit.cardcrawl.screens.compendium.PotionViewScreen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.compendium.PotionViewScreen;

import java.lang.reflect.Field;

public class ScrollBoundsFix
{
	@SpirePatch(
			clz= PotionViewScreen.class,
			method="render"
	)
	public static class UpperBounds
	{
		public static void Postfix(PotionViewScreen __instance, SpriteBatch sb) throws NoSuchFieldException, IllegalAccessException
		{
			int row;
			float SPACE;
			float START_Y = Settings.HEIGHT - 300f * Settings.scale;

			Field f = PotionViewScreen.class.getDeclaredField("row");
			f.setAccessible(true);
			row = f.getInt(__instance);

			f = PotionViewScreen.class.getDeclaredField("SPACE");
			f.setAccessible(true);
			SPACE = f.getFloat(null);

			f = PotionViewScreen.class.getDeclaredField("scrollUpperBound");
			f.setAccessible(true);

			float scrollUpperBounds = START_Y + SPACE * (row + 3) - (Settings.HEIGHT - SPACE * 3);
			scrollUpperBounds = Math.max(scrollUpperBounds, Settings.HEIGHT - 100 * Settings.scale);
			f.set(__instance, scrollUpperBounds);
		}
	}

	@SpirePatch(
			clz=PotionViewScreen.class,
			method="open"
	)
	public static class OpenStartBounds
	{
		public static void Postfix(PotionViewScreen __instance, @ByRef float[] ___scrollLowerBound, @ByRef float[] ___targetY)
		{
			___scrollLowerBound[0] = Settings.HEIGHT - 100 * Settings.scale;
			___targetY[0] = ___scrollLowerBound[0];
		}
	}
}
