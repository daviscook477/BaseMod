package basemod.patches.com.megacrit.cardcrawl.powers.CloneablePowers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.powers.RitualPower;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.NotFoundException;


@SpirePatch(
        clz = RitualPower.class,
        method = SpirePatch.CONSTRUCTOR
)
public class OwnerAmountOnplayerPowerPatch extends CloneablePowersPatch {
    public static void Raw(CtBehavior ctMethodToPatch) throws NotFoundException, CannotCompileException {
        addMakeCopyMethod(ctMethodToPatch, "amount, onPlayer");
    }
}
