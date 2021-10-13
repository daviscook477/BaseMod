package basemod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.localization.LocalizedStrings;

public class InnateMod extends AbstractCardModifier {
    public static String ID = "basemod:InnateCardModifier";

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return GameDictionary.INNATE.NAMES[0] + LocalizedStrings.PERIOD + " NL " + rawDescription;
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !card.isInnate;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.isInnate = true;
    }

    @Override
    public void onRemove(AbstractCard card) {
        card.isInnate = false;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new InnateMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
