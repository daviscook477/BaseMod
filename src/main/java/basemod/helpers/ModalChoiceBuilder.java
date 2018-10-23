package basemod.helpers;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class ModalChoiceBuilder
{
    private String title = "Choose an Option";
    private ArrayList<AbstractCard> options = new ArrayList<>();
    private ModalChoice.Callback callback = null;
    private AbstractCard.CardColor color = AbstractCard.CardColor.COLORLESS;
    private AbstractCard.CardType type = AbstractCard.CardType.SKILL;

    public ModalChoiceBuilder()
    {
    }

    public ModalChoice create()
    {
        return new ModalChoice(title, options);
    }

    public ModalChoiceBuilder setTitle(String title)
    {
        this.title = title;
        return this;
    }

    public ModalChoiceBuilder addOption(String description, AbstractCard.CardTarget target)
    {
        return addOption(null, description, target);
    }

    public ModalChoiceBuilder addOption(String title, String description, AbstractCard.CardTarget target)
    {
        if (title == null) {
            title = "Option " + (options.size() + 1);
        }
        return addOption(new ModalChoiceCard(title, description, type, color, target, options.size(), callback));
    }

    public ModalChoiceBuilder addOption(AbstractCard card)
    {
        options.add(card);
        return this;
    }

    // setColor - Sets the color for all following automatically created ModalChoiceCards.
    //            Does not affect previously added cards
    public ModalChoiceBuilder setColor(AbstractCard.CardColor color)
    {
        this.color = color;
        return this;
    }

    // setType - Sets the card type for all following automatically created ModalChoiceCards.
    //           Does not affect previously added cards
    public ModalChoiceBuilder setType(AbstractCard.CardType type)
    {
        this.type = type;
        return this;
    }

    // setCallback - Sets the callback for all following automatically created ModalChoiceCards.
    //               Does not affect previously added cards
    public ModalChoiceBuilder setCallback(ModalChoice.Callback callback)
    {
        this.callback = callback;
        return this;
    }
}
