package basemod.patches.com.megacrit.cardcrawl.actions.common.ApplyPowerAction;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

@SpirePatch(cls="com.megacrit.cardcrawl.actions.common.ApplyPowerAction", method="update")
public class ApplyPowerActionPostPowerApplyHook {
	@SpireInsertPatch(rloc=6,localvars= {"powerToApply","target","source"})
	public static void Insert(ApplyPowerAction apa, AbstractPower powerToApply, AbstractCreature target, AbstractCreature source) {
		BaseMod.publishPostPowerApply(powerToApply, target, source);;
	}
}
