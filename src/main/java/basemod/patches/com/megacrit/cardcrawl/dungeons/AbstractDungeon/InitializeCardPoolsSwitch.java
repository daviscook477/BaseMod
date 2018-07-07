package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.Map;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.daily.DailyMods;

import basemod.BaseMod;
import javassist.CtBehavior;

@SpirePatch(cls="com.megacrit.cardcrawl.dungeons.AbstractDungeon", method="initializeCardPools")
public class InitializeCardPoolsSwitch {

	@SpireInsertPatch(localvars={"tmpPool"})
	public static void Insert(Object __obj_instance, Object tmpPoolObj) {
		AbstractPlayer player = AbstractDungeon.player;
		AbstractPlayer.PlayerClass chosenClass = player.chosenClass;
		@SuppressWarnings("unchecked")
		ArrayList<AbstractCard> tmpPool = (ArrayList<AbstractCard>) tmpPoolObj;
		AbstractCard card;
		if (DailyMods.cardMods.get("Diverse")){
			for (Map.Entry<String, AbstractCard> c : CardLibrary.cards.entrySet()) {
				card = c.getValue();
				if ((BaseMod.playerColorMap.containsValue(card.color.toString())) && (card.rarity != AbstractCard.CardRarity.BASIC) &&
						((!UnlockTracker.isCardLocked(c.getKey())) || (Settings.isDailyRun))) {
					tmpPool.add(card);
				}
			}
		}
		else if (!BaseMod.isBaseGameCharacter(chosenClass)) {
			String color = BaseMod.getColor(chosenClass.toString());
			for (Map.Entry<String, AbstractCard> c : CardLibrary.cards.entrySet()) {
				card = c.getValue();
				if ((card.color.toString().equals(color)) && (card.rarity != AbstractCard.CardRarity.BASIC) && (
						(!UnlockTracker.isCardLocked(c.getKey())) || (Settings.isDailyRun))) {
					tmpPool.add(card);
				}
			}
		}
	}

	public static class Locator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctBehavior) throws Exception
		{
			Matcher finalMatcher = new Matcher.MethodCallMatcher(
					"com.megacrit.cardcrawl.dungeons.AbstractDungeon", "addColorlessCards");

			return LineFinder.findInOrder(ctBehavior, new ArrayList<>(), finalMatcher);
		}
	}
}
