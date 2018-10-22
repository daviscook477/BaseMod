package basemod.patches.com.megacrit.cardcrawl.ui.panels.PotionPopUp;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.ui.panels.PotionPopUp;
import javassist.CtBehavior;

import java.util.Collections;

@SpirePatch(
		clz=PotionPopUp.class,
		method="updateTargetMode"
)
public class PostPotionUseHookTargetMode
{
	@SpireInsertPatch(
			locator=Locator.class,
			localvars={"potion"}
	)
	public static void Insert(PotionPopUp __instance, AbstractPotion potion)
	{
		BaseMod.publishPostPotionUse(potion);
	}

	private static class Locator extends SpireInsertLocator
	{
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
		{
			Matcher finalMatcher = new Matcher.FieldAccessMatcher(PotionPopUp.class, "targetMode");
			return LineFinder.findInOrder(ctMethodToPatch, Collections.singletonList(finalMatcher), finalMatcher);
		}
	}
}
