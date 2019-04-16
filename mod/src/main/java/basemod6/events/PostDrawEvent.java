package basemod6.events;

import com.megacrit.cardcrawl.cards.AbstractCard;

public class PostDrawEvent extends Event
{
	private AbstractCard card;

	public PostDrawEvent(AbstractCard card)
	{
		this.card = card;
	}

	public AbstractCard getCard()
	{
		return card;
	}
}
