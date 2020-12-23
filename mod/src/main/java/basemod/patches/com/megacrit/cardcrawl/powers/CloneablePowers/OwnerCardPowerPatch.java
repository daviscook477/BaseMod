package basemod.patches.com.megacrit.cardcrawl.powers.CloneablePowers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.powers.StasisPower;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.NotFoundException;


@SpirePatch(
        clz = StasisPower.class,
        method = SpirePatch.CONSTRUCTOR
)
public class OwnerCardPowerPatch extends CloneablePowersPatch {
    public static void Raw(CtBehavior ctMethodToPatch) throws NotFoundException, CannotCompileException {
        addMakeCopyMethod(ctMethodToPatch, "card");
    }
}
