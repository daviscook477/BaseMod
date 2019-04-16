package basemod.patches.com.megacrit.cardcrawl.actions.GameActionManager;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.PreMonsterTurnEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(
        clz=GameActionManager.class,
        method="getNextAction"
)
public class PreMonsterTurnHook
{
    public static ExprEditor Instrument()
    {
        return new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException
            {
                if (m.getClassName().equals(AbstractMonster.class.getName()) && m.getMethodName().equals("takeTurn")) {
                    m.replace("{ if (" + PreMonsterTurnHook.class.getName() + ".Do(m)) { $proceed(); } }");
                }
            }
        };
    }

    public static boolean Do(AbstractMonster monster)
    {
        boolean ret = BaseMod6.EVENT_BUS.post(new PreMonsterTurnEvent(monster));
        if (!BaseMod.publishPreMonsterTurn(monster)) {
            ret = false;
        }
        return ret;
    }
}
