package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.ReflectionHacks;
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
			localvars={"word", "currentWidth", "numLines", "CN_DESC_BOX_WIDTH"}
	)
	public static void Insert(AbstractCard __instance, @ByRef String[] word, @ByRef float[] currentWidth,
							  @ByRef int[] numLines,
							  float CN_DESC_BOX_WIDTH)
	{
		StringBuilder sbuilderRFHacks = (StringBuilder) ReflectionHacks.getPrivateStatic(AbstractCard.class, "sbuilder");

		if (word[0].startsWith("!")) {
			GlyphLayout gl = new GlyphLayout(FontHelper.cardDescFont_N, "!M!");
			if (currentWidth[0] + gl.width > CN_DESC_BOX_WIDTH) {
				++numLines[0];
				__instance.description.add(new DescriptionLine(sbuilderRFHacks.toString(), currentWidth[0]));
				sbuilderRFHacks = new StringBuilder();
				currentWidth[0] = gl.width;
				sbuilderRFHacks.append(" ").append(word[0]).append("! ");
			} else {
				sbuilderRFHacks.append(" ").append(word[0]).append("! ");
				currentWidth[0] += gl.width;
			}
			word[0] = "";

			//gl[0] = new GlyphLayout(FontHelper.cardDescFont_N, "!D");
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
