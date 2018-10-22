package basemod.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface PostExhaustSubscriber extends ISubscriber {
	void receivePostExhaust(AbstractCard c);
}
