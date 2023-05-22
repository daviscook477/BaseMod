package basemod.patches.com.megacrit.cardcrawl.screens.charSelect.CharacterOption;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(clz = CharacterOption.class, method = "renderRelics", paramtypez = {SpriteBatch.class})
public class MultilineStarterRelicFix {

    public static ExprEditor Instrument() {
        final int[] counter = {0};
        return new ExprEditor() {
            @Override
            public void edit(MethodCall methodCall) throws CannotCompileException {
                if (methodCall.getMethodName().equals("renderSmartText")) {
                    if (counter[0] == 1 || counter[0] == 3) {
                        methodCall.replace(
                                "$7 = 30 * com.megacrit.cardcrawl.core.Settings.scale; $_ = $proceed($$);"
                        );
                    }
                    counter[0]++;
                }
            }
        };
    }

}