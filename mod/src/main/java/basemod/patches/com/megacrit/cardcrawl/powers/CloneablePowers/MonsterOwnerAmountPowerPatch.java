package basemod.patches.com.megacrit.cardcrawl.powers.CloneablePowers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.RegenerateMonsterPower;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.NotFoundException;


@SpirePatch(
        clz = RegenerateMonsterPower.class,
        method = SpirePatch.CONSTRUCTOR
)
public class MonsterOwnerAmountPowerPatch extends CloneablePowersPatch {
    public static void Raw(CtBehavior ctMethodToPatch) throws NotFoundException, CannotCompileException {
        addMakeCopyMethod(ctMethodToPatch, "(" + AbstractMonster.class.getName() + ")", "amount");
    }
}
