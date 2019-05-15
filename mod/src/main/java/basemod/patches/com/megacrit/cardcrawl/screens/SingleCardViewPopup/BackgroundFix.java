package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import basemod.BaseMod;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.lang.reflect.Field;

public class BackgroundFix
{
	@SpirePatch(
			clz=SingleCardViewPopup.class,
			method="renderCardBack"
	)
	public static class BackgroundTexture
	{
		public static  SpireReturn<?> Prefix(Object __obj_instance, Object sbObject)
		{
			try {
				SingleCardViewPopup popup = (SingleCardViewPopup) __obj_instance;
				SpriteBatch sb = (SpriteBatch) sbObject;
				Field cardField;
				cardField = popup.getClass().getDeclaredField("card");
				cardField.setAccessible(true);
				AbstractCard card = (AbstractCard) cardField.get(popup);
				AbstractCard.CardColor color = card.color;
				
				
				if (!(card instanceof CustomCard)) {
					return SpireReturn.Continue();
				} else if ((((CustomCard) card).textureBackgroundLargeImg == null)
						|| ((CustomCard) card).textureBackgroundLargeImg.isEmpty()) {
					if (color == AbstractCard.CardColor.RED
							|| color == AbstractCard.CardColor.GREEN
							|| color == AbstractCard.CardColor.BLUE
							|| color == AbstractCard.CardColor.COLORLESS
							|| color == AbstractCard.CardColor.CURSE) {
						return SpireReturn.Continue();
					}
				}
				
					switch (card.type) {
						case ATTACK: {
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
						case POWER: {
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
						default: {
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
			return SpireReturn.Return(null);
		}
	}
	
	@SpirePatch(
			clz=SingleCardViewPopup.class,
			method="renderCost"
	)
	public static class EnergyOrbTexture
	{
		public static ExprEditor Instrument()
		{
			return new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException
				{
					if (m.getClassName().equals(SpriteBatch.class.getName()) && m.getMethodName().equals("draw")) {
						m.replace(EnergyOrbTexture.class.getName() + ".drawEnergyOrb(card, sb, $$);");
					}
				}
			};
		}

		@SuppressWarnings("unused")
		public static void drawEnergyOrb(AbstractCard card, SpriteBatch sb,
										 Texture texture,
										 float x, float y,
										 float originX, float originY,
										 float width, float height,
										 float scaleX, float scaleY,
										 float rotation,
										 int srcX, int srcY,
										 int srcWidth, int srcHeight,
										 boolean flipX, boolean flipY)
		{
            if (card instanceof CustomCard) {
                texture = ((CustomCard) card).getOrbLargeTexture();
            }

				if (texture == null) {
					texture = BaseMod.getEnergyOrbPortraitTexture(card.color);
					if (texture == null) {
						texture = ImageMaster.loadImage(BaseMod.getEnergyOrbPortrait(card.color));
						BaseMod.saveEnergyOrbPortraitTexture(card.color, texture);
					}
				}

				if (texture == null) {
					texture = ImageMaster.CARD_GRAY_ORB_L;
				}
			

			sb.draw(texture, x, y, originX, originY, width, height, scaleX, scaleY, rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
		}
	}
	
	@SpirePatch(
			clz=SingleCardViewPopup.class,
			method="renderCardBanner"
	)
	public static class BannerTexture
	{
		public static void Replace(Object __obj_instance, SpriteBatch sb)
		{
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
				if (bannerTexture == null) {
					if (rarity == AbstractCard.CardRarity.UNCOMMON) {
						bannerTexture = ImageMaster.CARD_BANNER_UNCOMMON_L;
					} else if (rarity == AbstractCard.CardRarity.RARE) {
						bannerTexture = ImageMaster.CARD_BANNER_RARE_L;
					} else {
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
