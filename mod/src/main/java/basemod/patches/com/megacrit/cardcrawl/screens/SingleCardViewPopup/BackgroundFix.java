package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import basemod.BaseMod;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import java.lang.reflect.Field;

public class BackgroundFix
{
	@SpirePatch(
			clz=SingleCardViewPopup.class,
			method="renderCardBack"
	)
	public static class BackgroundTexture
	{
		public static void Prefix(Object __obj_instance, Object sbObject)
		{
			try {
				SingleCardViewPopup popup = (SingleCardViewPopup) __obj_instance;
				SpriteBatch sb = (SpriteBatch) sbObject;
				Field cardField;
				cardField = popup.getClass().getDeclaredField("card");
				cardField.setAccessible(true);
				AbstractCard card = (AbstractCard) cardField.get(popup);
				AbstractCard.CardColor color = card.color;
				if (!BaseMod.isBaseGameCardColor(color)) {
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
				}
			} catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
				e.printStackTrace();
			}
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
				public void edit(FieldAccess f) throws CannotCompileException
				{
					if (f.getFieldName().equals("CARD_GRAY_ORB_L")) {
						f.replace("$_ = " + EnergyOrbTexture.class.getName() + ".getEnergyOrb(card, $proceed($$));");
					}
				}
			};
		}

		@SuppressWarnings("unused")
		public static TextureAtlas.AtlasRegion getEnergyOrb(AbstractCard card, TextureAtlas.AtlasRegion orb)
		{
			if (card.color == AbstractCard.CardColor.COLORLESS) {
				return orb;
			}

			Texture texture = null;

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
				return orb;
			}

			return new TextureAtlas.AtlasRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
		}
	}
	
	@SpirePatch(
			clz=SingleCardViewPopup.class,
			method="renderCardBanner"
	)
	public static class BannerTexture
	{
		@SpireInsertPatch(
				locator=Locator.class,
				localvars={"card", "tmpImg"}
		)
		public static void Insert(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard card, @ByRef TextureAtlas.AtlasRegion[] tmpImg)
		{
			if (card instanceof CustomCard) {
				Texture bannerTexture = ((CustomCard)card).getBannerLargeTexture();
				if (bannerTexture != null) {
					tmpImg[0] = new TextureAtlas.AtlasRegion(bannerTexture, 0, 0, bannerTexture.getWidth(), bannerTexture.getHeight());
				}
			}
		}

		private static class Locator extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws Exception
			{
				Matcher matcher = new Matcher.MethodCallMatcher(SingleCardViewPopup.class, "renderHelper");
				int[] found = LineFinder.findInOrder(ctBehavior, matcher);
				return new int[]{found[0]-1};
			}
		}
	}
}
