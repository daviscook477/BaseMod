package basemod6.events;

import com.megacrit.cardcrawl.cards.AbstractCard;

public class CardUseEvent extends Event
{
    private AbstractCard card;

    public CardUseEvent(AbstractCard card)
    {
        this.card = card;
    }

    public AbstractCard getCard()
    {
        return card;
    }
}
