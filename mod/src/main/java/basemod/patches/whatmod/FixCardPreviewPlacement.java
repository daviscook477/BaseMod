package basemod.patches.whatmod;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

@SpirePatch(
		clz=AbstractCard.class,
		method="renderCardPreviewInSingleView"
)
public class FixCardPreviewPlacement
{
	public static ExprEditor Instrument()
	{
		return new ExprEditor() {
			@Override
			public void edit(FieldAccess f) throws CannotCompileException
			{
				if (f.isWriter() && f.getFieldName().equals("current_x")) {
					f.replace(String.format("$proceed(%1$s.WIDTH / 2f - 475f * %1$s.scale);", Settings.class.getName()));
				}
			}
		};
	}
}
