package basemod6.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class PostCreateStartingDeckEvent extends Event
{
	private AbstractPlayer.PlayerClass playerClass;
	private CardGroup startingDeck;

	public PostCreateStartingDeckEvent(AbstractPlayer.PlayerClass playerClass, CardGroup startingDeck)
	{
		this.playerClass = playerClass;
		this.startingDeck = startingDeck;
	}

	public AbstractPlayer.PlayerClass getPlayerClass()
	{
		return playerClass;
	}

	public CardGroup getStartingDeck()
	{
		return startingDeck;
	}

	public void addCard(AbstractCard card)
	{
		startingDeck.addToTop(card);
	}
}
