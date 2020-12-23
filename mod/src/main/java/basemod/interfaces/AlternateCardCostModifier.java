package basemod.interfaces;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;

public interface AlternateCardCostModifier {
    int getAlternateResource(AbstractCard card);

    default boolean prioritizeAlternateCost(AbstractCard card) {
        return false;
    }

    default boolean canSplitCost(AbstractCard card) {
        return false;
    }

    int spendAlternateCost(AbstractCard card, int costToSpend);

    default boolean costEffectActive(AbstractCard card) {
        return true;
    }

    default int setXCostLimit(AbstractCard card) {
        return -1;
    }

    default boolean disableEnergyForX(AbstractCard card) {
        return false;
    }

    default String replaceCostString(AbstractCard card, String currentCostString, Color currentCostColor) {
        return currentCostString;
    }
}
