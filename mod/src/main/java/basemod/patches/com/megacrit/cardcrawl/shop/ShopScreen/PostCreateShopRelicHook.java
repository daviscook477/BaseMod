package basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod6.BaseMod6;
import basemod6.events.PostCreateShopRelicEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StoreRelic;

import java.util.ArrayList;

@SpirePatch(
		clz=ShopScreen.class,
		method="initRelics"
)
public class PostCreateShopRelicHook {
	@SuppressWarnings("unchecked")
	public static void Postfix(ShopScreen __instance) {
		ArrayList<StoreRelic> relics = (ArrayList<StoreRelic>) ReflectionHacks.getPrivate(__instance, ShopScreen.class, "relics");
		BaseMod6.EVENT_BUS.post(new PostCreateShopRelicEvent(__instance, relics));
		BaseMod.publishPostCreateShopRelics(relics, __instance);
	}
}
