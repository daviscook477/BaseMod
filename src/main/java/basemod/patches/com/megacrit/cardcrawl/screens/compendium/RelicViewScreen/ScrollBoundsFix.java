package basemod.patches.com.megacrit.cardcrawl.screens.compendium.RelicViewScreen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.compendium.RelicViewScreen;

import java.lang.reflect.Field;

@SpirePatch(
		cls="com.megacrit.cardcrawl.screens.compendium.RelicViewScreen",
		method="render"
)
public class ScrollBoundsFix
{
	public static void Postfix(RelicViewScreen __instance, SpriteBatch sb) throws NoSuchFieldException, IllegalAccessException
	{
		int row;
		float SPACE;
		float START_Y;

		Field f = RelicViewScreen.class.getDeclaredField("row");
		f.setAccessible(true);
		row = f.getInt(__instance);

		f = RelicViewScreen.class.getDeclaredField("SPACE");
		f.setAccessible(true);
		SPACE = f.getFloat(null);

		f = RelicViewScreen.class.getDeclaredField("START_Y");
		f.setAccessible(true);
		START_Y = f.getFloat(null);

		f = RelicViewScreen.class.getDeclaredField("scrollUpperBound");
		f.setAccessible(true);

		float scrollUpperBounds = START_Y + SPACE * (row + 3) - (Settings.HEIGHT - SPACE * 3);
		f.set(__instance, scrollUpperBounds);
	}
}
