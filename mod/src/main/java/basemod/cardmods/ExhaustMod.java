package basemod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

public class ExhaustMod extends AbstractCardModifier {
    public static String ID = "basemod:ExhaustCardModifier";
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return String.format(uiStrings.TEXT[0], rawDescription);
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !card.exhaust;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.exhaust = true;
    }

    @Override
    public void onRemove(AbstractCard card) {
        card.exhaust = false;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ExhaustMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
