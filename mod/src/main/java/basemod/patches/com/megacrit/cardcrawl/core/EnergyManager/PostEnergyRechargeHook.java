package basemod.patches.com.megacrit.cardcrawl.core.EnergyManager;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.PostEnergyRechargeEvent;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.core.EnergyManager;
import javassist.CtBehavior;

@SpirePatch(
		clz=EnergyManager.class,
		method="recharge"
)
public class PostEnergyRechargeHook
{
    @SpireInsertPatch(
    		locator=Locator.class
	)
    public static void Insert(EnergyManager __instance)
	{
		BaseMod6.EVENT_BUS.post(new PostEnergyRechargeEvent());
        BaseMod.publishPostEnergyRecharge();
    }

	private static class Locator extends SpireInsertLocator
	{
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
		{
			Matcher finalMatcher = new Matcher.MethodCallMatcher(GameActionManager.class, "updateEnergyGain");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}
}
