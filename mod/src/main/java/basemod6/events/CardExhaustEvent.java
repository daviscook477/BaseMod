package basemod6.events;

import com.megacrit.cardcrawl.cards.AbstractCard;

public class CardExhaustEvent extends Event
{
	private AbstractCard card;

	public CardExhaustEvent(AbstractCard card)
	{
		this.card = card;
	}

	public AbstractCard getCard()
	{
		return card;
	}
}
