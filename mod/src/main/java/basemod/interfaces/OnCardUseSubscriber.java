package basemod.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

@Deprecated
public interface OnCardUseSubscriber extends ISubscriber {
	void receiveCardUsed(AbstractCard c);
}
