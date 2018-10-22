package basemod.patches.com.megacrit.cardcrawl.rooms.AbstractRoom;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

@SpirePatch(cls="com.megacrit.cardcrawl.rooms.AbstractRoom", method="endBattle")
public class EndBattleHook {
	public static void Postfix(Object __obj_instance) {
		BaseMod.publishPostBattle((AbstractRoom) __obj_instance);
	}
}
