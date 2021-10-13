package basemod.patches.com.megacrit.cardcrawl.monsters;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.beyond.Transient;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;

@SpirePatch(
		clz = Transient.class,
		method = "takeTurn"
)
public class FixTransientCrash
{
	public static ExprEditor Instrument()
	{
		return new ExprEditor() {
			@Override
			public void edit(MethodCall m) throws CannotCompileException
			{
				if (m.getClassName().equals(ArrayList.class.getName()) && m.getMethodName().equals("get")) {
					m.replace("$_ = " + FixTransientCrash.class.getName() + ".makeDamageInfo(this, $1, startingDeathDmg);");
				}
			}
		};
	}

	public static DamageInfo makeDamageInfo(Transient __instance, int count, int startingDeathDmg)
	{
		DamageInfo info = new DamageInfo(__instance, startingDeathDmg + count * 10);
		info.applyPowers(__instance, AbstractDungeon.player);
		return info;
	}
}
