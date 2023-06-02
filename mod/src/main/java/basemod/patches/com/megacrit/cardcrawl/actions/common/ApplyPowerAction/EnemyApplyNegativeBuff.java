package basemod.patches.com.megacrit.cardcrawl.actions.common.ApplyPowerAction;

import basemod.abstracts.CustomPlayer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.FocusPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpirePatch(
        clz = ApplyPowerAction.class,
        paramtypez = {AbstractCreature.class, AbstractCreature.class, AbstractPower.class, int.class, boolean.class, AbstractGameAction.AttackEffect.class },
        method=SpirePatch.CONSTRUCTOR)
public class EnemyApplyNegativeBuff {
    private static final Set<String> powerIds = new HashSet<>(Arrays.asList(StrengthPower.POWER_ID, DexterityPower.POWER_ID, FocusPower.POWER_ID));

    @SpirePrefixPatch
    public static SpireReturn<Void> prefix(ApplyPowerAction instance, AbstractCreature target, AbstractCreature source, AbstractPower powerToApply, int stackAmount) {
        if (target instanceof CustomPlayer &&
                source instanceof AbstractMonster &&
                powerToApply != null &&
                powerIds.contains(powerToApply.ID) &&
                stackAmount < 0 &&
                powerToApply.amount == stackAmount) {

            if (((CustomPlayer) target).onEnemyApplyNegativeBuff((AbstractMonster) source, powerToApply.ID, stackAmount)) {
                instance.isDone = true;
                return SpireReturn.Return();
            }
        }

        return SpireReturn.Continue();
    }
}
