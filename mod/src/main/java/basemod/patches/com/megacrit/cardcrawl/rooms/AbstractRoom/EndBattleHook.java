package basemod.patches.com.megacrit.cardcrawl.rooms.AbstractRoom;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.BattleEndEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

@SpirePatch(
		clz=AbstractRoom.class,
		method="endBattle"
)
public class EndBattleHook {
	public static void Postfix(AbstractRoom __instance) {
		BaseMod6.EVENT_BUS.post(new BattleEndEvent(__instance));
		BaseMod.publishPostBattle(__instance);
	}
}
