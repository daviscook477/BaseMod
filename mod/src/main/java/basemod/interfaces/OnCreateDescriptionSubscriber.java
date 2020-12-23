package basemod.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnCreateDescriptionSubscriber extends ISubscriber {
	String receiveCreateCardDescription(String rawDescription, AbstractCard card);
}
