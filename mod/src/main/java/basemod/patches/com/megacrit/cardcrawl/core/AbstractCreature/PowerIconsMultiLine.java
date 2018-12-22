package basemod.patches.com.megacrit.cardcrawl.core.AbstractCreature;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(
		clz=AbstractCreature.class,
		method="renderPowerIcons"
)
public class PowerIconsMultiLine
{
	private static final int MAX_PER_ROW = 10;
	private static float offsetY = 0;
	private static int count = 0;
	private static boolean doingAmounts = false;

	@SuppressWarnings("unused")
	public static float getOffsetY()
	{
		return offsetY;
	}

	@SuppressWarnings("unused")
	public static void incrementOffsetY(float[] offsetX)
	{
		++count;
		if (count == MAX_PER_ROW) {
			count = 0;
			offsetY -= 38 * Settings.scale;
			offsetX[0] = ((doingAmounts ? 0 : 10) - 48) * Settings.scale;
		}
	}

	public static void Prefix(AbstractCreature __instance, SpriteBatch sb, float x, float y)
	{
		offsetY = 0;
		count = 0;
		doingAmounts = false;
	}

	@SpireInsertPatch(
			rloc=9
	)
	public static void Insert(AbstractCreature __instance, SpriteBatch sb, float x, float y)
	{
		offsetY = 0;
		count = 0;
		doingAmounts = true;
	}

	public static ExprEditor Instrument()
	{
		return new ExprEditor() {
			@Override
			public void edit(MethodCall m) throws CannotCompileException
			{
				if (m.getMethodName().equals("renderIcons")) {
					m.replace("{" +
							"$3 += " + PowerIconsMultiLine.class.getName() + ".getOffsetY();" +
							"$proceed($$);" +
							"float[] offsetArr = new float[]{offset};" +
							PowerIconsMultiLine.class.getName() + ".incrementOffsetY(offsetArr);" +
							"offset = offsetArr[0];" +
							"}");
				} else if (m.getMethodName().equals("renderAmount")) {
					m.replace("{" +
							"$3 += " + PowerIconsMultiLine.class.getName() + ".getOffsetY();" +
							"$proceed($$);" +
							"float[] offsetArr = new float[]{offset};" +
							PowerIconsMultiLine.class.getName() + ".incrementOffsetY(offsetArr);" +
							"offset = offsetArr[0];" +
							"}");
				}
			}
		};
	}
}
