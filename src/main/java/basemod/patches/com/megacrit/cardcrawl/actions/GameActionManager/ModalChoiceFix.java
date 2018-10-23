package basemod.patches.com.megacrit.cardcrawl.actions.GameActionManager;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.CardQueueItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpirePatch(
		clz=GameActionManager.class,
		method="getNextAction"
)
public class ModalChoiceFix
{
	private static ArrayList<AbstractGameAction> savedActions;
	private static ArrayList<AbstractGameAction> savedPreTurnActions;
	private static List<CardQueueItem> savedCardQueue;

	public static SpireReturn<Void> Prefix(GameActionManager __instance)
	{
		if (BaseMod.modalChoiceScreen.isOpen) {
			savedCardQueue = __instance.cardQueue.stream()
					.filter(c -> !BaseMod.modalChoiceScreen.cardGroup.contains(c.card))
					.collect(Collectors.toList());
			__instance.cardQueue.removeIf(c -> !BaseMod.modalChoiceScreen.cardGroup.contains(c.card));
			if (__instance.cardQueue.isEmpty()) {
				__instance.cardQueue.addAll(savedCardQueue);
				savedCardQueue = null;
				return SpireReturn.Return(null);
			}
			savedActions = __instance.actions;
			savedPreTurnActions = __instance.preTurnActions;
			__instance.actions = new ArrayList<>();
			__instance.preTurnActions = new ArrayList<>();
		}
		return SpireReturn.Continue();
	}

	public static void Postfix(GameActionManager __instance)
	{
		if (savedActions != null) {
			savedActions.addAll(0, __instance.actions);
			__instance.actions = savedActions;
			savedActions = null;
		}
		if (savedPreTurnActions != null) {
			savedPreTurnActions.addAll(0, __instance.preTurnActions);
			__instance.preTurnActions = savedPreTurnActions;
			savedPreTurnActions = null;
		}
		if (savedCardQueue != null) {
			__instance.cardQueue.addAll(savedCardQueue);
			savedCardQueue = null;
		}
	}
}
