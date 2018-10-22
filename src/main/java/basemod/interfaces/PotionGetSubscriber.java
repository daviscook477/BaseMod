package basemod.interfaces;

import com.megacrit.cardcrawl.potions.AbstractPotion;

public interface PotionGetSubscriber extends ISubscriber {
	void receivePotionGet(AbstractPotion p);
}
