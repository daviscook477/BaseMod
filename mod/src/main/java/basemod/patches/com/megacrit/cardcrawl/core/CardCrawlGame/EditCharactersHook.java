package basemod.patches.com.megacrit.cardcrawl.core.CardCrawlGame;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.EditCharactersEvent;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.splash.SplashScreen;
import javassist.CtBehavior;

@SpirePatch(
		clz=CardCrawlGame.class,
		method="create"
)
public class EditCharactersHook
{
	@SpireInsertPatch(
			locator=Locator.class
	)
	public static void Insert(CardCrawlGame __instance)
	{
		BaseMod6.EVENT_BUS.post(new EditCharactersEvent());
		BaseMod.publishEditCharacters();
	}

	private static class Locator extends SpireInsertLocator
	{
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
		{
			Matcher finalMatcher = new Matcher.NewExprMatcher(SplashScreen.class);
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}
}
