package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import basemod.BaseMod;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class BackgroundFix {

	@SpirePatch(cls="com.megacrit.cardcrawl.screens.SingleCardViewPopup",
			method="renderCardBack")
	public static class BackgroundTexture {
		public static void Prefix(Object __obj_instance, Object sbObject) {
			try {
				SingleCardViewPopup popup = (SingleCardViewPopup) __obj_instance;
				SpriteBatch sb = (SpriteBatch) sbObject;
				Field cardField;
				cardField = popup.getClass().getDeclaredField("card");
				cardField.setAccessible(true);
				AbstractCard card = (AbstractCard) cardField.get(popup);
				AbstractCard.CardColor color = card.color;
				switch (card.type) {
				case ATTACK:
					if (!color.toString().equals("RED") && !color.toString().equals("GREEN") && !color.toString().equals("BLUE")
							&& !color.toString().equals("COLORLESS") && !color.toString().equals("CURSE")) {
						Texture bgTexture = null;
						if (card instanceof CustomCard) {
							bgTexture = ((CustomCard) card).getBackgroundLargeTexture();
						}
						if (bgTexture == null) {
							bgTexture = BaseMod.getAttackBgPortraitTexture(color);
							if (bgTexture == null) {
								bgTexture = ImageMaster.loadImage(BaseMod.getAttackBgPortrait(color));
								BaseMod.saveAttackBgPortraitTexture(color, bgTexture);
							}
						}
						sb.draw(bgTexture, Settings.WIDTH / 2.0F - 512.0F, Settings.HEIGHT / 2.0F - 512.0F, 512.0F, 512.0F, 1024.0F, 1024.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1024, 1024, false, false);
					}
					break;
				case POWER:
					if (!color.toString().equals("RED") && !color.toString().equals("GREEN") && !color.toString().equals("BLUE")
							&& !color.toString().equals("COLORLESS") && !color.toString().equals("CURSE")) {
						Texture bgTexture = null;
						if (card instanceof CustomCard) {
							bgTexture = ((CustomCard) card).getBackgroundLargeTexture();
						}
						if (bgTexture == null) {
							bgTexture = BaseMod.getPowerBgPortraitTexture(color);
							if (bgTexture == null) {
								bgTexture = ImageMaster.loadImage(BaseMod.getPowerBgPortrait(color));
								BaseMod.savePowerBgPortraitTexture(color, bgTexture);
							}
						}
						sb.draw(bgTexture, 
								Settings.WIDTH / 2.0F - 512.0F, 
								Settings.HEIGHT / 2.0F - 512.0F, 
								512.0F,
								512.0F, 
								1024.0F, 
								1024.0F, 
								Settings.scale,
								Settings.scale, 
								0.0F, 
								0, 
								0, 
								1024, 
								1024, 
								false, 
								false);
					}
					break;
				default:
					if (color != AbstractCard.CardColor.RED && color != AbstractCard.CardColor.GREEN && color != AbstractCard.CardColor.BLUE
							&& color != AbstractCard.CardColor.COLORLESS && color != AbstractCard.CardColor.CURSE) {
						Texture bgTexture = null;
						if (card instanceof CustomCard) {
							bgTexture = ((CustomCard) card).getBackgroundLargeTexture();
						}
						if (bgTexture == null) {
							bgTexture = BaseMod.getSkillBgPortraitTexture(color);
							if (bgTexture == null) {
								bgTexture = ImageMaster.loadImage(BaseMod.getSkillBgPortrait(color));
								BaseMod.saveSkillBgPortraitTexture(color, bgTexture);
							}
						}
						sb.draw(bgTexture, Settings.WIDTH / 2.0F - 512.0F, Settings.HEIGHT / 2.0F - 512.0F, 512.0F, 512.0F, 1024.0F, 1024.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1024, 1024, false, false);
					}
					break;
				}
			} catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SpirePatch(cls="com.megacrit.cardcrawl.screens.SingleCardViewPopup",
			method="renderCost")
	public static class EnergyOrbTexture {
		
		@SpireInsertPatch(
				locator=Locator.class
		)
		public static void Insert(Object __obj_instance, Object sbObject) {
			try {
				SingleCardViewPopup popup = (SingleCardViewPopup) __obj_instance;
				SpriteBatch sb = (SpriteBatch) sbObject;
				Field cardField;
				cardField = popup.getClass().getDeclaredField("card");
				cardField.setAccessible(true);
				AbstractCard card = (AbstractCard) cardField.get(popup);
				AbstractCard.CardColor color = card.color;
				if (card.cost > -2) {
					if (!color.toString().equals("RED") && !color.toString().equals("GREEN") && !color.toString().equals("BLUE")
							&& !color.toString().equals("COLORLESS") && !color.toString().equals("CURSE")) {
						Texture orbTexture = null;
						if(card instanceof CustomCard) {
							orbTexture = ((CustomCard) card).getOrbLargeTexture();
						}
							
						if(orbTexture == null) {
							orbTexture = BaseMod.getEnergyOrbPortraitTexture(color);
							if (orbTexture == null) {
								orbTexture = ImageMaster.loadImage(BaseMod.getEnergyOrbPortrait(color));
								BaseMod.saveEnergyOrbPortraitTexture(color, orbTexture);
							}
						}
						
						sb.draw(orbTexture, Settings.WIDTH / 2.0F - 82.0F - 270.0F * Settings.scale, Settings.HEIGHT / 2.0F - 82.0F + 380.0F * Settings.scale, 82.0F, 82.0F, 164.0F, 164.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 164, 164, false, false);
					}
				}
			} catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
		{
			Matcher finalMatcher = new Matcher.FieldAccessMatcher("com.megacrit.cardcrawl.cards.AbstractCard", "color");

			return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
		}
		}
	}
	
	@SpirePatch(cls="com.megacrit.cardcrawl.screens.SingleCardViewPopup", method="renderCardBanner")
	public static class BannerTexture {
		public static void Replace(Object __obj_instance, SpriteBatch sb) {
			try {
				SingleCardViewPopup view = (SingleCardViewPopup)__obj_instance;
				AbstractCard card;
				
				Field cardField;
				cardField = view.getClass().getDeclaredField("card");
				cardField.setAccessible(true);
				
				card = (AbstractCard)cardField.get(view);
				
				AbstractCard.CardRarity rarity = card.rarity;
				
				Texture bannerTexture = null;
				if (card instanceof CustomCard) {
					bannerTexture = ((CustomCard)card).getBannerLargeTexture();
				}
				if(bannerTexture == null) {
					switch(rarity.toString()) {
					case "BASIC":
					case "COMMON":
					case "CURSE":
						bannerTexture = ImageMaster.CARD_BANNER_COMMON_L;
						break;
					case "UNCOMMON":
						bannerTexture = ImageMaster.CARD_BANNER_UNCOMMON_L;
						break;
					case "RARE":
						bannerTexture = ImageMaster.CARD_BANNER_RARE_L;
						break;
						default:
							bannerTexture = ImageMaster.CARD_BANNER_COMMON_L;
					}
				}
				sb.draw(bannerTexture, Settings.WIDTH / 2.0f - 512.0f, Settings.HEIGHT / 2.0f - 512.0f, 512.0f, 512.0f, 1024.0f, 1024.0f, 
						Settings.scale, Settings.scale, 0.0f, 0,0,1024,1024, false, false);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
	}
}
