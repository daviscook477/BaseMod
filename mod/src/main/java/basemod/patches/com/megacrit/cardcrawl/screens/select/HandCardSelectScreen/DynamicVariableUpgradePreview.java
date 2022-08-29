package basemod.patches.com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;

import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.SmithPreview;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import javassist.CtBehavior;

import java.util.HashMap;
import java.util.Map;

@SpirePatch2(
		clz = HandCardSelectScreen.class,
		method = "render"
)
public class DynamicVariableUpgradePreview
{
	private static Map<String, Boolean> save = new HashMap<>();

	@SpireInsertPatch(
			locator = Locator1.class
	)
	public static void Insert1(AbstractCard ___upgradePreviewCard)
	{
		save.clear();
		SmithPreview.ForEachDynamicVariable(___upgradePreviewCard, (card, dv) -> {
			save.put(dv.key(), dv.isModified(card));
		});
	}

	@SpireInsertPatch(
			locator = Locator2.class
	)
	public static void Insert2(AbstractCard ___upgradePreviewCard)
	{
		SmithPreview.ForEachDynamicVariable(___upgradePreviewCard, (card, dv) -> {
			boolean t = save.getOrDefault(dv.key(), false);
			if (t) {
				dv.setIsModified(card, true);
			}
		});
		save.clear();
	}


	private static class Locator1 extends SpireInsertLocator
	{
		@Override
		public int[] Locate(CtBehavior ctBehavior) throws Exception
		{
			Matcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "applyPowers");
			return LineFinder.findInOrder(ctBehavior, matcher);
		}
	}

	private static class Locator2 extends SpireInsertLocator
	{
		@Override
		public int[] Locate(CtBehavior ctBehavior) throws Exception
		{
			Matcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "render");
			return LineFinder.findInOrder(ctBehavior, matcher);
		}
	}
}
