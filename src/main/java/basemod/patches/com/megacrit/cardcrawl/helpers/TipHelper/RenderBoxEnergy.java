package basemod.patches.com.megacrit.cardcrawl.helpers.TipHelper;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(
        cls="com.megacrit.cardcrawl.helpers.TipHelper",
        method="renderBox"
)
public class RenderBoxEnergy
{
    public static ExprEditor Instrument()
    {
        return new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException
            {
                if (m.getClassName().equals("com.megacrit.cardcrawl.helpers.FontHelper") && m.getMethodName().equals("renderFontLeftTopAligned")) {
                    m.replace("if (word.equals(\"[E]\")) {" +
                            "renderTipEnergy(sb, basemod.BaseMod.getCardSmallEnergy(card), x + TEXT_OFFSET_X, y + ORB_OFFSET_Y);" +
                            "$3 = capitalize(TEXT[0]);" +
                            "$4 = x + TEXT_OFFSET_X * 2.5f;" +
                            "$_ = $proceed($$);" +
                            "} else {" +
                            "$_ = $proceed($$);" +
                            "}");
                }
            }
        };
    }
}
