package basemod.patches.com.megacrit.cardcrawl.screens.stats.Achievements;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.stats.AchievementItem;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(
		clz=AchievementItem.class,
		method=SpirePatch.CONSTRUCTOR,
		paramtypez={
				String.class,
				String.class,
				String.class,
				String.class,
				boolean.class
		}
)
public class StopPublisherUnlock
{
	public static ExprEditor Instrument()
	{
		return new ExprEditor() {
			@Override
			public void edit(MethodCall m) throws CannotCompileException
			{
				if (m.getMethodName().equals("unlockAchievement")) {
					m.replace("if (!" + BaseMod.class.getName() + ".isModdedAchievement($$)) {" +
							"$_ = $proceed($$);" +
							"}");
				}
			}
		};
	}
}
