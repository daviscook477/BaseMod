package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import basemod.abstracts.AbstractCardModifier;
import basemod.abstracts.CustomCard;
import basemod.helpers.CardModifierManager;
import basemod.helpers.TooltipInfo;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpirePatch(
		clz=SingleCardViewPopup.class,
		method="renderTips"
)
public class FakeKeywords
{
	@SpireInsertPatch(
			locator=LocatorBefore.class,
			localvars={"card", "t"}
	)
	public static void InsertBefore(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard acard, @ByRef ArrayList<PowerTip>[] t)
	{
		if (acard instanceof CustomCard) {
			CustomCard card = (CustomCard) acard;
			List<TooltipInfo> tooltips = card.getCustomTooltipsTop();
			if (tooltips != null) {
				t[0].addAll(tooltips.stream().map(TooltipInfo::toPowerTip).collect(Collectors.toList()));
			}
		}
	}

	@SpireInsertPatch(
			locator=LocatorAfter.class,
			localvars={"card", "t"}
	)
	public static void InsertAfter(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard acard, @ByRef ArrayList<PowerTip>[] t)
	{
		if (acard instanceof CustomCard) {
			CustomCard card = (CustomCard) acard;
			List<TooltipInfo> tooltips = card.getCustomTooltips();
			if (tooltips != null) {
				t[0].addAll(tooltips.stream().map(TooltipInfo::toPowerTip).collect(Collectors.toList()));
			}
		}
		for (AbstractCardModifier modifier : CardModifierManager.modifiers(acard)) {
			List<TooltipInfo> tooltips = modifier.additionalTooltips(acard);
			if (tooltips != null) {
				t[0].addAll(tooltips.stream().map(TooltipInfo::toPowerTip).collect(Collectors.toList()));
			}
		}
	}

	private static class LocatorBefore extends SpireInsertLocator
	{
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
		{
			Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "keywords");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}

	private static class LocatorAfter extends SpireInsertLocator
	{
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
		{
			Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "isEmpty");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}
}
