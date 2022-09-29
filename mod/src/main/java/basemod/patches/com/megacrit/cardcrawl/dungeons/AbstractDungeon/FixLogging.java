package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class FixLogging
{
    //Makes it possible to prefix/postfix patch these methods instead of making the default branch almost impossible to patch
    public static void unknownScreenUpdate() {

    }
    public static void unknownScreenRender() {

    }

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
                        m.replace(FixLogging.class.getName() + ".unknownScreenUpdate();");
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
                        m.replace(FixLogging.class.getName() + ".unknownScreenRender();");
                    }
                }
            };
        }
    }
}