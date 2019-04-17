package basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod6.BaseMod6;
import basemod6.events.PostCreateShopPotionEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;

import java.util.ArrayList;

@SpirePatch(cls="com.megacrit.cardcrawl.shop.ShopScreen", method="initPotions")
@SpirePatch(
		clz=ShopScreen.class,
		method="initPotions"
)
public class PostCreateShopPotionHook {
	@SuppressWarnings("unchecked")
	public static void Postfix(ShopScreen __instance) {
		ArrayList<StorePotion> potions = (ArrayList<StorePotion>) ReflectionHacks.getPrivate(__instance, ShopScreen.class, "potions");
		BaseMod6.EVENT_BUS.post(new PostCreateShopPotionEvent(__instance, potions));
		BaseMod.publishPostCreateShopPotions(potions, __instance);
	}
}
