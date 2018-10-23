package basemod.patches.com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

@SpirePatch(
		clz=GetAllInBattleInstances.class,
		method="get"
)
public class FixNPE
{
	public static ExprEditor Instrument()
	{
		return new ExprEditor() {
			@Override
			public void edit(FieldAccess f) throws CannotCompileException
			{
				if (f.getFieldName().equals("cardInUse")) {
					f.replace("if ($0.cardInUse == null) {" +
							"$_ = " + CardLibrary.class.getName() + ".getCard(" + Madness.class.getName() + ".ID);"  +
							"} else {" +
							"$_ = $proceed($$);" +
							"}");
				}
			}
		};
	}
}
