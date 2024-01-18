package basemod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;


public class EtherealMod extends AbstractCardModifier {
    public static String ID = "basemod:EtherealCardModifier";
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return String.format(uiStrings.TEXT[0], rawDescription);
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
