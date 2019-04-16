package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.PostDrawEvent;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import javassist.CtBehavior;

@SpirePatch(
		clz=AbstractPlayer.class,
		method="draw",
		paramtypez={
				int.class
		}
)
public class PostDrawHook
{
    @SpireInsertPatch(
    		locator=Locator.class,
			localvars={"c"}
	)
    public static void Insert(AbstractPlayer __instance, int numCards, AbstractCard c)
	{
		BaseMod6.EVENT_BUS.post(new PostDrawEvent(c));
        BaseMod.publishPostDraw(c);
    }

	private static class Locator extends SpireInsertLocator
	{
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
		{
			Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "relics");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}
}
