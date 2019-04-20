package basemod6.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class CreateStartingDeckEvent extends Event
{
	private AbstractPlayer.PlayerClass playerClass;
	private CardGroup startingDeck;

	public CreateStartingDeckEvent(AbstractPlayer.PlayerClass playerClass, CardGroup startingDeck)
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
