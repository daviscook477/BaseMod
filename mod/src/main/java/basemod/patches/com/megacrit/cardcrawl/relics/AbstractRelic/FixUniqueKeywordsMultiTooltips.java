package basemod.patches.com.megacrit.cardcrawl.relics.AbstractRelic;

import basemod.BaseMod;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.FixDescriptionWidthCustomDynamicVariable;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CannotCompileException;
import javassist.CtBehavior;

@SpirePatch(
		clz=AbstractRelic.class,
		method="initializeTips"
)
public class FixUniqueKeywordsMultiTooltips
{
	@SpireInsertPatch(
			locator=Locator.class,
			localvars={"s", "t", "alreadyExists"}
	)
	public static void Insert(AbstractRelic __instance, String s, PowerTip t, @ByRef boolean[] alreadyExists)
	{
		if (BaseMod.keywordIsUnique(s)) {
			s = FixDescriptionWidthCustomDynamicVariable.removeLowercasePrefix(s, BaseMod.getKeywordPrefix(s));
			if (t.header.toLowerCase().equals(s)) {
				alreadyExists[0] = true;
			}
		}
	}

	private static class Locator extends SpireInsertLocator
	{
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
		{
			Matcher finalMatcher = new Matcher.FieldAccessMatcher(PowerTip.class, "header");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}
}
