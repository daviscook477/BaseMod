package basemod.patches.com.megacrit.cardcrawl.rooms.AbstractRoom;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;

@SpirePatch(clz = AbstractRoom.class, method = "addPotionToRewards", paramtypez = {})
public class FixUnnecessaryRewardCulling {
    @SpireInstrumentPatch
    public static ExprEditor patch() {
        return new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals(ArrayList.class.getName()) && m.getMethodName().equals("size")) {
                    m.replace("$_ = -1;");
                }
            }
        };
    }
}
