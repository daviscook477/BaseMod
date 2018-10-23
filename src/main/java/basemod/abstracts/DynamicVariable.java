package basemod.abstracts;

import com.megacrit.cardcrawl.cards.AbstractCard;

public abstract class DynamicVariable
{
    public abstract String key();

    public abstract boolean isModified(AbstractCard card);

    public abstract int value(AbstractCard card);

    public abstract int baseValue(AbstractCard card);

    public abstract boolean upgraded(AbstractCard card);
}
