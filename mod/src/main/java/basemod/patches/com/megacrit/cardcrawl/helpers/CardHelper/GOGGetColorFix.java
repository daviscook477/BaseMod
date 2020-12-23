package basemod.patches.com.megacrit.cardcrawl.helpers.CardHelper;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.CardHelper;
import javassist.*;
import javassist.bytecode.DuplicateMemberException;

@SpirePatch(
		clz = CardHelper.class,
		method = SpirePatch.CONSTRUCTOR
)
public class GOGGetColorFix
{
	public static void Raw(CtBehavior ctBehavior)
	{
		try {
			CtClass ctClass = ctBehavior.getDeclaringClass();
			CtMethod method = CtNewMethod.make(
					String.format(
							"public static %1$s getColor(float r, float g, float b) {" +
									"return new %1$s(r / 255f, g / 255f, b / 255f, 1f);" +
									"}",
							Color.class.getName()
					),
					ctClass
			);
			ctClass.addMethod(method);
		} catch (DuplicateMemberException ignored) {
		} catch (CannotCompileException e) {
			e.printStackTrace();
		}
	}
}
