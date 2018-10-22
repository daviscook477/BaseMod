package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateCardImageSwitch {
	public static final Logger logger = LogManager.getLogger(BaseMod.class.getName());
	
	@SpirePatch(cls = "com.megacrit.cardcrawl.cards.AbstractCard", method = "createCardImage")
	public static class CreateCardImage {
		public static SpireReturn<?> Prefix(AbstractCard __instance) {
			//if its color is a default color skip the rest of this
			if(__instance.color == AbstractCard.CardColor.RED || __instance.color == AbstractCard.CardColor.GREEN ||	
				__instance.color == AbstractCard.CardColor.BLUE || __instance.color == AbstractCard.CardColor.COLORLESS ||
				__instance.color == AbstractCard.CardColor.CURSE) {
				return SpireReturn.Continue();
			}
			
			//sets the card colors
			ReflectionHacks.setPrivate(__instance, AbstractCard.class, "bgColor", BaseMod.getBgColor(__instance.color).cpy());
			ReflectionHacks.setPrivate(__instance, AbstractCard.class, "backColor", BaseMod.getBackColor(__instance.color).cpy());
			ReflectionHacks.setPrivate(__instance, AbstractCard.class, "frameColor", BaseMod.getFrameColor(__instance.color).cpy());
			ReflectionHacks.setPrivate(__instance, AbstractCard.class, "frameOutlineColor", BaseMod.getFrameOutlineColor(__instance.color).cpy());
			ReflectionHacks.setPrivate(__instance, AbstractCard.class, "descBoxColor", BaseMod.getDescBoxColor(__instance.color).cpy());
			
			//sets rarity colors
			switch(__instance.rarity) {
			case SPECIAL:
			case RARE:
				ReflectionHacks.setPrivate(__instance, AbstractCard.class, "bannerColor", 
						((Color) ReflectionHacks.getPrivateStatic(AbstractCard.class, "BANNER_COLOR_RARE")).cpy());
				ReflectionHacks.setPrivate(__instance, AbstractCard.class, "imgFrameColor", 
						((Color) ReflectionHacks.getPrivateStatic(AbstractCard.class, "IMG_FRAME_COLOR_RARE")).cpy());
				break;
			case UNCOMMON:
				ReflectionHacks.setPrivate(__instance, AbstractCard.class, "bannerColor", 
						((Color) ReflectionHacks.getPrivateStatic(AbstractCard.class, "BANNER_COLOR_UNCOMMON")).cpy());
				ReflectionHacks.setPrivate(__instance, AbstractCard.class, "imgFrameColor", 
						((Color) ReflectionHacks.getPrivateStatic(AbstractCard.class, "IMG_FRAME_COLOR_UNCOMMON")).cpy());
				break;
			case BASIC:
			case COMMON:
			case CURSE:
			default:
				ReflectionHacks.setPrivate(__instance, AbstractCard.class, "bannerColor", 
						((Color) ReflectionHacks.getPrivateStatic(AbstractCard.class, "BANNER_COLOR_COMMON")).cpy());
				ReflectionHacks.setPrivate(__instance, AbstractCard.class, "imgFrameColor", 
						((Color) ReflectionHacks.getPrivateStatic(AbstractCard.class, "IMG_FRAME_COLOR_COMMON")).cpy());
				break;
			}
			
			//sets the tint and shadow colors
			ReflectionHacks.setPrivate(__instance, AbstractCard.class, "tintColor", new Color(43.0f, 37.0f, 65.0f, 0.0f));
			ReflectionHacks.setPrivate(__instance, AbstractCard.class, "frameShadowColor", 
					((Color) ReflectionHacks.getPrivateStatic(AbstractCard.class, "FRAME_SHADOW_COLOR")).cpy());
			
			return SpireReturn.Return(null);
		}
	}
}
