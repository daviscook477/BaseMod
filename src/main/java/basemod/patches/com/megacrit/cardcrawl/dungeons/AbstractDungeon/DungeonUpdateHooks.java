package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

@SpirePatch(cls = "com.megacrit.cardcrawl.dungeons.AbstractDungeon", method = "update")
public class DungeonUpdateHooks {
    public static void Prefix(AbstractDungeon __instance) {
        BaseMod.publishPreDungeonUpdate();
    }

    public static void Postfix(AbstractDungeon __instance) {
        BaseMod.publishPostDungeonUpdate();
    }
}
