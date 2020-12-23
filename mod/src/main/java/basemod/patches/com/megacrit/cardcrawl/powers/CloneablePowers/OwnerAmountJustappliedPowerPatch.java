package basemod.patches.com.megacrit.cardcrawl.powers.CloneablePowers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.powers.*;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.NotFoundException;


@SpirePatch(
        clz = DoubleDamagePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = FrailPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = NoBlockPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = VulnerablePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = WeakPower.class,
        method = SpirePatch.CONSTRUCTOR
)
public class OwnerAmountJustappliedPowerPatch extends CloneablePowersPatch {
    public static void Raw(CtBehavior ctMethodToPatch) throws NotFoundException, CannotCompileException {
        addMakeCopyMethod(ctMethodToPatch, "amount, justApplied");
    }
}
