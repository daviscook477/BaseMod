package basemod.patches.com.megacrit.cardcrawl.rooms.AbstractRoom;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(
		clz = AbstractRoom.class,
		method = "update"
)
public class StartPlayerTurnPostDrawHook
{
	public static ExprEditor Instrument() {
		return new ExprEditor() {
			@Override
			public void edit(MethodCall m) throws CannotCompileException
			{
				if (m.getClassName().equals(GameActionManager.class.getCanonicalName())
						&& m.getMethodName().equals("useNextCombatActions")) {
					// Round 1
					m.replace(String.format("{ $_ = $proceed($$); %s.publishStartPlayerTurnPostDraw(1); }", BaseMod.class.getCanonicalName()));
				}
			}
		};
	}
}
