package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.PostDungeonUpdateEvent;
import basemod6.events.PreDungeonUpdateEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

@SpirePatch(
        clz=AbstractDungeon.class,
        method="update"
)
public class DungeonUpdateHooks {
    public static void Prefix(AbstractDungeon __instance) {
        BaseMod6.EVENT_BUS.post(new PreDungeonUpdateEvent());
        BaseMod.publishPreDungeonUpdate();
    }

    public static void Postfix(AbstractDungeon __instance) {
        BaseMod6.EVENT_BUS.post(new PostDungeonUpdateEvent());
        BaseMod.publishPostDungeonUpdate();
    }
}
