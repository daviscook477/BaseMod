package basemod.patches.com.megacrit.cardcrawl.cards.CardGroup;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.PostExhaustEvent;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import javassist.CtBehavior;

@SpirePatch(
		clz=CardGroup.class,
		method="moveToExhaustPile"
)
public class MoveToExhaustPileHook
{
	@SpireInsertPatch(
			locator=Locator.class
	)
	public static void Insert(CardGroup __instance, AbstractCard c)
	{
		BaseMod6.EVENT_BUS.post(new PostExhaustEvent(c));
		BaseMod.publishPostExhaust(c);
	}

	private static class Locator extends SpireInsertLocator
	{
		@Override
		public int[] Locate(CtBehavior ctBehavior) throws Exception
		{
			Matcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "triggerOnExhaust");
			return LineFinder.findInOrder(ctBehavior, matcher);
		}
	}
}
