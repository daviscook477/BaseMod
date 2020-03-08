package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import basemod.abstracts.CustomCard;
import basemod.helpers.SuperclassFinder;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.cards.curses.Pride;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RenderFixSwitches
{
	@SpirePatch(
			clz=AbstractCard.class,
			method="renderBannerImage"
	)
	public static class RenderBannerSwitch
	{
		public static final Logger logger = LogManager.getLogger(BaseMod.class.getName());
		
		public static SpireReturn<?> Prefix(AbstractCard __instance, SpriteBatch sb, float drawX, float drawY)
		{
			//if it is not a custom card it cant possibly have the method getBannerSmallTexture force the normal rendering
			if (!(__instance instanceof CustomCard)) {
				return SpireReturn.Continue();
			}
			
			CustomCard card = (CustomCard) __instance;
			Texture texture = card.getBannerSmallTexture();
			if (texture == null) {
				return SpireReturn.Continue();
			}
			
			renderHelper(card, sb, Color.WHITE, new TextureAtlas.AtlasRegion(texture, 0, 0, texture.getWidth(), texture.getHeight()), drawX, drawY);
			
			return SpireReturn.Return(null);
		}
	}
	
	@SpirePatch(
			clz=AbstractCard.class,
			method="renderEnergy"
	)
	public static class RenderEnergySwitch
	{
		public static ExprEditor Instrument()
		{
			return new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException
				{
					if (m.getClassName().equals(AbstractCard.class.getName()) && m.getMethodName().equals("renderHelper")) {
						m.replace("{" +
								"$3 = " + RenderEnergySwitch.class.getName() + ".getEnergyOrb(this, $3);" +
								"$_ = $proceed($$);" +
								"}");
					}
				}
			};
		}

		@SpireInsertPatch(
                locator=Locator.class,
				localvars={"text", "font", "costColor"}
		)
		public static void Insert(AbstractCard __instance, SpriteBatch sb, String text, BitmapFont font, Color costColor)
		{
			if ((__instance.type == AbstractCard.CardType.STATUS && !__instance.cardID.equals(Slimed.ID))
				|| (__instance.color == CardColor.CURSE && !__instance.cardID.equals(Pride.ID))) {
				FontHelper.renderRotatedText(
						sb,
						font,
						text,
						__instance.current_x,
						__instance.current_y,
						-132 * __instance.drawScale * Settings.scale,
						192 * __instance.drawScale * Settings.scale,
						__instance.angle,
						false,
						costColor
				);
			}
		}

		@SuppressWarnings("unused")
		public static TextureAtlas.AtlasRegion getEnergyOrb(AbstractCard card, TextureAtlas.AtlasRegion orb)
		{
			if (!(card instanceof CustomCard)) {
				return orb;
			}

			CustomCard ccard = (CustomCard) card;

			Texture texture = ccard.getOrbSmallTexture();
			if (texture != null) {
				return new TextureAtlas.AtlasRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
			}

			Texture baseModTexture = BaseMod.getEnergyOrbTexture(card.color);
			if (baseModTexture != null) {
				return new TextureAtlas.AtlasRegion(baseModTexture, 0, 0, baseModTexture.getWidth(), baseModTexture.getHeight());
			}

			switch (card.color) {
				case BLUE:
					return ImageMaster.CARD_BLUE_ORB;
				case GREEN:
					return ImageMaster.CARD_GREEN_ORB;
				case RED:
					return ImageMaster.CARD_RED_ORB;
				case PURPLE:
					return ImageMaster.CARD_PURPLE_ORB;
				case COLORLESS:
				case CURSE:
					return ImageMaster.CARD_COLORLESS_ORB;
			}

			texture = ImageMaster.loadImage(BaseMod.getEnergyOrb(card.color));
			BaseMod.saveEnergyOrbTexture(card.color, texture);
			return new TextureAtlas.AtlasRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
		}

		private static class Locator extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws Exception
			{
				Matcher matcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "type");
				return LineFinder.findInOrder(ctBehavior, matcher);
			}
		}
	}
	
	@SpirePatch(
			clz=AbstractCard.class,
			method="renderOuterGlow"
	)
	public static class RenderOuterGlowSwitch
	{
		public static final Logger logger = LogManager.getLogger(BaseMod.class.getName());
		
		@SpireInsertPatch(rloc = 9)
		public static void Insert(Object __obj_instance, Object sbObj)
		{
			AbstractCard card = (AbstractCard) __obj_instance;
			CardColor color = card.color;
			SpriteBatch sb = (SpriteBatch) sbObj;
			if (!BaseMod.isBaseGameCardColor(color)) {
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
	
	@SpirePatch(
			clz=AbstractCard.class,
			method="renderCardBg"
	)
	public static class RenderBgSwitch
	{
		public static SpireReturn<?> Prefix(AbstractCard __instance, SpriteBatch sb, float xPos, float yPos)
		{
			if (!(__instance instanceof CustomCard)
					|| BaseMod.isBaseGameCardColor(__instance.color)
			) {
				return SpireReturn.Continue();
			}
			CardColor color = __instance.color;
			CustomCard card = (CustomCard) __instance;
			Texture texture = null;
			TextureAtlas.AtlasRegion region = null;
			
			switch (card.type) {
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
				region = ImageMaster.CARD_SKILL_BG_BLACK;
				break;
			}
			
			if (card.textureBackgroundSmallImg != null && !card.textureBackgroundSmallImg.isEmpty()) {
				texture = card.getBackgroundSmallTexture();
			}

			if (texture != null) {
				region = new TextureAtlas.AtlasRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
			}
			
			if (region == null) {
				BaseMod.logger.info(color.toString() + " texture is null wtf");
				return SpireReturn.Continue();
			}
			
			renderHelper(card, sb, Color.WHITE, region, xPos, yPos);
			
			return SpireReturn.Return(null);
		}
	}
	
	private static void renderHelper(AbstractCard card, SpriteBatch sb, Color color, TextureAtlas.AtlasRegion region, float xPos, float yPos)
	{
		try {
			// use reflection hacks to invoke renderHelper (without float scale)
			Method renderHelperMethod;
			Field renderColorField; 
			
			
			renderHelperMethod = SuperclassFinder.getSuperClassMethod(card.getClass(), "renderHelper", SpriteBatch.class, Color.class, TextureAtlas.AtlasRegion.class, float.class, float.class);
			renderHelperMethod.setAccessible(true);
			renderColorField = SuperclassFinder.getSuperclassField(card.getClass(), "renderColor");
			renderColorField.setAccessible(true);
			
				
			Color renderColor = (Color) renderColorField.get(card);
			renderHelperMethod.invoke(card, sb, renderColor, region, xPos, yPos);
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	private static void renderHelper(AbstractCard card, SpriteBatch sb, Color color, Texture texture, float xPos, float yPos, float scale)
	{
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
