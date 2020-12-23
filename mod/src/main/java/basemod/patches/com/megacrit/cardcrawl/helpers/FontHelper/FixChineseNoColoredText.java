package basemod.patches.com.megacrit.cardcrawl.helpers.FontHelper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.helpers.FontHelper;
import javassist.CtBehavior;

@SpirePatch(
		clz = FontHelper.class,
		method = "exampleNonWordWrappedText"
)
public class FixChineseNoColoredText {
	@SpireInsertPatch(
			locator = Locator.class,
			localvars = {"word"}
	)
	public static void Insert(SpriteBatch sb, BitmapFont font, String msg, float x, float y, Color c, float widthMax, float lineSpacing, @ByRef float[] ___curWidth, @ByRef int[] ___currentLine, String word) {
		if (word.length() > 1 && word.charAt(1) == '#') {
			FontHelper.layout.setText(font, word);
			___curWidth[0] += FontHelper.layout.width;
			if (___curWidth[0] > widthMax) {
				___curWidth[0] = 0.0F;
				++___currentLine[0];
				font.draw(sb, word, x + ___curWidth[0], y - lineSpacing * ___currentLine[0]);
				___curWidth[0] = FontHelper.layout.width;
			} else {
				font.draw(sb, word, x + ___curWidth[0] - FontHelper.layout.width, y - lineSpacing * ___currentLine[0]);
			}
		}
	}

	private static class Locator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(FontHelper.class, "identifyOrb");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}
}
