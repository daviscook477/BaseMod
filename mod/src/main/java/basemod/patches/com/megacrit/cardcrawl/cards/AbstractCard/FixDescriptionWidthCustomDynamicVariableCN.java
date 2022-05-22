package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DescriptionLine;
import com.megacrit.cardcrawl.helpers.FontHelper;
import javassist.CtBehavior;

@SpirePatch(
		clz= AbstractCard.class,
		method="initializeDescriptionCN"
)
public class FixDescriptionWidthCustomDynamicVariableCN
{
	@SpireInsertPatch(
			locator=Locator.class,
			localvars={"word", "currentWidth", "sbuilder", "numLines", "CN_DESC_BOX_WIDTH"}
	)
	public static void Insert(AbstractCard __instance, @ByRef String[] word, @ByRef float[] currentWidth,
							  StringBuilder currentLine, @ByRef int[] numLines,
							  float CN_DESC_BOX_WIDTH)
	{
		if (word[0].startsWith("!")) {
			GlyphLayout gl = new GlyphLayout(FontHelper.cardDescFont_N, "!M!");
			if (currentWidth[0] + gl.width > CN_DESC_BOX_WIDTH) {
				++numLines[0];
				__instance.description.add(new DescriptionLine(currentLine.toString(), currentWidth[0]));
				currentLine.setLength(0);
				currentWidth[0] = gl.width;
				currentLine.append(" ").append(word[0]).append("! ");
			} else {
				currentLine.append(" ").append(word[0]).append("! ");
				currentWidth[0] += gl.width;
			}
			word[0] = "";
		}
	}

	private static class Locator extends SpireInsertLocator
	{
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
		{
			Matcher finalMatcher = new Matcher.MethodCallMatcher(String.class, "toCharArray");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}
}
