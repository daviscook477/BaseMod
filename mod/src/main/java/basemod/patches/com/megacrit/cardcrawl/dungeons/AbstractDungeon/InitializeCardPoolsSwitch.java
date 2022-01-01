package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.BlueCards;
import com.megacrit.cardcrawl.daily.mods.Diverse;
import com.megacrit.cardcrawl.daily.mods.GreenCards;
import com.megacrit.cardcrawl.daily.mods.RedCards;
import com.megacrit.cardcrawl.daily.mods.PurpleCards;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
		clz=AbstractDungeon.class,
		method="initializeCardPools"
)
public class InitializeCardPoolsSwitch {

	@SpireInsertPatch(
			locator=Locator.class,
			localvars={"tmpPool"}
	)
	public static void Insert(AbstractDungeon __instance, ArrayList<AbstractCard> tmpPool) {
		AbstractPlayer player = AbstractDungeon.player;
		AbstractPlayer.PlayerClass chosenClass = player.chosenClass;

		if (AbstractPlayer.customMods == null) {
			AbstractPlayer.customMods = new ArrayList<>();
		}

		// Diverse
		if (ModHelper.isModEnabled(Diverse.ID)) {
			for (AbstractPlayer character : BaseMod.getModdedCharacters()) {
				character.getCardPool(tmpPool);
			}
		} else if (!BaseMod.isBaseGameCharacter(chosenClass)) {
			// Red/Green/Blue/Purple Cards modifiers for modded characters
			if (ModHelper.isModEnabled(RedCards.ID)) {
				CardLibrary.addRedCards(tmpPool);
			}
			if (ModHelper.isModEnabled(GreenCards.ID)) {
				CardLibrary.addGreenCards(tmpPool);
			}
			if (ModHelper.isModEnabled(BlueCards.ID)) {
				CardLibrary.addBlueCards(tmpPool);
			}
			if (ModHelper.isModEnabled(PurpleCards.ID)) {
				CardLibrary.addPurpleCards(tmpPool);
			}
		}

		if (!ModHelper.isModEnabled(Diverse.ID)) {
			// Modded character cards modifiers
			CustomMod charMod = new CustomMod("Modded Character Cards", "p", false);
			for (AbstractPlayer character : BaseMod.getModdedCharacters()) {
				if (character.chosenClass == chosenClass) {
					continue;
				}
				 String ID = character.chosenClass.name() + charMod.name;
				 if (ModHelper.isModEnabled(ID)) {
				 	BaseMod.logger.info("[INFO] Adding " + character.getLocalizedCharacterName() + " cards into card pool.");
				 	AbstractCard.CardColor color = character.getCardColor();
				 	for (AbstractCard c : CardLibrary.cards.values()) {
				 		if (c.color == color && c.rarity != AbstractCard.CardRarity.BASIC &&
								(!UnlockTracker.isCardLocked(c.cardID) || Settings.treatEverythingAsUnlocked())) {
				 			tmpPool.add(c);
						}
					}
				 }
			}
		}
	}

	private static class Locator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctBehavior) throws Exception
		{
			Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "addColorlessCards");

			int[] lines = LineFinder.findAllInOrder(ctBehavior, finalMatcher);
			return new int[]{lines[lines.length-1]};
		}
	}
}
