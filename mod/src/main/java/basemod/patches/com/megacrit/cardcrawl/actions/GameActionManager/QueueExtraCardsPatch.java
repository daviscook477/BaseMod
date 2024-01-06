package basemod.patches.com.megacrit.cardcrawl.actions.GameActionManager;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

@SpirePatch2(clz= GameActionManager.class, method = "queueExtraCard")
public class QueueExtraCardsPatch {
    // Fixes the crash that happens when this method is called after the turn has ended because that queues an empty cardQueueItem
    @SpireInstrumentPatch
    public static ExprEditor instrument() {
        return new ExprEditor() {
            @Override
            public void edit(FieldAccess f) throws CannotCompileException {
                if (f.getClassName().equals(CardQueueItem.class.getName()) && f.getFieldName().equals("card")) {
                    f.replace("{" +
                            "if(c.card != null) { $_= $proceed($$); }" +
                            // This card will never have the same UUID as the last one, so it'll be false.
                            "else { $_ = new com.megacrit.cardcrawl.cards.status.Dazed();}" +
                            "}");
                }
            }
        };
    }
}
