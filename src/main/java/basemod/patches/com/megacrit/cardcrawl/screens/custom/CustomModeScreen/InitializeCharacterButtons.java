package basemod.patches.com.megacrit.cardcrawl.screens.custom.CustomModeScreen;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.screens.custom.CustomModeScreen;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
		cls="com.megacrit.cardcrawl.screens.custom.CustomModeScreen",
		method="initializeCharacters"
)
public class InitializeCharacterButtons
{
	@SpireInsertPatch(
			locator=Locator.class
	)
	public static void Insert(CustomModeScreen __instance)
	{
		__instance.options.addAll(BaseMod.generateCustomCharacterOptions());
	}

	private static class Locator extends SpireInsertLocator
	{
		@Override
		public int[] Locate(CtBehavior ctBehavior) throws Exception {
			Matcher finalMatcher = new Matcher.NewExprMatcher("com.megacrit.cardcrawl.screens.custom.CustomModeCharacterButton");

			int[] locations = LineFinder.findAllInOrder(ctBehavior, new ArrayList<>(), finalMatcher);
			return new int[]{locations[locations.length - 1]+1};
		}
	}
}
