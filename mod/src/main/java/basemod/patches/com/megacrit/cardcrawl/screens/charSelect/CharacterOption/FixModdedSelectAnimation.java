package basemod.patches.com.megacrit.cardcrawl.screens.charSelect.CharacterOption;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

/* NOTE:
 * This is a hack. It would be much better to figure out what is causing
 * the long frame and solve that, but this will have to do for now.
 */
@SpirePatch(
		clz = CharacterSelectScreen.class,
		method = "update"
)
public class FixModdedSelectAnimation
{
	public static ExprEditor Instrument()
	{
		return new ExprEditor() {
			@Override
			public void edit(MethodCall m) throws CannotCompileException
			{
				if (m.getClassName().equals(MathHelper.class.getName()) && m.getMethodName().equals("fadeLerpSnap")) {
					m.replace("$_ = " + FixModdedSelectAnimation.class.getName() + ".fadeLerpSnapFix($$);");
				}
			}
		};
	}

	public static float fadeLerpSnapFix(float startX, float targetX)
	{
		if (startX != targetX) {
			float dt = Gdx.graphics.getDeltaTime();
			if (dt > 0.01f) {
				dt = 0.01f;
			}
			startX = MathUtils.lerp(startX, targetX, dt * 12f);
			if (Math.abs(startX - targetX) < 0.01f) {
				startX = targetX;
			}
		}
		return startX;
	}
}
