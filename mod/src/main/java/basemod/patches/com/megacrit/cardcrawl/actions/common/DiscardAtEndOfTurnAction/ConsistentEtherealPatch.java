package basemod.patches.com.megacrit.cardcrawl.actions.common.DiscardAtEndOfTurnAction;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.DiscardAtEndOfTurnAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.Collections;

@SpirePatch(
        clz = DiscardAtEndOfTurnAction.class,
        method = "update"
)
public class ConsistentEtherealPatch {
    @SpireInstrumentPatch
    public static ExprEditor instrument() {
        return new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getMethodName().equals("shuffle") && m.getClassName().equals(Collections.class.getName())) {
                    m.replace("if (" + BaseMod.class.getName() + ".fixesEnabled) {" +
                            Collections.class.getName() + ".shuffle($1, new java.util.Random(" + AbstractDungeon.class.getName() + ".shuffleRng.randomLong()));" +
                            "} else {" +
                            "$proceed($$);" +
                            "}");
                }
            }
        };
    }
}
