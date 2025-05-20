package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.RenderCardDescriptors.getAllDescriptors;

public class RenderFixSwitches
{
	@SpirePatch(
			clz=AbstractCard.class,
			method="renderBannerImage"
	)
	public static class RenderBannerSwitch
	{
		public static SpireReturn<?> Prefix(AbstractCard __instance, SpriteBatch sb, float drawX, float drawY, Color ___renderColor)
		{
			//If it is not a custom card it cant possibly have the method getBannerSmallRegion, so use normal rendering
			if (!(__instance instanceof CustomCard)) {
				return SpireReturn.Continue();
			}

			CustomCard card = (CustomCard) __instance;
			TextureAtlas.AtlasRegion region = card.getBannerSmallRegion();
			if (region == null) {
				return SpireReturn.Continue();
			}

			renderHelper(card, sb, ___renderColor, region, drawX, drawY);

			return SpireReturn.Return(null);
		}
	}

	/*
		4 possibilities
		no frame, no descriptor - normal rendering
		custom frame, no descriptor - custom rendering
		custom frame, descriptor - custom rendering
		no frame, descriptor - ....custom rendering?
	 */
	@SpirePatch(
			clz=AbstractCard.class,
			method="renderPortraitFrame"
	)
	public static class RenderPortraitFrameSwitch
	{
		public static SpireReturn<?> Prefix(AbstractCard __instance, SpriteBatch sb, float x, float y, Color ___renderColor)
		{
			//If it's not a CustomCard, no custom rendering
			if (!(__instance instanceof CustomCard)) {
				return SpireReturn.Continue();
			}

			List<String> descriptors = getAllDescriptors(__instance);

			CustomCard card = (CustomCard) __instance;
			TextureAtlas.AtlasRegion frame = card.frameSmallRegion,
					frameMid = card.frameMiddleRegion,
					frameLeft = card.frameLeftRegion,
					frameRight = card.frameRightRegion;

			if (frame == null) {
				if (descriptors.isEmpty()) {
					return SpireReturn.Continue();
				}

				switch (card.rarity) {
					case UNCOMMON:
						if (frameMid == null) {
							frameMid = ImageMaster.CARD_UNCOMMON_FRAME_MID;
							frameLeft = ImageMaster.CARD_UNCOMMON_FRAME_LEFT;
							frameRight = ImageMaster.CARD_UNCOMMON_FRAME_RIGHT;
						}
						switch (card.type) {
							case ATTACK:
								frame = ImageMaster.CARD_FRAME_ATTACK_UNCOMMON;
								break;
							case POWER:
								frame = ImageMaster.CARD_FRAME_POWER_UNCOMMON;
								break;
							default:
								frame = ImageMaster.CARD_FRAME_SKILL_UNCOMMON;
								break;
						}
						break;
					case RARE:
						if (frameMid == null) {
							frameMid = ImageMaster.CARD_RARE_FRAME_MID;
							frameLeft = ImageMaster.CARD_RARE_FRAME_LEFT;
							frameRight = ImageMaster.CARD_RARE_FRAME_RIGHT;
						}
						switch (card.type) {
							case ATTACK:
								frame = ImageMaster.CARD_FRAME_ATTACK_RARE;
								break;
							case POWER:
								frame = ImageMaster.CARD_FRAME_POWER_RARE;
								break;
							default:
								frame = ImageMaster.CARD_FRAME_SKILL_RARE;
								break;
						}
						break;
					default:
						if (frameMid == null) {
							frameMid = ImageMaster.CARD_COMMON_FRAME_MID;
							frameLeft = ImageMaster.CARD_COMMON_FRAME_LEFT;
							frameRight = ImageMaster.CARD_COMMON_FRAME_RIGHT;
						}
						switch (card.type) {
							case ATTACK:
								frame = ImageMaster.CARD_FRAME_ATTACK_COMMON;
								break;
							case POWER:
								frame = ImageMaster.CARD_FRAME_POWER_COMMON;
								break;
							default:
								frame = ImageMaster.CARD_FRAME_SKILL_COMMON;
								break;
						}
						break;
				}
			}


			float tWidth = 0;
			float tOffset = 0;

			switch (card.type)
			{
				case ATTACK:
					descriptors.add(0, AbstractCard.TEXT[0]);
					tWidth = AbstractCard.typeWidthAttack;
					tOffset = AbstractCard.typeOffsetAttack;
					break;
				case SKILL:
					descriptors.add(0, AbstractCard.TEXT[1]);
					tWidth = AbstractCard.typeWidthSkill;
					tOffset = AbstractCard.typeOffsetSkill;
					break;
				case POWER:
					descriptors.add(0, AbstractCard.TEXT[2]);
					tWidth = AbstractCard.typeWidthPower;
					tOffset = AbstractCard.typeOffsetPower;
					break;
				case CURSE:
					descriptors.add(0, AbstractCard.TEXT[3]);
					tWidth = AbstractCard.typeWidthCurse;
					tOffset = AbstractCard.typeOffsetCurse;
					break;
				case STATUS:
					descriptors.add(0, AbstractCard.TEXT[7]);
					tWidth = AbstractCard.typeWidthStatus;
					tOffset = AbstractCard.typeOffsetStatus;
					break;
				default:
					descriptors.add(0, AbstractCard.TEXT[5]);
					break;
			}

			renderHelper(card, sb, ___renderColor, frame, x, y);

			if (descriptors.size() > 1) {
				String text = String.join(RenderCardDescriptors.SEPARATOR, descriptors);
				GlyphLayout gl = new GlyphLayout();
				FontHelper.cardTypeFont.getData().setScale(1f);
				gl.setText(FontHelper.cardTypeFont, text);
				tOffset = (gl.width - 38 * Settings.scale) / 2f;
				tWidth = (gl.width - 0f) / (32 * Settings.scale);
			}

			if (tWidth > 1.1f && frameMid != null) {
				dynamicFrameRenderHelper(sb, frameMid, x, y, 0.0F, __instance.drawScale, __instance.angle, tWidth);
				dynamicFrameRenderHelper(sb, frameLeft, x, y, -tOffset, __instance.drawScale, __instance.angle, 1.0F);
				dynamicFrameRenderHelper(sb, frameRight, x, y, tOffset, __instance.drawScale, __instance.angle, 1.0F);
			}

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

	//As far as I can tell, this method isn't actually used anymore, but the old version would attempt to render a null texture if it ever was called, so I decided to clean it up anyways.
	@SpirePatch(
			clz=AbstractCard.class,
			method="renderOuterGlow"
	)
	public static class RenderOuterGlowSwitch
	{
		public static final Logger logger = LogManager.getLogger(BaseMod.class.getName());
		
		@SpireInsertPatch(rloc = 9)
		public static void Insert(AbstractCard __instance, SpriteBatch sb, float ___current_x, float ___current_y, Color ___tintColor)
		{
			if (!BaseMod.isBaseGameCardColor(__instance.color)) {
				Color glowColor = BaseMod.getGlowColor(__instance.color);
				if (glowColor == null) {
					glowColor = Color.WHITE;
				}
				renderHelper(__instance, sb, glowColor, __instance.getCardBgAtlas(), ___current_x - 256.0f, ___current_y - 256.0f, 1.0F + ___tintColor.a / 5.0f);
			}
		}
	}
	
	@SpirePatch(
			clz=AbstractCard.class,
			method="renderCardBg"
	)
	public static class RenderBgSwitch
	{
		public static SpireReturn<?> Prefix(AbstractCard __instance, SpriteBatch sb, float xPos, float yPos, Color ___renderColor)
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


			if (card.textureBackgroundSmallImg != null && !card.textureBackgroundSmallImg.isEmpty()) {
				texture = card.getBackgroundSmallTexture();
			}
			else
			{
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
			}

			if (texture != null) {
				region = new TextureAtlas.AtlasRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
			}
			
			if (region == null) {
				BaseMod.logger.info(color.toString() + " texture is null wtf");
				return SpireReturn.Continue();
			}
			
			renderHelper(card, sb, ___renderColor, region, xPos, yPos);
			
			return SpireReturn.Return(null);
		}
	}


