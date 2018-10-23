package basemod.helpers.dynamicvariables;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class DamageVariable extends DynamicVariable
{
    @Override
    public String key()
    {
        return "D";
    }

    @Override
    public boolean isModified(AbstractCard card)
    {
        return card.isDamageModified;
    }

    @Override
    public int value(AbstractCard card)
    {
        return card.damage;
    }

    @Override
    public int baseValue(AbstractCard card)
    {
        return card.baseDamage;
    }

    @Override
    public boolean upgraded(AbstractCard card)
    {
        return card.upgradedDamage;
    }
}
