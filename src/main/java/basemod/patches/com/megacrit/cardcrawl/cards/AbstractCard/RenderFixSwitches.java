package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomCard;
import basemod.helpers.SuperclassFinder;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RenderFixSwitches {

	@SpirePatch(cls = "com.megacrit.cardcrawl.cards.AbstractCard", method = "renderBannerImage")
	public static class RenderBannerSwitch {
		public static final Logger logger = LogManager.getLogger(BaseMod.class.getName());
		
		public static SpireReturn<?> Prefix(AbstractCard __instance, SpriteBatch sb, float drawX, float drawY) {
			//if it is not a custom card it cant possibly have the method getBannerSmallTexture force the normal rendering
			if(!(__instance instanceof CustomCard)) return SpireReturn.Continue();
			
			CustomCard card = (CustomCard) __instance;
			Texture texture = card.getBannerSmallTexture();
			if(texture == null) {
				return SpireReturn.Continue();
			}
			
			renderHelper(card, sb, Color.WHITE, texture, drawX, drawY);
			
			return SpireReturn.Return(null);
		}
	}
	
	@SpirePatch(cls = "com.megacrit.cardcrawl.cards.AbstractCard", method = "renderEnergy")
	public static class RenderEnergySwitch {
		public static SpireReturn<?> Prefix(AbstractCard __instance, SpriteBatch sb) {
			//if it is not a custom card use the default renderer
			if(!(__instance instanceof CustomCard)) 
				return SpireReturn.Continue();
			
			//if the custom card is cost -2 or locked or not seen or darken end the method
			if(__instance.cost <= -2 || __instance.isLocked || !__instance.isSeen 
					|| (boolean) ReflectionHacks.getPrivate(__instance, AbstractCard.class, "darken")) {
				return SpireReturn.Return(null);
			}
			
			CustomCard card = (CustomCard) __instance;
			
			float current_x = (float) ReflectionHacks.getPrivate(__instance, AbstractCard.class, "current_x");
			float current_y = (float) ReflectionHacks.getPrivate(__instance, AbstractCard.class, "current_y");
			
			float drawX = current_x - 256f;
			float drawY = current_y - 256f;

			Texture texture = card.getOrbSmallTexture();
			
			if(texture == null) {
				Texture baseModTexture = BaseMod.getEnergyOrbTexture(card.color);
				if (baseModTexture == null) {
					if(card.color != AbstractCard.CardColor.RED &&
							card.color != AbstractCard.CardColor.GREEN &&
							card.color != AbstractCard.CardColor.BLUE &&
							card.color != AbstractCard.CardColor.COLORLESS &&
							card.color != AbstractCard.CardColor.CURSE) {
						texture = ImageMaster.loadImage(BaseMod.getEnergyOrb(card.color));
						BaseMod.saveEnergyOrbTexture(card.color, texture);
					} else {
						switch(card.color) {
						case BLUE:
							texture = ImageMaster.CARD_BLUE_ORB;
							break;
						case GREEN:
							texture = ImageMaster.CARD_GREEN_ORB;
							break;
						case RED:
							texture = ImageMaster.CARD_RED_ORB;
							break;
						case COLORLESS:
						case CURSE:
							texture = ImageMaster.CARD_COLORLESS_ORB;
							break;
						}
					}
				} else {
					texture = baseModTexture;
				}
			}
			
			renderHelper(card, sb, Color.WHITE.cpy(), texture, drawX, drawY);
			
			
			if(card.type == CardType.STATUS && card.cost == -2) return SpireReturn.Return(null);
			if(card.type == CardType.CURSE && card.cost == -2) return SpireReturn.Return(null);
			
			Color costColor = Color.WHITE.cpy();
            if (AbstractDungeon.player != null && AbstractDungeon.player.hand.contains(card) && !card.hasEnoughEnergy()) {
                costColor = (Color) ReflectionHacks.getPrivateStatic(AbstractCard.class, "ENERGY_COST_RESTRICTED_COLOR");
            } else if(card.isCostModified || card.isCostModifiedForTurn || card.freeToPlayOnce) {
                costColor = (Color) ReflectionHacks.getPrivateStatic(AbstractCard.class, "ENERGY_COST_MODIFIED_COLOR");
            }
			
			costColor.a = card.transparency;
			String text = getCost(card);
			FontHelper.cardEnergyFont_L.getData().setScale(card.drawScale);
			BitmapFont font = FontHelper.cardEnergyFont_L;
			
			FontHelper.renderRotatedText(sb, font, text, current_x, current_y, -132.0f * card.drawScale * Settings.scale, 192.0f * card.drawScale * Settings.scale, card.angle, false, costColor);
			
			return SpireReturn.Return(null);
		}
		
		private static String getCost(AbstractCard card) {
			if(card.cost == -1)
				return "X";
			if(card.freeToPlayOnce)
				return "0";
			return Integer.toString(card.costForTurn);
		}
	}
	
	@SpirePatch(cls = "com.megacrit.cardcrawl.cards.AbstractCard", method = "renderOuterGlow")
	public static class RenderOuterGlowSwitch {
		public static final Logger logger = LogManager.getLogger(BaseMod.class.getName());
		
		@SpireInsertPatch(rloc = 9)
		public static void Insert(Object __obj_instance, Object sbObj) {
			AbstractCard card = (AbstractCard) __obj_instance;
			CardColor color = card.color;
			SpriteBatch sb = (SpriteBatch) sbObj;
			if (color != CardColor.RED && color != CardColor.GREEN && color != CardColor.BLUE
					&& color != CardColor.COLORLESS && color != CardColor.CURSE) {
				Color glowColor = BaseMod.getGlowColor(color);
				if (glowColor == null) {
					glowColor = Color.WHITE;
				}
				try {
					// use reflection hacks to invoke renderHelper (with float scale)
					Field current_x;
					current_x = AbstractCard.class.getDeclaredField("current_x");
					current_x.setAccessible(true);
					Field current_y;
					current_y = AbstractCard.class.getDeclaredField("current_y");
					current_y.setAccessible(true);
					Field tintColor;
					tintColor = AbstractCard.class.getDeclaredField("tintColor");
					tintColor.setAccessible(true);
					Method renderHelperMethod = AbstractCard.class.getDeclaredMethod("renderHelper", SpriteBatch.class,
							Color.class, Texture.class, float.class, float.class, float.class);
					renderHelperMethod.setAccessible(true);
					renderHelperMethod.invoke(card, sb, glowColor, card.getCardBg(),
							((Float)current_x.get(card)) - 256.0f, ((Float)current_y.get(card)) - 256.0f, 1.0F + ((Color)tintColor.get(card)).a / 5.0f);
				} catch (Exception e) {
					logger.error("could not render outer glow for card " + card.getClass().toString() + " with color " + color.toString());
					logger.error("with exception: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	
	@SpirePatch(cls = "com.megacrit.cardcrawl.cards.AbstractCard", method = "renderCardBg")
	public static class RenderBgSwitch {
		public static SpireReturn<?> Prefix(AbstractCard __instance, SpriteBatch sb, float xPos, float yPos) {
			if(!(__instance instanceof CustomCard) 
					|| __instance.color==AbstractCard.CardColor.RED 
					|| __instance.color==AbstractCard.CardColor.GREEN
					|| __instance.color==AbstractCard.CardColor.BLUE 
					|| __instance.color==AbstractCard.CardColor.COLORLESS
					|| __instance.color==AbstractCard.CardColor.CURSE) return SpireReturn.Continue();
			CardColor color = __instance.color;
			CustomCard card = (CustomCard) __instance;
			Texture texture;
			
			switch(card.type) {
			case POWER:
				if (BaseMod.getPowerBgTexture(color) == null) {
					BaseMod.savePowerBgTexture(color, ImageMaster.loadImage(BaseMod.getPowerBg(color)));
				}
				texture = BaseMod.getPowerBgTexture(color);
				break;
			case ATTACK:
				if (BaseMod.getAttackBgTexture(color) == null) {
					BaseMod.saveAttackBgTexture(color, ImageMaster.loadImage(BaseMod.getAttackBg(color)));
				}
				texture = BaseMod.getAttackBgTexture(color);
				break;
			case SKILL:
				if (BaseMod.getSkillBgTexture(color) == null) {
					BaseMod.saveSkillBgTexture(color, ImageMaster.loadImage(BaseMod.getSkillBg(color)));
				}
				texture = BaseMod.getSkillBgTexture(color);
				break;
			default:
				texture = ImageMaster.CARD_SKILL_BG_BLACK;
				break;
			}
			
			if(!(card.textureBackgroundSmallImg == null) && !card.textureBackgroundSmallImg.isEmpty()) {
				texture = card.getBackgroundSmallTexture();
			}
			
			if(texture == null) {
				BaseMod.logger.info(color.toString() + " texture is null wtf");
				return SpireReturn.Continue();
			}
			
			renderHelper(card, sb, Color.WHITE, texture, xPos, yPos);
			
			return SpireReturn.Return(null);
		}
	}
	
	private static void renderHelper(AbstractCard card, SpriteBatch sb, Color color, Texture texture, float xPos, float yPos) {
		try {
			// use reflection hacks to invoke renderHelper (without float scale)
			Method renderHelperMethod;
			Field renderColorField; 
			
			
			renderHelperMethod = SuperclassFinder.getSuperClassMethod(card.getClass(), "renderHelper", SpriteBatch.class, Color.class, Texture.class, float.class, float.class);
			renderHelperMethod.setAccessible(true);
			renderColorField = SuperclassFinder.getSuperclassField(card.getClass(), "renderColor");
			renderColorField.setAccessible(true);
			
				
			Color renderColor = (Color) renderColorField.get(card);
			renderHelperMethod.invoke(card, sb, renderColor, texture, xPos, yPos);
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	private static void renderHelper(AbstractCard card, SpriteBatch sb, Color color, Texture texture, float xPos, float yPos, float scale) {
		try {
			// use reflection hacks to invoke renderHelper (without float scale)
			Method renderHelperMethod;
			Field renderColorField; 
			
			
			renderHelperMethod = SuperclassFinder.getSuperClassMethod(card.getClass(), "renderHelper", SpriteBatch.class, Color.class, Texture.class, float.class, float.class, float.class);
			renderHelperMethod.setAccessible(true);
			renderColorField = SuperclassFinder.getSuperclassField(card.getClass(), "renderColor");
			renderColorField.setAccessible(true);
			
				
			Color renderColor = (Color) renderColorField.get(card);
			renderHelperMethod.invoke(card, sb, renderColor, texture, xPos, yPos);
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException | SecurityException e) {
			e.printStackTrace();
		}
	}
}
