package basemod.patches.com.megacrit.cardcrawl.actions.unique.DiscoveryAction;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.DiscoveryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.watcher.MasterRealityPower;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import javassist.CtBehavior;

@SpirePatch(
		clz = DiscoveryAction.class,
		method = "update"
)
public class MoreThanTwoFix
{
	@SpireInsertPatch(
			locator = Locator.class
	)
	public static SpireReturn<Void> Insert(DiscoveryAction __instance)
	{
		if (__instance.amount != 1 && __instance.amount != 2) {
			int amount = __instance.amount;
			int toHand = Math.min(amount, BaseMod.MAX_HAND_SIZE - AbstractDungeon.player.hand.size());
			int toDiscard = amount - toHand;

			for (int i=0; i<toHand; ++i) {
				AbstractCard card = AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy();
				if (AbstractDungeon.player.hasPower(MasterRealityPower.POWER_ID)) {
					card.upgrade();
				}
				card.setCostForTurn(0);
				AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(card));
			}
			for (int i=0; i<toDiscard; ++i) {
				AbstractCard card = AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy();
				if (AbstractDungeon.player.hasPower(MasterRealityPower.POWER_ID)) {
					card.upgrade();
				}
				card.setCostForTurn(0);
				AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(card));
			}

			// Cleanup like the original code
			AbstractDungeon.cardRewardScreen.discoveryCard = null;
			ReflectionHacks.setPrivate(__instance, DiscoveryAction.class, "retrieveCard", true);
			ReflectionHacks.privateMethod(AbstractGameAction.class, "tickDuration").invoke(__instance);

			return SpireReturn.Return(null);
		}

		return SpireReturn.Continue();
	}

	private static class Locator extends SpireInsertLocator
	{
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
		{
			Matcher finalMatcher = new Matcher.FieldAccessMatcher(DiscoveryAction.class, "amount");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}
}
