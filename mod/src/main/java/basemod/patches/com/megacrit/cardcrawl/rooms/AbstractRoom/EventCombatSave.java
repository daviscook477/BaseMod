package basemod.patches.com.megacrit.cardcrawl.rooms.AbstractRoom;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class EventCombatSave {
    @SpirePatch(
            clz = AbstractRoom.class,
            method = SpirePatch.CLASS
    )
    public static class Field {
        public static SpireField<Boolean> noSave = new SpireField<>(()->false);
    }

    public static void preventSave() {
        Field.noSave.set(AbstractDungeon.getCurrRoom(), true);
    }
    public static void allowSave() {
        Field.noSave.set(AbstractDungeon.getCurrRoom(), false);
    }
    public static boolean noSave(AbstractRoom room) {
        return Field.noSave.get(room);
    }

    @SpirePatch(
            clz = AbstractRoom.class,
            method = "update"
    )
    public static class ControlSave {
        @SpireInstrumentPatch
        public static ExprEditor modifyCondition() {
            return new ExprEditor() {
                int count = 0;

                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (count < 3 && f.getFieldName().equals("loadingSave") && f.getClassName().equals(CardCrawlGame.class.getName())) {
                        ++count;
                        if (count == 3) {
                            f.replace("$_ = $proceed($$) || " + EventCombatSave.class.getName() + ".noSave(this);");
                            //becomes !(CardCrawlGame.loadingSave || noSave(this))
                        }
                    }
                }
            };
        }
    }
}

