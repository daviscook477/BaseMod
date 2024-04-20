package basemod.abstracts;

import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;

import java.util.regex.Pattern;

public abstract class DynamicVariable
{
    public static Pattern variablePattern = Pattern.compile("(.*)!(.+)!(.*)");

    public abstract String key();

    public abstract boolean isModified(AbstractCard card);

    public void setIsModified(AbstractCard card, boolean v)
    {

    }

    public abstract int value(AbstractCard card);

    public abstract int baseValue(AbstractCard card);

    //Only affects the standard damage, block, and magicNumber variables normally.
    //Can be overriden to do custom modification of base value for a custom variable.
    public int modifiedBaseValue(AbstractCard card) {
        int base = baseValue(card);

        base = CardModifierManager.modifiedBaseValue(card, base, key());

        return base;
    }

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
