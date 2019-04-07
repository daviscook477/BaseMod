package basemod.patches.com.megacrit.cardcrawl.relics.AbstractRelic;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.RelicGetEvent;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;

@SpirePatch(
		clz=AbstractRelic.class,
		method="reorganizeObtain"
)
public class ReorganizeObtainRelicGetHook
{
	@SpireInsertPatch(
			locator=Locator.class
	)
	public static void Insert(AbstractRelic __instance, AbstractPlayer p, int slot, boolean callOnEquip, int relicAmount)
	{
		if (AbstractDungeon.player == p) {
			BaseMod6.EVENT_BUS.post(new RelicGetEvent(__instance));
			BaseMod.publishRelicGet(__instance);
		}
	}

	private static class Locator extends SpireInsertLocator
	{
		@Override
		public int[] Locate(CtBehavior ctBehavior) throws Exception
		{
			Matcher matcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "relics");
			return LineFinder.findInOrder(ctBehavior, matcher);
		}
	}
}
