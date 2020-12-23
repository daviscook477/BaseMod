package basemod.patches.com.megacrit.cardcrawl.powers.CloneablePowers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.powers.deprecated.DEPRECATEDAlwaysMadPower;
import com.megacrit.cardcrawl.powers.deprecated.DEPRECATEDDisciplinePower;
import com.megacrit.cardcrawl.powers.deprecated.DEPRECATEDEmotionalTurmoilPower;
import com.megacrit.cardcrawl.powers.deprecated.DEPRECATEDGroundedPower;
import com.megacrit.cardcrawl.powers.watcher.*;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.NotFoundException;


@SpirePatch(
        clz = BackAttackPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = BarricadePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = CannotChangeStancePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = ConfusionPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = CorpseExplosionPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = CorruptionPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DEPRECATEDAlwaysMadPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DEPRECATEDDisciplinePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DEPRECATEDEmotionalTurmoilPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DEPRECATEDGroundedPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DevaPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = ElectroPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = EndTurnDeathPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = EntanglePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = ForcefieldPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = MasterRealityPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = MinionPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = NoDrawPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = NoSkillsPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = PainfulStabsPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = ReactivePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = ReboundPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = RegrowPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = ResurrectPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = ShiftingPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = SplitPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = SurroundedPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = TimeWarpPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = UnawakenedPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = WrathNextTurnPower.class,
        method = SpirePatch.CONSTRUCTOR
)
public class OwnerPowerPatch extends CloneablePowersPatch {
    public static void Raw(CtBehavior ctMethodToPatch) throws NotFoundException, CannotCompileException {
        addMakeCopyMethod(ctMethodToPatch, "");
    }
}
