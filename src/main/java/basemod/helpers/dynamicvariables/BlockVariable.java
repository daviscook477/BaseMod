package basemod.helpers.dynamicvariables;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class BlockVariable extends DynamicVariable
{
    @Override
    public String key()
    {
        return "B";
    }

    @Override
    public boolean isModified(AbstractCard card)
    {
        return card.isBlockModified;
    }

    @Override
    public int value(AbstractCard card)
    {
        return card.block;
    }

    @Override
    public int baseValue(AbstractCard card)
    {
        return card.baseBlock;
    }

    @Override
    public boolean upgraded(AbstractCard card)
    {
        return card.upgradedBlock;
    }
}
