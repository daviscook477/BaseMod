package basemod.interfaces;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

@Deprecated
public interface PostCreateStartingDeckSubscriber extends ISubscriber {
	void receivePostCreateStartingDeck(AbstractPlayer.PlayerClass chosenClass, CardGroup addCardsToMe);
}
