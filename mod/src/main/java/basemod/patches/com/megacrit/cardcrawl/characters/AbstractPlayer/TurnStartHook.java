package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "applyStartOfTurnPostDrawRelics"
)
public class TurnStartHook {
    @SpirePostfixPatch
    public static void onTurnStart(AbstractPlayer __instance) {
        BaseMod.publishOnTurnStart();
    }
}