	//renderHelper usability
	private static Method renderHelperMethod;
	private static Method renderHelperMethodWithScale;

	static
	{
		try
		{
			renderHelperMethod = AbstractCard.class.getDeclaredMethod("renderHelper", SpriteBatch.class, Color.class, TextureAtlas.AtlasRegion.class, float.class, float.class);
			renderHelperMethod.setAccessible(true);
			renderHelperMethodWithScale = AbstractCard.class.getDeclaredMethod("renderHelper", SpriteBatch.class, Color.class, TextureAtlas.AtlasRegion.class, float.class, float.class, float.class);
			renderHelperMethodWithScale.setAccessible(true);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	private static void renderHelper(AbstractCard card, SpriteBatch sb, Color color, TextureAtlas.AtlasRegion region, float xPos, float yPos)
	{
		try {
			// use reflection hacks to invoke renderHelper (without float scale)
			renderHelperMethod.invoke(card, sb, color, region, xPos, yPos);
		} catch (IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	private static void renderHelper(AbstractCard card, SpriteBatch sb, Color color, TextureAtlas.AtlasRegion region, float xPos, float yPos, float scale)
	{
		try {
			// use reflection hacks to invoke renderHelper (without float scale)
			renderHelperMethodWithScale.invoke(card, sb, color, region, xPos, yPos, scale);
		} catch (IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static void dynamicFrameRenderHelper(SpriteBatch sb, TextureAtlas.AtlasRegion img, float x, float y, float xOffset, float drawScale, float angle, float xScale) {
		Vector2 tmp = new Vector2(0,0);
		tmp.set(xOffset, 0);
		tmp.scl(drawScale);
		tmp.rotate(angle);
		sb.draw(
				img,
				x + img.offsetX - (img.originalWidth / 2f + 1) + tmp.x,
				y + img.offsetY - img.originalHeight / 2f + tmp.y,
				img.originalWidth / 2f - img.offsetX + 1,
				img.originalHeight / 2f - img.offsetY,
				img.packedWidth,
				img.packedHeight,
				Settings.scale * drawScale * xScale,
				Settings.scale * drawScale,
				angle
		);
	}
}
