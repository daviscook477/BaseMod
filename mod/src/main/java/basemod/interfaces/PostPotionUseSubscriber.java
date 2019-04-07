package basemod.interfaces;

import com.megacrit.cardcrawl.potions.AbstractPotion;

@Deprecated
public interface PostPotionUseSubscriber extends ISubscriber {
	void receivePostPotionUse(AbstractPotion p);
}
