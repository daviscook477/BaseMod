package basemod.patches.com.megacrit.cardcrawl.powers.CloneablePowers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.powers.watcher.VaultPower;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.NotFoundException;


@SpirePatch(
        clz = ConstrictedPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = PoisonPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = VaultPower.class,
        method = SpirePatch.CONSTRUCTOR
)
public class OwnerSourceAmountPowerPatch extends CloneablePowersPatch {
    public static void Raw(CtBehavior ctMethodToPatch) throws NotFoundException, CannotCompileException {
        addMakeCopyMethod(ctMethodToPatch, "source, amount");
    }
}
