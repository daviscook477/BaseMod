package basemod.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnCardUseSubscriber extends ISubscriber {
	void receiveCardUsed(AbstractCard c);
}
