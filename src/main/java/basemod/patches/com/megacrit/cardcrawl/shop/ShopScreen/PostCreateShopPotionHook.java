package basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;

import java.util.ArrayList;

@SpirePatch(cls="com.megacrit.cardcrawl.shop.ShopScreen", method="initPotions")
public class PostCreateShopPotionHook {
//	@SpireInsertPatch(loc=315, localvars={"i", "potion"})
//	public static void Insert(Object __obj_instance, int i, Object potionObj) {
//		potionObj = BaseMod.publishPostCreateShopPotion((StorePotion) potionObj, i, (ShopScreen) __obj_instance);
//	}

	@SuppressWarnings("unchecked")
	public static void Postfix(Object __obj_instance) {
		ShopScreen me = (ShopScreen) __obj_instance;
		BaseMod.publishPostCreateShopPotions(
					(ArrayList<StorePotion>) ReflectionHacks.getPrivate(me, ShopScreen.class, "potions"),
					me
				);
	}
	
}
