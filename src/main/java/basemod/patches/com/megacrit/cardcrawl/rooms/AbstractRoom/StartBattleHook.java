package basemod.patches.com.megacrit.cardcrawl.rooms.AbstractRoom;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.rooms.MonsterRoom;

@SpirePatch(
        cls = "com.megacrit.cardcrawl.rooms.MonsterRoom",
        method = "onPlayerEntry"
)
public class StartBattleHook {

    public static void Prefix(MonsterRoom __instance) {
        BaseMod.publishStartBattle(__instance);
    }

}
