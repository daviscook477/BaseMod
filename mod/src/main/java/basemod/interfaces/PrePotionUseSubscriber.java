package basemod.interfaces;

import com.megacrit.cardcrawl.potions.AbstractPotion;

@Deprecated
public interface PrePotionUseSubscriber extends ISubscriber {
	void receivePrePotionUse(AbstractPotion p);
}
