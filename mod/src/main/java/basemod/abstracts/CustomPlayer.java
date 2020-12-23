package basemod.abstracts;

import basemod.BaseMod;
import basemod.animations.AbstractAnimation;
import basemod.animations.G3DJAnimation;
import basemod.animations.SpineAnimation;
import basemod.interfaces.ModelRenderSubscriber;
import basemod.patches.com.megacrit.cardcrawl.unlock.UnlockTracker.CountModdedUnlockCards;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.cutscenes.CutscenePanel;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.helpers.SaveHelper;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;
import com.megacrit.cardcrawl.ui.panels.energyorb.EnergyOrbInterface;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class CustomPlayer extends AbstractPlayer implements ModelRenderSubscriber
{
	private static final Logger logger = LogManager.getLogger(CustomPlayer.class.getName());

	protected AbstractAnimation animation;

	protected EnergyOrbInterface energyOrb;
	protected Prefs prefs;
	protected CharStat charStat;

	public CustomPlayer(String name, PlayerClass playerClass, String[] orbTextures, String orbVfxPath,
			String model, String animation) {
		this(name, playerClass, orbTextures, orbVfxPath, null, model, animation);
	}

	public CustomPlayer(String name, PlayerClass playerClass, EnergyOrbInterface energyOrbInterface,
						String model, String animation) {
		this(name, playerClass, energyOrbInterface, new G3DJAnimation(model, animation));
	}
	
	public CustomPlayer(String name, PlayerClass playerClass, String[] orbTextures, String orbVfxPath, float[] layerSpeeds,
			String model, String animation) {
		this(name, playerClass, orbTextures, orbVfxPath, layerSpeeds, new G3DJAnimation(model, animation));
	}

	public CustomPlayer(String name, PlayerClass playerClass, String[] orbTextures, String orbVfxPath, AbstractAnimation animation) {
		this(name, playerClass, orbTextures, orbVfxPath, null, animation);
	}

	public CustomPlayer(String name, PlayerClass playerClass, String[] orbTextures, String orbVfxPath, float[] layerSpeeds,
						AbstractAnimation animation) {
		this(name, playerClass, new CustomEnergyOrb(orbTextures, orbVfxPath, layerSpeeds), animation);
	}

	public CustomPlayer(String name, PlayerClass playerClass, EnergyOrbInterface energyOrbInterface, AbstractAnimation animation) {
		super(name, playerClass);

		energyOrb = energyOrbInterface;
		charStat = new CharStat(this);
		
		this.dialogX = (this.drawX + 0.0F * Settings.scale);
		this.dialogY = (this.drawY + 220.0F * Settings.scale);

		this.animation = animation;

		if (animation instanceof SpineAnimation) {
			SpineAnimation spine = (SpineAnimation) animation;
			loadAnimation(spine.atlasUrl, spine.skeletonUrl, spine.scale);
		}

		if (animation.type() != AbstractAnimation.Type.NONE) {
			this.atlas = new TextureAtlas();
		}
		
		if (animation.type() == AbstractAnimation.Type.MODEL) {
			BaseMod.subscribe(this);
		}
	}

	@Override
	public void receiveModelRender(ModelBatch batch, Environment env) {
		if (this != AbstractDungeon.player) {
			BaseMod.unsubscribeLater(this);
		} else {
			animation.renderModel(batch, env);
		}
	}

	@Override
	public void renderPlayerImage(SpriteBatch sb) {
		switch (animation.type()) {
			case NONE:
				super.renderPlayerImage(sb);
				break;
			case MODEL:
				BaseMod.publishAnimationRender(sb);
				break;
			case SPRITE:
				animation.setFlip(flipHorizontal, flipVertical);
				animation.renderSprite(sb, drawX + animX, drawY + animY + AbstractDungeon.sceneOffsetY);
				break;
		}
	}

	@Override
	public String getAchievementKey()
	{
		// Returning null crashes the game, hopefully returning a fake key won't
		return "MODDING";
	}

	@Override
	public ArrayList<AbstractCard> getCardPool(ArrayList<AbstractCard> tmpPool)
	{
		AbstractCard.CardColor color = getCardColor();
		for (Map.Entry<String, AbstractCard> c : CardLibrary.cards.entrySet()) {
			AbstractCard card = c.getValue();
			if (card.color.equals(color) && card.rarity != AbstractCard.CardRarity.BASIC &&
					(!UnlockTracker.isCardLocked(c.getKey()) || Settings.isDailyRun)) {
				tmpPool.add(card);
			}
		}
		return tmpPool;
	}

	@Override
	public String getLeaderboardCharacterName()
	{
		// This is never called
		// The one place it's called is gated behind an isModded check
		return null;
	}

	@Override
	public Texture getEnergyImage()
	{
		if (energyOrb instanceof CustomEnergyOrb) {
			return ((CustomEnergyOrb) energyOrb).getEnergyImage();
		}
		throw new RuntimeException();
	}

	@Override
	public TextureAtlas.AtlasRegion getOrb()
	{
		return BaseMod.getCardEnergyOrbAtlasRegion(getCardColor());
	}

	@Override
	public void renderOrb(SpriteBatch sb, boolean enabled, float current_x, float current_y)
	{
		energyOrb.renderOrb(sb, enabled, current_x, current_y);
	}

	@Override
	public void updateOrb(int energyCount)
	{
		energyOrb.updateOrb(energyCount);
	}

	@Override
	public String getSaveFilePath()
	{
		return SaveAndContinue.getPlayerSavePath(chosenClass);
	}

	@Override
	public Prefs getPrefs()
	{
		if (prefs == null) {
			logger.error("prefs need to be initialized first!");
		}
		return prefs;
	}

	@Override
	public void loadPrefs()
	{
		if (prefs == null) {
			prefs = SaveHelper.getPrefs(chosenClass.name());
		}
	}

	@Override
	public CharStat getCharStat()
	{
		return charStat;
	}

	@Override
	public int getUnlockedCardCount()
	{
		return CountModdedUnlockCards.getUnlockedCardCount(chosenClass);
	}

	@Override
	public int getSeenCardCount()
	{
		return BaseMod.getSeenCardCount(getCardColor());
	}

	@Override
	public int getCardCount()
	{
		return BaseMod.getCardCount(getCardColor());
	}

	@Override
	public boolean saveFileExists()
	{
		return SaveAndContinue.saveExistsAndNotCorrupted(this);
	}

	@Override
	public String getWinStreakKey()
	{
		// The only places this is called then pass it to Steam integration
		// I'm uncertain what Steam will do with these unofficial keys
		return "win_streak_" + chosenClass.name();
	}

	@Override
	public String getLeaderboardWinStreakKey()
	{
		// This is never called
		// The one place it's called is gated behind an isModded check
		return chosenClass.name() + "_CONSECUTIVE_WINS";
	}

	@Override
	public void renderStatScreen(SpriteBatch sb, float screenX, float screenY)
	{
		StatsScreen.renderHeader(sb, BaseMod.colorString(getLocalizedCharacterName(), "#" + getCardRenderColor().toString()), screenX, screenY);
		getCharStat().render(sb, screenX, screenY);
	}

	@Override
	public Texture getCustomModeCharacterButtonImage()
	{
		if (BaseMod.getCustomModePlayerButton(chosenClass) != null) {
			return ImageMaster.loadImage(BaseMod.getCustomModePlayerButton(chosenClass));
		} else {
			Pixmap pixmap = new Pixmap(Gdx.files.internal(BaseMod.getPlayerButton(chosenClass)));
			Pixmap small = new Pixmap(128, 128, pixmap.getFormat());
			small.drawPixmap(pixmap,
					0, 0, pixmap.getWidth(), pixmap.getHeight(),
					20, 20, small.getWidth() - 40, small.getHeight() - 40);
			Texture texture = new Texture(small);
			pixmap.dispose();
			small.dispose();
			return texture;
		}
	}

	@Override
	public CharacterStrings getCharacterString()
	{
		CharSelectInfo loadout = getLoadout();
		CharacterStrings characterStrings = new CharacterStrings();
		characterStrings.NAMES = new String[]{loadout.name};
		characterStrings.TEXT = new String[]{loadout.flavorText};
		return characterStrings;
	}

	public String getSensoryStoneText()
	{
		return null;
	}

	@Override
	public void refreshCharStat()
	{
		charStat = new CharStat(this);
	}

	@Override
	public void movePosition(float x, float y)
	{
		float dialogOffsetX = dialogX - drawX;
		float dialogOffsetY = dialogY - drawY;

		drawX = x;
		drawY = y;
		dialogX = drawX + dialogOffsetX;
		dialogY = drawY + dialogOffsetY;
		animX = 0;
		animY = 0;
		refreshHitboxLocation();
	}

	@SpireOverride
	protected void updateEscapeAnimation()
	{
		if (escapeTimer != 0) {
			if (flipHorizontal) {
				dialogX -= Gdx.graphics.getDeltaTime() * 400f * Settings.scale;
			} else {
				dialogX += Gdx.graphics.getDeltaTime() * 500f * Settings.scale;
			}
		}
		SpireSuper.call();
	}

	public Texture getCutsceneBg()
	{
		return ImageMaster.loadImage("images/scenes/redBg.jpg");
	}

	public List<CutscenePanel> getCutscenePanels()
	{
		List<CutscenePanel> panels = new ArrayList<>();
		panels.add(new CutscenePanel("images/scenes/ironclad1.png", "ATTACK_HEAVY"));
		panels.add(new CutscenePanel("images/scenes/ironclad2.png"));
		panels.add(new CutscenePanel("images/scenes/ironclad3.png"));
		return panels;
	}

	public void updateVictoryVfx(ArrayList<AbstractGameEffect> effects)
	{

	}

	@Override
	public String getPortraitImageName()
	{
		return BaseMod.getPlayerPortrait(chosenClass);
	}
}
