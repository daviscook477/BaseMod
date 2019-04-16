package basemod6.events;

import com.megacrit.cardcrawl.cards.AbstractCard;

public class PostExhaustEvent extends Event
{
	private AbstractCard card;

	public PostExhaustEvent(AbstractCard card)
	{
		this.card = card;
	}

	public AbstractCard getCard()
	{
		return card;
	}
}
