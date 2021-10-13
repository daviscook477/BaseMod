package basemod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.localization.LocalizedStrings;

public class RetainMod extends AbstractCardModifier {
    public static String ID = "basemod:RetainCardModifier";

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return GameDictionary.RETAIN.NAMES[0] + LocalizedStrings.PERIOD + " NL " + rawDescription;
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !card.selfRetain;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.selfRetain = true;
    }

    @Override
    public void onRemove(AbstractCard card) {
        card.selfRetain = false;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new RetainMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
