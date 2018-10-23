package basemod.helpers.dynamicvariables;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class MagicNumberVariable extends DynamicVariable
{
    @Override
    public String key()
    {
        return "M";
    }

    @Override
    public boolean isModified(AbstractCard card)
    {
        return card.isMagicNumberModified;
    }

    @Override
    public int value(AbstractCard card)
    {
        return card.magicNumber;
    }

    @Override
    public int baseValue(AbstractCard card)
    {
        return card.baseMagicNumber;
    }

    @Override
    public boolean upgraded(AbstractCard card)
    {
        return card.upgradedMagicNumber;
    }
}
