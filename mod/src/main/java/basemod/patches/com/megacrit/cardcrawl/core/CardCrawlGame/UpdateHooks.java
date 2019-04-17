package basemod.patches.com.megacrit.cardcrawl.core.CardCrawlGame;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.PostUpdateEvent;
import basemod6.events.PreUpdateEvent;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class UpdateHooks {

	@SpirePatch(
			clz=CardCrawlGame.class,
			method="update"
	)
	public static class PreUpdateHook {
	    @SpireInsertPatch(
	    		locator=Locator.class
		)
	    public static void Insert(CardCrawlGame __instance) {
			BaseMod6.EVENT_BUS.post(new PreUpdateEvent());
	        BaseMod.publishPreUpdate();
	    }
	    
	    private static int[] offset(int[] originalArr, int offset) {
	    	int[] resultArr = new int[originalArr.length];
	    	for (int i = 0; i < originalArr.length; i++) {
	    		resultArr[i] = originalArr[i] + offset;
	    	}
	    	return resultArr;
	    }

	    private static class Locator extends SpireInsertLocator
		{
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
			{
				Matcher finalMatcher = new Matcher.MethodCallMatcher("com.megacrit.cardcrawl.helpers.input.InputHelper", "updateFirst");

				int[] beforeLines = LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);

				// offset by 1 to be called **after** the found method call
				return offset(beforeLines, 1);
			}
		}
	}

	
	@SpirePatch(
			clz=CardCrawlGame.class,
			method="update"
	)
	public static class PostUpdateHook {
		
	    @SpireInsertPatch(
	    		locator=Locator.class
		)
	    public static void Insert(CardCrawlGame __instance) {
	    	BaseMod6.EVENT_BUS.post(new PostUpdateEvent());
	        BaseMod.publishPostUpdate();
	    }

	    private static class Locator extends SpireInsertLocator
	    {
	        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
	        {
	            Matcher finalMatcher = new Matcher.MethodCallMatcher("com.megacrit.cardcrawl.helpers.input.InputHelper", "updateLast");

	            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
	        }
	    }
	}

	
}
