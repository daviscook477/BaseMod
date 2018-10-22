package basemod.patches.com.megacrit.cardcrawl.core.AbstractCreature;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import javassist.CtBehavior;

public class PlayerMaxHPChange
{
	private static boolean recursive = false;

	@SpirePatch(
			clz=AbstractCreature.class,
			method="increaseMaxHp"
	)
	public static class Increase
	{
		@SpireInsertPatch(
				locator=Locator.class,
				localvars={"amount"}
		)
		public static SpireReturn<Void> Insert(AbstractCreature __instance, int amountParam, boolean showEffect, @ByRef int[] amount)
		{
			if (recursive) {
				return SpireReturn.Continue();
			}

			amount[0] = BaseMod.publishMaxHPChange(amount[0]);
			if (amount[0] == 0) {
				return SpireReturn.Return(null);
			} else if (amount[0] < 0) {
				recursive = true;
				__instance.decreaseMaxHealth(-amount[0]);
				recursive = false;
				return SpireReturn.Return(null);
			}
			return SpireReturn.Continue();
		}

		private static class Locator extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws Exception
			{
				Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCreature.class, "maxHealth");
				return LineFinder.findInOrder(ctBehavior, finalMatcher);
			}
		}
	}

	@SpirePatch(
			clz=AbstractCreature.class,
			method="decreaseMaxHealth"
	)
	public static class Decrease
	{
		@SpireInsertPatch(
				locator=Locator.class,
				localvars={"amount"}
		)
		public static SpireReturn<Void> Insert(AbstractCreature __instance, int amountParam, @ByRef int[] amount)
		{
			if (recursive) {
				return SpireReturn.Continue();
			}

			amount[0] = -BaseMod.publishMaxHPChange(-amount[0]);
			if (amount[0] == 0) {
				return SpireReturn.Return(null);
			} else if (amount[0] < 0) {
				recursive = true;
				__instance.increaseMaxHp(-amount[0], false);
				recursive = false;
				return SpireReturn.Return(null);
			}
			return SpireReturn.Continue();
		}

		private static class Locator extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws Exception
			{
				Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCreature.class, "maxHealth");
				return LineFinder.findInOrder(ctBehavior, finalMatcher);
			}
		}
	}
}
