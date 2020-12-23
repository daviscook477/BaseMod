package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(
		clz = AbstractDungeon.class,
		method = "returnTrulyRandomCardInCombat",
		paramtypez = {}
)
public class FixDiscoveryLogSpam
{
	public static ExprEditor Instrument()
	{
		return new ExprEditor() {
			@Override
			public void edit(MethodCall m) throws CannotCompileException
			{
				if (m.getClassName().equals(UnlockTracker.class.getName()) && m.getMethodName().equals("markCardAsSeen")) {
					m.replace("");
				}
			}
		};
	}

	public static AbstractCard Postfix(AbstractCard __result)
	{
		UnlockTracker.markCardAsSeen(__result.cardID);
		return __result;
	}
}
