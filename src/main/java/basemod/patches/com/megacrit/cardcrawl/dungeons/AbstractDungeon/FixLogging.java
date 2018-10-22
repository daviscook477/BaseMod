package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class FixLogging
{
    @SpirePatch(
            cls="com.megacrit.cardcrawl.dungeons.AbstractDungeon",
            method="update"
    )
    public static class FixUpdateLog {
        public static ExprEditor Instrument()
        {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException
                {
                    if (m.getMethodName().equals("info")) {
                        m.replace("");
                    }
                }
            };
        }
    }

    @SpirePatch(
            cls="com.megacrit.cardcrawl.dungeons.AbstractDungeon",
            method="render"
    )
    public static class FixRenderLog {
        public static ExprEditor Instrument()
        {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException
                {
                    if (m.getMethodName().equals("info")) {
                        m.replace("");
                    }
                }
            };
        }
    }
}