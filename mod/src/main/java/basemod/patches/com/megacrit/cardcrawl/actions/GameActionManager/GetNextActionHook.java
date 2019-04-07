package basemod.patches.com.megacrit.cardcrawl.actions.GameActionManager;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.OnCardUseEvent;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
		clz=GameActionManager.class,
		method="getNextAction"
)
public class GetNextActionHook
{
	@SpireInsertPatch(
			locator=Locator.class
	)
	public static void Insert(GameActionManager __instance)
	{
		BaseMod6.EVENT_BUS.post(new OnCardUseEvent(__instance.cardQueue.get(0).card));
		BaseMod.publishOnCardUse(__instance.cardQueue.get(0).card);
	}
	
	private static class Locator extends SpireInsertLocator
	{
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
		{
			ArrayList<Matcher> prevMatches = new ArrayList<>();
			prevMatches.add(
					new Matcher.FieldAccessMatcher(GameActionManager.class, "cardsPlayedThisTurn"));
			
			Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "add");

			return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
		}
	}
	
}
