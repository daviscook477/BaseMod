package basemod6.events;

import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;

import java.util.ArrayList;

public class CreateShopPotionEvent extends Event
{
	private ShopScreen shopScreen;
	private ArrayList<StorePotion> potions;

	public CreateShopPotionEvent(ShopScreen shopScreen, ArrayList<StorePotion> potions)
	{
		this.shopScreen = shopScreen;
		this.potions = potions;
	}

	public ShopScreen getShopScreen()
	{
		return shopScreen;
	}

	public ArrayList<StorePotion> getPotions()
	{
		return potions;
	}
}
