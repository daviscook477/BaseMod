package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "initializeStarterDeck"
)
public class FloorZeroStarterDeckFix {
    public static SpireReturn<Void> Prefix(AbstractPlayer __instance) {
        if(__instance.masterDeck.size() > 1) {
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}
