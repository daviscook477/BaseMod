package basemod.patches.com.megacrit.cardcrawl.actions.GameActionManager;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(cls="com.megacrit.cardcrawl.actions.GameActionManager", method="getNextAction")
public class GetNextActionHook {
	
	
	@SpireInsertPatch(
			locator=Locator.class
	)
	public static void Insert(Object __obj_instance) {
		GameActionManager actionManager = (GameActionManager) __obj_instance;
		BaseMod.publishOnCardUse(((CardQueueItem)actionManager.cardQueue.get(0)).card);
	}
	
	private static class Locator extends SpireInsertLocator
	{
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
		{
			ArrayList<Matcher> prevMatches = new ArrayList<>();
			prevMatches.add(
					new Matcher.FieldAccessMatcher(
							"com.megacrit.cardcrawl.actions.GameActionManager",
							"cardsPlayedThisTurn"));
			
			Matcher finalMatcher = new Matcher.MethodCallMatcher("java.util.ArrayList", "add");

			return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
		}
	}
	
}
