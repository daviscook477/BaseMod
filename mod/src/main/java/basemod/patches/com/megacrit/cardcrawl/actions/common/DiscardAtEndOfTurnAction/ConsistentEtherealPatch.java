package basemod.patches.com.megacrit.cardcrawl.actions.common.DiscardAtEndOfTurnAction;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DiscardAtEndOfTurnAction;
import com.megacrit.cardcrawl.core.Settings;
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
                            Collections.class.getName() + ".shuffle($1, new java.util.Random(" + ConsistentEtherealPatch.class.getName() + ".arbitrarySomewhatSeedRelatedNumber()));" +
                            "} else {" +
                            "$proceed($$);" +
                            "}");
                }
            }
        };
    }

    private static final long FLOOR_OFFSET = 55;
    public static long arbitrarySomewhatSeedRelatedNumber() {
        return (Settings.seed == null ? 0 : Settings.seed) + AbstractDungeon.floorNum * FLOOR_OFFSET + GameActionManager.turn;
    }
}
