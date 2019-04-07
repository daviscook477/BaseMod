package basemod6.events;

import com.megacrit.cardcrawl.cards.AbstractCard;

public class OnCardUseEvent extends Event
{
    private AbstractCard card;

    public OnCardUseEvent(AbstractCard card)
    {
        this.card = card;
    }

    public AbstractCard getCard()
    {
        return card;
    }
}
