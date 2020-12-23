package basemod.abstracts;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.helpers.BaseModCardTags;
import basemod.helpers.TooltipInfo;
import basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup.TitleFontSize;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CustomCard extends AbstractCard {
	public static HashMap<String, Texture> imgMap;
	
	public static final String PORTRAIT_ENDING = "_p";
	
	public static Texture getPortraitImage(CustomCard card) {
		return card.getPortraitImage();
	}
	
	private static void loadTextureFromString(String textureString) {
		if (!imgMap.containsKey(textureString)) {
			imgMap.put(textureString, ImageMaster.loadImage(textureString));
		}
	}
	
	private static Texture getTextureFromString(String textureString) {
		loadTextureFromString(textureString);
		return imgMap.get(textureString);
	}

	@Override
	public AbstractCard makeCopy() {
		try{
			return this.getClass().newInstance();
		}catch(InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("BaseMod failed to auto-generate makeCopy for card: " + cardID);
		}
	}

	static {
		imgMap = new HashMap<>();
	}

	
	public String textureImg;
	public String textureOrbSmallImg = null;
	public String textureOrbLargeImg = null;
	public String textureBackgroundSmallImg = null;
	public String textureBackgroundLargeImg = null;
	public String textureBannerSmallImg = null;
	public String textureBannerLargeImg = null;

	public AtlasRegion bannerSmallRegion = null;
	public AtlasRegion bannerLargeRegion = null;

	public AtlasRegion frameSmallRegion = null;
	public AtlasRegion frameLargeRegion = null;

	//dynamic frame parts
	public AtlasRegion frameMiddleRegion = null;
	public AtlasRegion frameLeftRegion = null;
	public AtlasRegion frameRightRegion = null;

	public AtlasRegion frameMiddleLargeRegion = null;
	public AtlasRegion frameLeftLargeRegion = null;
	public AtlasRegion frameRightLargeRegion = null;

	private static Map<Class<? extends CustomCard>, BitmapFont> titleFontMap = new HashMap<>();

	public CustomCard(String id, String name, String img, int cost, String rawDescription, CardType type, CardColor color, CardRarity rarity, CardTarget target) {
		super(id, name, "status/beta", "status/beta", cost, rawDescription, type, color, rarity, target);
		
		this.textureImg = img;
		if (img != null) {
			loadCardImage(img);
		}
	}

	public CustomCard(String id, String name, RegionName img, int cost, String rawDescription, CardType type, CardColor color, CardRarity rarity, CardTarget target) {
		super(id, name, "status/beta", img.name, cost, rawDescription, type, color, rarity, target);
	}
	
	// 
	// per card energy orb functionality
	//
	
	public Texture getOrbSmallTexture() {
		if (textureOrbSmallImg == null) {
			return BaseMod.getEnergyOrbTexture(this.color);
		}
		
		return getTextureFromString(textureOrbSmallImg);
	}
	
	public Texture getOrbLargeTexture() {
		if (textureOrbLargeImg == null) {
			return BaseMod.getEnergyOrbPortraitTexture(this.color);
		}
		
		return getTextureFromString(textureOrbLargeImg);
	}
	
	public void setOrbTexture(String orbSmallImg, String orbLargeImg) {
		this.textureOrbSmallImg = orbSmallImg;
		this.textureOrbLargeImg = orbLargeImg;
		
		loadTextureFromString(orbSmallImg);
		loadTextureFromString(orbLargeImg);
	}
	
	//
	// per card background functionality
	//
	
	public Texture getBackgroundSmallTexture() {
		if (textureBackgroundSmallImg == null) {
			switch (this.type) {
			case ATTACK:
				return BaseMod.getAttackBgTexture(this.color);
			case POWER:
				return BaseMod.getPowerBgTexture(this.color);
			default:
				return BaseMod.getSkillBgTexture(this.color);
			}
		}
		
		return getTextureFromString(textureBackgroundSmallImg);
	}

	public Texture getBackgroundLargeTexture() {
		if (textureBackgroundLargeImg == null) {
			switch (this.type) {
			case ATTACK:
				return BaseMod.getAttackBgPortraitTexture(this.color);
			case POWER:
				return BaseMod.getPowerBgPortraitTexture(this.color);
			default:
				return BaseMod.getSkillBgPortraitTexture(this.color);
			}
		}
		
		return getTextureFromString(textureBackgroundLargeImg);
	}

	public void setBackgroundTexture(String backgroundSmallImg, String backgroundLargeImg) {
		this.textureBackgroundSmallImg = backgroundSmallImg;
		this.textureBackgroundLargeImg = backgroundLargeImg;
		
		loadTextureFromString(backgroundSmallImg);
		loadTextureFromString(backgroundLargeImg);
	}
	
	//
	// per card banner functionality
	//
	public Texture getBannerSmallTexture() {
		if (textureBannerSmallImg == null) {
			return null;
		}
		
		return getTextureFromString(textureBannerSmallImg);
	}

	public Texture getBannerLargeTexture() {
		if (textureBannerLargeImg == null) {
			return null;
		}
		
		return getTextureFromString(textureBannerLargeImg);
	}

	//Region versions, to make it easier to use basegame regions defined in ImageMaster
	public AtlasRegion getBannerSmallRegion() {
		if (bannerSmallRegion == null && textureBannerSmallImg != null)
		{
			Texture t = getBannerSmallTexture();
			bannerSmallRegion = new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
		}
		return bannerSmallRegion;
	}

	public AtlasRegion getBannerLargeRegion() {
		if (bannerLargeRegion == null && textureBannerLargeImg != null)
		{
			Texture t = getBannerLargeTexture();
			bannerLargeRegion = new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
		}
		return bannerLargeRegion;
	}

	public void setBannerTexture(String bannerSmallImg, String bannerLargeImg) {
		this.textureBannerSmallImg = bannerSmallImg;
		this.textureBannerLargeImg = bannerLargeImg;
		
		loadTextureFromString(bannerSmallImg);
		loadTextureFromString(bannerLargeImg);

		Texture t = getBannerSmallTexture();
		this.bannerSmallRegion = new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
		t = getBannerLargeTexture();
		this.bannerLargeRegion = new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
	}

	public void setPortraitTextures(String cardFrameSmall, String cardFrameLarge)
	{
		loadTextureFromString(cardFrameSmall);
		loadTextureFromString(cardFrameLarge);

		Texture t = getTextureFromString(cardFrameSmall);
		this.frameSmallRegion = new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
		t = getTextureFromString(cardFrameLarge);
		this.frameLargeRegion = new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
	}

	public void setPortraitTextures(String cardFrameSmall, String cardFrameLarge, String dynamicLeftFrame, String dynamicMiddleFrame, String dynamicRightFrame, String dynamicLeftFrameLarge, String dynamicMiddleFrameLarge, String dynamicRightFrameLarge)
	{
		loadTextureFromString(cardFrameSmall);
		loadTextureFromString(cardFrameLarge);
		loadTextureFromString(dynamicLeftFrame);
		loadTextureFromString(dynamicMiddleFrame);
		loadTextureFromString(dynamicRightFrame);
		loadTextureFromString(dynamicLeftFrameLarge);
		loadTextureFromString(dynamicMiddleFrameLarge);
		loadTextureFromString(dynamicRightFrameLarge);

		Texture t = getTextureFromString(cardFrameSmall);
		this.frameSmallRegion = new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
		t = getTextureFromString(cardFrameLarge);
		this.frameLargeRegion = new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
		t = getTextureFromString(dynamicLeftFrame);
		this.frameLeftRegion = new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
		t = getTextureFromString(dynamicMiddleFrame);
		this.frameMiddleRegion = new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
		t = getTextureFromString(dynamicRightFrame);
		this.frameRightRegion = new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
		t = getTextureFromString(dynamicLeftFrameLarge);
		this.frameLeftLargeRegion = new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
		t = getTextureFromString(dynamicMiddleFrameLarge);
		this.frameMiddleLargeRegion = new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
		t = getTextureFromString(dynamicRightFrameLarge);
		this.frameRightLargeRegion = new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
	}

	public void setDisplayRarity(CardRarity rarity)
	{
		switch (rarity)
		{
			case BASIC:
			case CURSE:
			case SPECIAL:
			case COMMON:
				this.bannerSmallRegion = ImageMaster.CARD_BANNER_COMMON;
				this.bannerLargeRegion = ImageMaster.CARD_BANNER_COMMON_L;

				switch (type)
				{
					case ATTACK:
						this.frameSmallRegion = ImageMaster.CARD_FRAME_ATTACK_COMMON;
						this.frameLargeRegion = ImageMaster.CARD_FRAME_ATTACK_COMMON_L;
						break;
					case POWER:
						this.frameSmallRegion = ImageMaster.CARD_FRAME_POWER_COMMON;
						this.frameLargeRegion = ImageMaster.CARD_FRAME_POWER_COMMON_L;
						break;
					default:
						this.frameSmallRegion = ImageMaster.CARD_FRAME_SKILL_COMMON;
						this.frameLargeRegion = ImageMaster.CARD_FRAME_SKILL_COMMON_L;
						break;
				}
				this.frameMiddleRegion = ImageMaster.CARD_COMMON_FRAME_MID;
				this.frameLeftRegion = ImageMaster.CARD_COMMON_FRAME_LEFT;
				this.frameRightRegion = ImageMaster.CARD_COMMON_FRAME_RIGHT;

				this.frameMiddleLargeRegion = ImageMaster.CARD_COMMON_FRAME_MID_L;
				this.frameLeftLargeRegion = ImageMaster.CARD_COMMON_FRAME_LEFT_L;
				this.frameRightLargeRegion = ImageMaster.CARD_COMMON_FRAME_RIGHT_L;
				break;
			case UNCOMMON:
				this.bannerSmallRegion = ImageMaster.CARD_BANNER_UNCOMMON;
				this.bannerLargeRegion = ImageMaster.CARD_BANNER_UNCOMMON_L;

				switch (type)
				{
					case ATTACK:
						this.frameSmallRegion = ImageMaster.CARD_FRAME_ATTACK_UNCOMMON;
						this.frameLargeRegion = ImageMaster.CARD_FRAME_ATTACK_UNCOMMON_L;
						break;
					case POWER:
						this.frameSmallRegion = ImageMaster.CARD_FRAME_POWER_UNCOMMON;
						this.frameLargeRegion = ImageMaster.CARD_FRAME_POWER_UNCOMMON_L;
						break;
					default:
						this.frameSmallRegion = ImageMaster.CARD_FRAME_SKILL_UNCOMMON;
						this.frameLargeRegion = ImageMaster.CARD_FRAME_SKILL_UNCOMMON_L;
						break;
				}
				this.frameMiddleRegion = ImageMaster.CARD_UNCOMMON_FRAME_MID;
				this.frameLeftRegion = ImageMaster.CARD_UNCOMMON_FRAME_LEFT;
				this.frameRightRegion = ImageMaster.CARD_UNCOMMON_FRAME_RIGHT;

				this.frameMiddleLargeRegion = ImageMaster.CARD_UNCOMMON_FRAME_MID_L;
				this.frameLeftLargeRegion = ImageMaster.CARD_UNCOMMON_FRAME_LEFT_L;
				this.frameRightLargeRegion = ImageMaster.CARD_UNCOMMON_FRAME_RIGHT_L;
				break;
			case RARE:
				this.bannerSmallRegion = ImageMaster.CARD_BANNER_RARE;
				this.bannerLargeRegion = ImageMaster.CARD_BANNER_RARE_L;

				switch (type)
				{
					case ATTACK:
						this.frameSmallRegion = ImageMaster.CARD_FRAME_ATTACK_RARE;
						this.frameLargeRegion = ImageMaster.CARD_FRAME_ATTACK_RARE_L;
						break;
					case POWER:
						this.frameSmallRegion = ImageMaster.CARD_FRAME_POWER_RARE;
						this.frameLargeRegion = ImageMaster.CARD_FRAME_POWER_RARE_L;
						break;
					default:
						this.frameSmallRegion = ImageMaster.CARD_FRAME_SKILL_RARE;
						this.frameLargeRegion = ImageMaster.CARD_FRAME_SKILL_RARE_L;
						break;
				}
				this.frameMiddleRegion = ImageMaster.CARD_RARE_FRAME_MID;
				this.frameLeftRegion = ImageMaster.CARD_RARE_FRAME_LEFT;
				this.frameRightRegion = ImageMaster.CARD_RARE_FRAME_RIGHT;

				this.frameMiddleLargeRegion = ImageMaster.CARD_RARE_FRAME_MID_L;
				this.frameLeftLargeRegion = ImageMaster.CARD_RARE_FRAME_LEFT_L;
				this.frameRightLargeRegion = ImageMaster.CARD_RARE_FRAME_RIGHT_L;
				break;
			default:
				System.out.println("Attempted to set display rarity to an unknown rarity: " + rarity.name());
		}
	}
	
	/**
	 * To be overriden in subclasses if they want to manually modify their card's damage
	 * like PerfectedStrike or HeavyBlade before any other powers get to modify the damage
	 * 
	 * default implementation does nothing
	 * @param player the player that is casting this card
	 * @param mo the monster that this card is targetting (may be null, check for this.isMultiTarget)
	 * @param tmp the current damage amount
	 * @return the current damage amount modified however you want
	 */
	public float calculateModifiedCardDamage(AbstractPlayer player, AbstractMonster mo, float tmp) {
		return tmp;
	}
	
	/*
	 * Same as above but without the monster - this would be used when the card needs
	 * to calculate damage to display while it's in the player's hand but not targetting anything
	 */
	public float calculateModifiedCardDamage(AbstractPlayer player, float tmp) {
		return calculateModifiedCardDamage(player, null, tmp);
	}
	
	// loadCardImage - copy of hack here: https://github.com/t-larson/STS-ModLoader/blob/master/modloader/CustomCard.java
	public void loadCardImage(String img) {
		Texture cardTexture;
		if (imgMap.containsKey(img)) {
			cardTexture = imgMap.get(img);
		} else {
			cardTexture = ImageMaster.loadImage(img);
			imgMap.put(img, cardTexture);
		}
		cardTexture.setFilter(Texture.TextureFilter.Linear,  Texture.TextureFilter.Linear);
		int tw = cardTexture.getWidth();
		int th = cardTexture.getHeight();
		TextureAtlas.AtlasRegion cardImg = new AtlasRegion(cardTexture, 0, 0, tw, th);
		ReflectionHacks.setPrivateInherited(this, CustomCard.class, "portrait", cardImg);
	}

	public List<TooltipInfo> getCustomTooltips()
	{
		return null;
	}

	public List<TooltipInfo> getCustomTooltipsTop()
	{
		return null;
	}
	
	//
	// For events that care about Strikes and Defends
	//

	@Deprecated
	public boolean isStrike() {
		return hasTag(BaseModCardTags.BASIC_STRIKE);
	}

	@Deprecated
	public boolean isDefend() {
		return hasTag(BaseModCardTags.BASIC_DEFEND);
	}

	@Override
	public void unlock() {
		this.isLocked = false;
	}

	public float getTitleFontSize() {
		return -1;
	}

	private BitmapFont getTitleFont() {
		if (getTitleFontSize() < 0) {
			return null;
		}

		BitmapFont font = titleFontMap.get(getClass());
		if (font == null) {
			font = generateTitleFont(getTitleFontSize());
			titleFontMap.put(getClass(), font);
		}
		return font;
	}

	@SpireOverride
	protected void renderTitle(SpriteBatch sb) {
		BitmapFont titleFont = getTitleFont();
		if (titleFont == null) {
			SpireSuper.call(sb);
			return;
		}

		BitmapFont savedFont = FontHelper.cardTitleFont;
		FontHelper.cardTitleFont = titleFont;
		Boolean useSmallTitleFont = ReflectionHacks.getPrivate(this, AbstractCard.class, "useSmallTitleFont");
		ReflectionHacks.setPrivate(this, AbstractCard.class, "useSmallTitleFont", false);
		SpireSuper.call(sb);
		ReflectionHacks.setPrivate(this, AbstractCard.class, "useSmallTitleFont", useSmallTitleFont);
		FontHelper.cardTitleFont = savedFont;
	}

	private static BitmapFont generateTitleFont(float size) {
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.minFilter = Texture.TextureFilter.Linear;
		param.magFilter = Texture.TextureFilter.Linear;
		param.hinting = FreeTypeFontGenerator.Hinting.Slight;
		param.spaceX = 0;
		param.kerning = true;
		param.borderColor = new Color(0.35f, 0.35f, 0.35f, 1);
		param.borderWidth = 2.25f * Settings.scale;
		param.gamma = 0.9f;
		param.borderGamma = 0.9f;
		param.shadowColor = new Color(0, 0, 0, 0.25f);
		param.shadowOffsetX = Math.round(3 * Settings.scale);
		param.shadowOffsetY = Math.round(3 * Settings.scale);
		param.borderStraight = false;

		param.characters = "";
		param.incremental = true;
		param.size = Math.round(size * Settings.scale);

		FreeTypeFontGenerator g = new FreeTypeFontGenerator(TitleFontSize.fontFile);
		g.scaleForPixelHeight(param.size);

		BitmapFont font = g.generateFont(param);
		font.setUseIntegerPositions(false);
		font.getData().markupEnabled = true;
		if (LocalizedStrings.break_chars != null) {
			font.getData().breakChars = LocalizedStrings.break_chars.toCharArray();
		}

		return font;
	}

	public List<String> getCardDescriptors() {
		return Collections.emptyList();
	}

	protected Texture getPortraitImage() {
		if (textureImg == null) {
			return null;
		}
		int endingIndex = textureImg.lastIndexOf(".");
		String newPath = textureImg.substring(0, endingIndex) +
				PORTRAIT_ENDING + textureImg.substring(endingIndex);
		System.out.println("Finding texture: " + newPath);
		Texture portraitTexture;
		try {
			portraitTexture = ImageMaster.loadImage(newPath);
		} catch (Exception e) {
			portraitTexture = null;
		}
		return portraitTexture;
	}

	public static class RegionName {
		public final String name;

		public RegionName(String name) {
			this.name = name;
		}
	}
}
