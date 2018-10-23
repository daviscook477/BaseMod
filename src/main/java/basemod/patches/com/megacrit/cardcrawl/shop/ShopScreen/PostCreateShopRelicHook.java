package basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StoreRelic;

import java.util.ArrayList;

@SpirePatch(cls="com.megacrit.cardcrawl.shop.ShopScreen", method="initRelics")
public class PostCreateShopRelicHook {
//	@SpireInsertPatch(loc=300, localvars={"i", "relic"})
//	public static void Insert(Object __obj_instance, int i, Object relicObj) {
//		relicObj = BaseMod.publishPostCreateShopRelic((StoreRelic) relicObj, i, (ShopScreen) __obj_instance);
//	}
	
	@SuppressWarnings("unchecked")
	public static void Postfix(Object __obj_instance) {
		ShopScreen me = (ShopScreen) __obj_instance;
		BaseMod.publishPostCreateShopRelics(
					(ArrayList<StoreRelic>) ReflectionHacks.getPrivate(me,  ShopScreen.class, "relics"),
					me
				);
	}
}
