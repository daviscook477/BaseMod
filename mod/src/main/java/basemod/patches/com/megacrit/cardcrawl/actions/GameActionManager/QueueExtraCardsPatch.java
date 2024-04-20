package basemod.patches.com.megacrit.cardcrawl.actions.GameActionManager;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;
import java.util.Iterator;

@SpirePatch2(
		clz = GameActionManager.class,
		method = "queueExtraCard"
)
public class QueueExtraCardsPatch
{
	@SpireInstrumentPatch
	public static ExprEditor Instrument()
	{
		return new ExprEditor() {
			@Override
			public void edit(MethodCall m) throws CannotCompileException
			{
				// done this way (instead of FieldAccess on cardQueue) to avoid creating a new ArrayList when filtering
				if (m.getClassName().equals(ArrayList.class.getName()) && m.getMethodName().equals("iterator")) {
					m.replace("$_ = " + QueueExtraCardsPatch.class.getName() + ".filterNullCards($0);");
				}
			}
		};
	}

	public static Iterator<CardQueueItem> filterNullCards(ArrayList<CardQueueItem> cardQueue)
	{
		return cardQueue.stream()
				.filter(c -> c.card != null)
				.iterator();
	}
}
