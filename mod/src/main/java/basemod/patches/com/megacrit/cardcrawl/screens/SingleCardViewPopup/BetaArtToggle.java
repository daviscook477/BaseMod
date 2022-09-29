package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import basemod.BaseMod;
import basemod.patches.com.megacrit.cardcrawl.screens.VictoryScreen.TrackKilledHeart;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

@SpirePatch(
        clz = SingleCardViewPopup.class,
        method = "canToggleBetaArt"
)
public class BetaArtToggle {
    @SpirePostfixPatch
    public static boolean otherCharacterCheck(boolean __result, SingleCardViewPopup __instance, AbstractCard ___card) {
        if (___card != null && !__result && !BaseMod.isBaseGameCardColor(___card.color)) {
            for (AbstractPlayer p : CardCrawlGame.characterManager.getAllCharacters()) {
                if (p.getCardColor() == ___card.color) {
                    Prefs playerPrefs = p.getPrefs();
                    return playerPrefs != null && playerPrefs.getBoolean(TrackKilledHeart.HEART_KILL, false);
                }
            }
        }
        return __result;
    }
}
