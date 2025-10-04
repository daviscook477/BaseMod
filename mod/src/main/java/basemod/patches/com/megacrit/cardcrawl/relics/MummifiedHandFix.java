package basemod.patches.com.megacrit.cardcrawl.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.MummifiedHand;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

@SpirePatch2(clz = MummifiedHand.class, method = "onUseCard")
public class MummifiedHandPatch {
    @SpireInstrumentPatch
    public static ExprEditor MummifiedHandCheck() {
        return new ExprEditor() {
            public void edit(FieldAccess f) throws CannotCompileException {
                if (f.getClassName().equals(AbstractCard.class.getName()) && f.getFieldName().equals("cost")) {
                    f.replace("if (" + BaseMod.class.getName() + ".fixesEnabled) {" +
                            "$_ = $0.costForTurn;" +
                            "} else {" +
                            "$_ = $proceed($$);" +
                            "}");
                }
            }
        };
    }
}
