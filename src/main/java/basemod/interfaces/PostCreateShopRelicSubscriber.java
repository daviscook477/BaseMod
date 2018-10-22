package basemod.interfaces;

import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StoreRelic;

import java.util.ArrayList;

public interface PostCreateShopRelicSubscriber extends ISubscriber {
	void receiveCreateShopRelics(ArrayList<StoreRelic> relics, ShopScreen screenInstance);
}
