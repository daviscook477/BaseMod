package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(
		cls="com.megacrit.cardcrawl.characters.AbstractPlayer",
		method="evokeOrb"
)
@SpirePatch(
		cls="com.megacrit.cardcrawl.characters.AbstractPlayer",
		method="evokeNewestOrb"
)
@SpirePatch(
		cls="com.megacrit.cardcrawl.characters.AbstractPlayer",
		method="evokeWithoutLosingOrb"
)
public class OnEvokeOrb
{
	public static ExprEditor Instrument()
	{
		return new ExprEditor() {
			@Override
			public void edit(MethodCall m) throws CannotCompileException
			{
				if (m.getMethodName().equals("onEvoke")) {
					m.replace("{" +
							"$_ = $proceed($$);" +
							"basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer.OnEvokeOrb.Nested.onEvoke($0);" +
							"}");
				}
			}
		};
	}

	public static class Nested
	{
		public static void onEvoke(AbstractOrb orb)
		{
			for (AbstractPower power : AbstractDungeon.player.powers) {
				power.onEvokeOrb(orb);
			}
			for (AbstractRelic relic : AbstractDungeon.player.relics) {
				relic.onEvokeOrb(orb);
			}
		}
	}
}
