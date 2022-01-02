package basemod.patches.com.megacrit.cardcrawl.actions.GameActionManager;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.helpers.ModHelper;
import javassist.CtBehavior;

@SpirePatch(
		clz = GameActionManager.class,
		method = "getNextAction"
)
public class StartPlayerTurnHook
{
	@SpireInsertPatch(
			locator = Locator.class
	)
	public static void Insert()
	{
		// round 2 ~ end battle
		BaseMod.publishStartPlayerTurn(GameActionManager.turn + 1);
	}

	private static class Locator extends SpireInsertLocator
	{
		@Override
		public int[] Locate(CtBehavior ctBehavior) throws Exception
		{
			Matcher finalMatcher = new Matcher.MethodCallMatcher(ModHelper.class, "isModEnabled");
			return LineFinder.findInOrder(ctBehavior, finalMatcher);
		}
	}
}
