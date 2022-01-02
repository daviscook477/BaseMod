package basemod.patches.com.megacrit.cardcrawl.actions.GameActionManager;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.EnableEndTurnButtonAction;
import javassist.CtBehavior;

@SpirePatch(
		clz = GameActionManager.class,
		method = "getNextAction"
)
public class StartPlayerTurnPostDrawHook
{
	@SpireInsertPatch(
			locator = Locator.class
	)
	public static void Insert()
	{
		// round 2 ~ end battle
		BaseMod.publishStartPlayerTurnPostDraw(GameActionManager.turn);
	}

	private static class Locator extends SpireInsertLocator
	{
		@Override
		public int[] Locate(CtBehavior ctBehavior) throws Exception
		{
			Matcher finalMatcher = new Matcher.NewExprMatcher(EnableEndTurnButtonAction.class);
			return LineFinder.findInOrder(ctBehavior, finalMatcher);
		}
	}
}
