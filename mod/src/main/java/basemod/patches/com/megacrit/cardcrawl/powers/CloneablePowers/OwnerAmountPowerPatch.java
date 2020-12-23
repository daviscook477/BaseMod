package basemod.patches.com.megacrit.cardcrawl.powers.CloneablePowers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.powers.deprecated.*;
import com.megacrit.cardcrawl.powers.watcher.*;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.NotFoundException;


@SpirePatch(
        clz = AccuracyPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = AfterImagePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = AmplifyPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = AngerPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = AngryPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = ArtifactPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = AttackBurnPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = BattleHymnPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = BeatOfDeathPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = BerserkPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = BiasPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = BlockReturnPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = BlurPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = BrutalityPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = BufferPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = BurstPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = ChokePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = CollectPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = ConservePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = CreativeAIPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = CuriosityPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = CurlUpPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DarkEmbracePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DemonFormPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DEPRECATEDCondensePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DEPRECATEDFlickedPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DEPRECATEDFlowPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DEPRECATEDHotHotPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DEPRECATEDMasterRealityPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DEPRECATEDMasteryPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DEPRECATEDRetributionPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DEPRECATEDSerenityPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DevotionPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DexterityPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DoubleTapPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DrawCardNextTurnPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DrawPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DrawReductionPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = DuplicationPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = EchoPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = EnergizedBluePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = EnergizedPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = EnergyDownPower.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = {
                AbstractCreature.class,
                int.class
        }
)
@SpirePatch(
        clz = EnvenomPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = EquilibriumPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = EstablishmentPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = EvolvePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = ExplosivePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = FadingPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = FeelNoPainPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = FireBreathingPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = FlameBarrierPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = FlightPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = FocusPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = ForesightPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = FreeAttackPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = GainStrengthPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = GrowthPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = HeatsinkPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = HelloPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = HexPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = InfiniteBladesPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = IntangiblePlayerPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = IntangiblePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = InvinciblePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = JuggernautPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = LightningMasteryPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = LikeWaterPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = LiveForeverPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = LockOnPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = LoopPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = LoseDexterityPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = LoseStrengthPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = MagnetismPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = MalleablePower.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = {
                AbstractCreature.class,
                int.class
        }
)
@SpirePatch(
        clz = MantraPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = MarkPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = MayhemPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = MentalFortressPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = MetallicizePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = ModeShiftPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = NirvanaPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = NoxiousFumesPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = OmegaPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = OmnisciencePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = PanachePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = PenNibPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = PhantasmalPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = PlatedArmorPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = RagePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = RechargingCorePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = RegenPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = RepairPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = RetainCardPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = RupturePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = RushdownPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = SadisticPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = SharpHidePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = SkillBurnPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = SlowPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = SporeCloudPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = StaticDischargePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = StormPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = StrengthPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = StrikeUpPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = StudyPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = ThieveryPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = ThornsPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = ThousandCutsPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = TimeMazePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = ToolsOfTheTradePower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = VigorPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = WaveOfTheHandPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = WinterPower.class,
        method = SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz = WraithFormPower.class,
        method = SpirePatch.CONSTRUCTOR
)
public class OwnerAmountPowerPatch extends CloneablePowersPatch {
    public static void Raw(CtBehavior ctMethodToPatch) throws NotFoundException, CannotCompileException {
        addMakeCopyMethod(ctMethodToPatch, "amount");
    }
}
