package basemod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import org.apache.commons.lang3.StringUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

public class InnateMod extends AbstractCardModifier {
    public static String ID = "basemod:InnateCardModifier";
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if ( Settings.language != Settings.GameLanguage.ENG && uiStrings.TEXT[0].contains("Innate") )
            return StringUtils.capitalize(GameDictionary.INNATE.NAMES[0]) + (Settings.lineBreakViaCharacter ? " " : "") + LocalizedStrings.PERIOD + " NL " + rawDescription;
        return uiStrings.TEXT[0] + rawDescription;
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
