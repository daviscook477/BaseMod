package basemod.abstracts;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.helpers.BaseModCardTags;
import basemod.helpers.TooltipInfo;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.HashMap;
import java.util.List;

public abstract class CustomCard extends AbstractCard {
	
	public static HashMap<String, Texture> imgMap;
	
	public static final String PORTRAIT_ENDING = "_p";
	
	public static Texture getPortraitImage(CustomCard card) {
		if (card.textureImg == null) {
			return null;
		}
		int endingIndex = card.textureImg.lastIndexOf(".");
		String newPath = card.textureImg.substring(0, endingIndex) + 
				PORTRAIT_ENDING + card.textureImg.substring(endingIndex); 
		System.out.println("Finding texture: " + newPath);
		Texture portraitTexture;
		try {
			portraitTexture = ImageMaster.loadImage(newPath);
		} catch (Exception e) {
			portraitTexture = null;
		}
		return portraitTexture;
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

	public CustomCard(String id, String name, String img, int cost, String rawDescription, CardType type, CardColor color, CardRarity rarity, CardTarget target) {
		super(id, name, "status/beta", "status/beta", cost, rawDescription, type, color, rarity, target);
		
		this.textureImg = img;
		if (img != null) {
			loadCardImage(img);
		}
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
		if(textureBannerSmallImg == null) {
			return null;
		}
		
		return getTextureFromString(textureBannerSmallImg);
	}
	
	public Texture getBannerLargeTexture() {
		if(textureBannerLargeImg == null) {
			return null;
		}
		
		return getTextureFromString(textureBannerLargeImg);
	}
	
	public void setBannerTexture(String bannerSmallImg, String bannerLargeImg) {
		this.textureBannerSmallImg = bannerSmallImg;
		this.textureBannerLargeImg = bannerLargeImg;
		
		loadTextureFromString(bannerSmallImg);
		loadTextureFromString(bannerLargeImg);
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
	
	//
	// For events that care about Strikes and Defends
	//
	
	public boolean isStrike() {
		return hasTag(BaseModCardTags.BASIC_STRIKE);
	}
	
	public boolean isDefend() {
		return hasTag(BaseModCardTags.BASIC_DEFEND);
	}
}
