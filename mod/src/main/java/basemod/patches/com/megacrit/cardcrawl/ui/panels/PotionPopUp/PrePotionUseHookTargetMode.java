package basemod.patches.com.megacrit.cardcrawl.ui.panels.PotionPopUp;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.PrePotionUseEvent;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.ui.panels.PotionPopUp;
import javassist.CannotCompileException;
import javassist.CtBehavior;

@SpirePatch(
		clz=PotionPopUp.class,
		method="updateTargetMode"
)
public class PrePotionUseHookTargetMode
{
	@SpireInsertPatch(
			locator=Locator.class,
			localvars={"potion"}
	)
	public static void Insert(PotionPopUp __instance, AbstractPotion potion)
	{
		BaseMod6.EVENT_BUS.post(new PrePotionUseEvent(potion));
		BaseMod.publishPrePotionUse(potion);
	}

	private static class Locator extends SpireInsertLocator
	{
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
		{
			Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPotion.class, "use");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}
}
