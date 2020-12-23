package basemod.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface XCostModifier {
    int modifyX(AbstractCard c);

    default boolean xCostModifierActive(AbstractCard c) {
        return true;
    }
}
