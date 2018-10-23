package basemod.patches.com.megacrit.cardcrawl.actions.GameActionManager;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(cls="com.megacrit.cardcrawl.actions.GameActionManager", method="getNextAction")
public class PreMonsterTurnHook {
    public static ExprEditor Instrument() {
        return new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals("com.megacrit.cardcrawl.monsters.AbstractMonster") && m.getMethodName().equals("takeTurn")) {
                    m.replace("{ if (basemod.BaseMod.publishPreMonsterTurn(m)) { $proceed(); } }");
                }
            }
        };
    }
}
