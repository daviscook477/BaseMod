package basemod.test;

import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Claw;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;

public class Purpleclad extends CustomPlayer {

	public Purpleclad(String name) {
		super(name, CharacterEnumPatch.THE_PURPLECLAD, null, null, (String)null, (String)null);
		
		this.dialogX = (this.drawX + 0.0F * Settings.scale);
		this.dialogY = (this.drawY + 220.0F * Settings.scale);
		
		initializeClass(null, "images/characters/ironclad/shoulder2.png", "images/characters/ironclad/shoulder.png", "images/characters/ironclad/corpse.png", 
				getLoadout(), 20.0F, -10.0F, 220.0F, 290.0F, new EnergyManager(3));
		
		loadAnimation("images/characters/ironclad/idle/skeleton.atlas", "images/characters/ironclad/idle/skeleton.json", 1.0F);
		
		AnimationState.TrackEntry e = this.state.setAnimation(0, "animation", true);
		e.setTime(e.getEndTime() * MathUtils.random());
	}

	@Override
	public ArrayList<String> getStartingDeck() {
		ArrayList<String> retVal = new ArrayList<>();
		retVal.add(Strike_Purple.ID);
		retVal.add(Strike_Purple.ID);
		retVal.add(Strike_Purple.ID);
		retVal.add(Strike_Purple.ID);
		retVal.add(Defend_Purple.ID);
		retVal.add(Defend_Purple.ID);
		retVal.add(Defend_Purple.ID);
		retVal.add(Defend_Purple.ID);
		return retVal;
	}

	@Override
	public AbstractCard getStartCardForEvent()
	{
		return new Claw();
	}

	@Override
	public Color getCardTrailColor()
	{
		return Color.PURPLE;
	}

	@Override
	public int getAscensionMaxHPLoss()
	{
		return 13;
	}

	@Override
	public BitmapFont getEnergyNumFont()
	{
		return FontHelper.energyNumFontRed;
	}

	@Override
	public void doCharSelectScreenSelectEffect()
	{
		CardCrawlGame.sound.playA("ATTACK_HEAVY", MathUtils.random(-0.2f, 0.2f));
		CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.XLONG, true);
	}

	@Override
	public String getCustomModeCharacterButtonSoundKey()
	{
		return "ATTACK_HEAVY";
	}

	@Override
	public String getLocalizedCharacterName()
	{
		return "The Purpleclad";
	}

	@Override
	public AbstractPlayer newInstance()
	{
		return new Purpleclad(name);
	}

	@Override
	public TextureAtlas.AtlasRegion getOrb()
	{
		return null;
	}

	@Override
	public String getSpireHeartText()
	{
		return null;
	}

	@Override
	public Color getSlashAttackColor()
	{
		return null;
	}

	@Override
	public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect()
	{
		return new AbstractGameAction.AttackEffect[0];
	}

	@Override
	public String getVampireText()
	{
		return null;
	}

	@Override
	public ArrayList<String> getStartingRelics() {
		ArrayList<String> retVal = new ArrayList<>();
		retVal.add(Arcanosphere.ID);
		UnlockTracker.markRelicAsSeen(Arcanosphere.ID);
		return retVal;
	}

	@Override
	public CharSelectInfo getLoadout() {
		return new CharSelectInfo("The Purpleclad", "A long-lost survivor of the Ironclads. Decided to dye himself purple.",
				75, 75, 0, 99, 5,
			this, getStartingRelics(), getStartingDeck(), false);
	}

	@Override
	public String getTitle(PlayerClass playerClass)
	{
		return "the Purpleclad";
	}

	@Override
	public AbstractCard.CardColor getCardColor()
	{
		return ColorEnumPatch.PURPLE;
	}

	@Override
	public Color getCardRenderColor()
	{
		return Color.PURPLE;
	}
}
