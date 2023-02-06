package basemod.abstracts;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;

public abstract class DynamicVariable
{
    public abstract String key();

    public abstract boolean isModified(AbstractCard card);

    public void setIsModified(AbstractCard card, boolean v)
    {

    }

    public abstract int value(AbstractCard card);

    public abstract int baseValue(AbstractCard card);

    public abstract boolean upgraded(AbstractCard card);

    public Color getNormalColor()
    {
        return Settings.CREAM_COLOR;
    }

    public Color getUpgradedColor()
    {
        return Settings.GREEN_TEXT_COLOR;
    }

    public Color getUpgradedColor(AbstractCard card)
    {
        return getUpgradedColor();
    }

    public Color getIncreasedValueColor()
    {
        return Settings.GREEN_TEXT_COLOR;
    }

    public Color getDecreasedValueColor()
    {
        return Settings.RED_TEXT_COLOR;
    }
}
