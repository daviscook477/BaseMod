package basemod.patches.com.megacrit.cardcrawl.helpers.input.InputAction;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class ConsoleDisable {

	@SpirePatch(cls="com.megacrit.cardcrawl.helpers.input.InputAction", method="isJustPressed")
	public static class IsJustPressedFix {
		
		public static ExprEditor Instrument() {
			return new ExprEditor() {
				public void edit(MethodCall m) throws CannotCompileException {
	                if (m.getClassName().equals("com.badlogic.gdx.Input") && m.getMethodName().equals("isKeyJustPressed")) {
	                    m.replace("{ $_ = !basemod.DevConsole.visible && $proceed($$); }");
	                }
	            }
			};
		}
		
	}
	
	@SpirePatch(cls="com.megacrit.cardcrawl.helpers.input.InputAction", method="isPressed")
	public static class IsPressedFix {
		
		public static ExprEditor Instrument() {
			return new ExprEditor() {
				public void edit(MethodCall m) throws CannotCompileException {
	                if (m.getClassName().equals("com.badlogic.gdx.Input") && m.getMethodName().equals("isKeyPressed")) {
	                    m.replace("{ $_ = !basemod.DevConsole.visible && $proceed($$); }");
	                }
	            }
			};
		}
		
	}
	
}
