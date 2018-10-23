package basemod.patches.com.megacrit.cardcrawl.screens.custom.CustomModeScreen;

import basemod.helpers.BaseModCardTags;
import basemod.helpers.CardTags;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.screens.custom.CustomModeScreen;
import com.megacrit.cardcrawl.trials.CustomTrial;
import com.megacrit.cardcrawl.trials.MyTrueFormTrial;

import java.util.ArrayList;
import java.util.List;


public class MyTrueFormPatch
{
	@SpirePatch(
			cls="com.megacrit.cardcrawl.screens.custom.CustomModeScreen",
			method="addNonDailyMods"
	)
	public static class CustomMode
	{
		public static void Postfix(CustomModeScreen __instance, CustomTrial trial, ArrayList<String> modIds)
		{
			for (String modId : modIds) {
				switch (modId) {
					case "My True Form":
						List<String> cards = new ArrayList<>();
						for (AbstractCard card : CardLibrary.getAllCards()) {
							if (CardTags.hasTag(card, BaseModCardTags.FORM)) {
								cards.add(card.cardID);
							}
						}
						trial.addStarterCards(cards);
						break;
				}
			}
		}
	}

	@SpirePatch(
			cls="com.megacrit.cardcrawl.trials.MyTrueFormTrial",
			method="dailyModIDs"
	)
	public static class SeededRun
	{
		public static ArrayList<String> Postfix(ArrayList<String> __result, MyTrueFormTrial __instance)
		{
			for (AbstractCard card : CardLibrary.getAllCards()) {
				if (CardTags.hasTag(card, BaseModCardTags.FORM)) {
					__result.add(card.cardID);
				}
			}
			return __result;
		}
	}
}
