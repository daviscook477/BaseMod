package basemod.patches.com.megacrit.cardcrawl.helpers.CardLibrary;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CtBehavior;

@SpirePatch(
		clz=CardLibrary.class,
		method="add"
)
public class AddSwitch
{
	@SpireInsertPatch(
			locator=Locator.class
	)
	public static void Insert(AbstractCard card)
	{
		CardColor color = card.color;
		switch (color) {
			case RED:
			case GREEN:
			case BLUE:
			case COLORLESS:
			case CURSE:
				break;
			default:
				BaseMod.incrementCardCount(color);
				if (UnlockTracker.isCardSeen(card.cardID)) {
					BaseMod.incrementSeenCardCount(color);
				}
		}
	}

	private static class Locator extends SpireInsertLocator
	{
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
		{
			Matcher finalMatcher = new Matcher.MethodCallMatcher(UnlockTracker.class, "isCardSeen");
			int[] lines = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
			return new int[]{lines[lines.length - 1]};
		}
	}
}
