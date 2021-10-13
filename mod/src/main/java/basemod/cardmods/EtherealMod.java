package basemod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.localization.LocalizedStrings;

public class EtherealMod extends AbstractCardModifier {
    public static String ID = "basemod:EtherealCardModifier";

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return GameDictionary.ETHEREAL.NAMES[0] + LocalizedStrings.PERIOD + " NL " + rawDescription;
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !card.isEthereal;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.isEthereal = true;
    }

    @Override
    public void onRemove(AbstractCard card) {
        card.isEthereal = false;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new EtherealMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
