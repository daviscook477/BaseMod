package basemod.patches.com.megacrit.cardcrawl.core.AbstractCreature;

import basemod.BaseMod;
import basemod.patches.com.megacrit.cardcrawl.actions.GameActionManager.OnPlayerLoseBlockToggle;
import basemod6.BaseMod6;
import basemod6.events.PlayerLoseBlockEvent;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;

@SpirePatch(
		clz=AbstractCreature.class,
		method="loseBlock",
		paramtypez={
				int.class,
				boolean.class
		}
)
public class ModifyPlayerLoseBlock
{
	public static void Prefix(AbstractCreature __instance, @ByRef int[] amount, boolean noAnimation)
	{
		if (OnPlayerLoseBlockToggle.isEnabled) {
			amount[0] = BaseMod6.EVENT_BUS.post(new PlayerLoseBlockEvent(amount[0]));
			amount[0] = BaseMod.publishOnPlayerLoseBlock(amount[0]);
		}
		if (amount[0] < 0) {
			amount[0] = 0;
		}
	}
}
