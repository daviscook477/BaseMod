package basemod.interfaces;

import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;

import java.util.ArrayList;

public interface PostCreateShopPotionSubscriber extends ISubscriber {
	void receiveCreateShopPotions(ArrayList<StorePotion> potions, ShopScreen screenInstance);
}
