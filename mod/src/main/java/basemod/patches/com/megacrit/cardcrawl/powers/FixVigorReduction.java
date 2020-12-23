package basemod.patches.com.megacrit.cardcrawl.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

@SpirePatch(
		clz = VigorPower.class,
		method = "onUseCard"
)
public class FixVigorReduction
{
	public static ExprEditor Instrument()
	{
		return new ExprEditor() {
			@Override
			public void edit(NewExpr e) throws CannotCompileException
			{
				if (e.getClassName().equals(RemoveSpecificPowerAction.class.getName())) {
					e.replace("$_ = new " + ReducePowerAction.class.getName() + "($$, amount);");
				}
			}
		};
	}
}
