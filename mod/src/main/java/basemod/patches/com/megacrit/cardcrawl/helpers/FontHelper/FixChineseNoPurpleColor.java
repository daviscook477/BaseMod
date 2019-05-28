package basemod.patches.com.megacrit.cardcrawl.helpers.FontHelper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import javassist.CtBehavior;

@SpirePatch(
		clz=FontHelper.class,
		method="exampleNonWordWrappedText"
)
public class FixChineseNoPurpleColor
{
	@SpireInsertPatch(
			locator=Locator.class,
			localvars={"word"}
	)
	public static void Insert(SpriteBatch sb, BitmapFont font, String msg, float x, float y, Color c, float widthMax, float lineSpacing, @ByRef String[] word)
	{
		if (word[0].charAt(1) == 'p') {
			word[0] = "[#" + Settings.PURPLE_COLOR.toString() + "]" + word[0].substring(2) + "[]";
		}
	}

	private static class Locator extends SpireInsertLocator
	{
		@Override
		public int[] Locate(CtBehavior ctBehavior) throws Exception
		{
			Matcher matcher = new Matcher.MethodCallMatcher(String.class, "substring");
			int[] found = LineFinder.findInOrder(ctBehavior, matcher);
			return new int[]{found[0]+1};
		}
	}
}
