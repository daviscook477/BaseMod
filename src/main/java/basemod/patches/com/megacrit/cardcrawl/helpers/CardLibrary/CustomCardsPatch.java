package basemod.patches.com.megacrit.cardcrawl.helpers.CardLibrary;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(cls="com.megacrit.cardcrawl.helpers.CardLibrary", method="initialize")
public class CustomCardsPatch {
	@SpireInsertPatch(
			locator=Locator.class
	)
	public static void Insert() {
		// add new cards
		for (AbstractCard card : BaseMod.getCustomCardsToAdd()) {
			CardLibrary.add(card);
		}
		
		// remove old cards
		ArrayList<String> cardIDs = BaseMod.getCustomCardsToRemove();
		ArrayList<AbstractCard.CardColor> colors = BaseMod.getCustomCardsToRemoveColors();
		for (int i = 0; i < cardIDs.size(); i++) {
			String cardID = cardIDs.get(i);
			CardLibrary.cards.remove(cardID);
			BaseMod.decrementCardCount(colors.get(i));
			CardLibrary.totalCardCount--;
		}
	}

	private static class Locator extends SpireInsertLocator
	{
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
		{
			Matcher finalMatcher = new Matcher.MethodCallMatcher(
					"com.megacrit.cardcrawl.helpers.CardLibrary", "addColorlessCards");

			return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
		}
	}
	
}
