package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.PostDungeonInitializeEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

@SpirePatch(
        clz=AbstractDungeon.class,
        method="initializeRelicList"
)
public class PostDungeonInitializeHook {
    public static void Postfix(AbstractDungeon __instance) {
        BaseMod6.EVENT_BUS.post(new PostDungeonInitializeEvent());
        BaseMod.publishPostDungeonInitialize();
    }
}
