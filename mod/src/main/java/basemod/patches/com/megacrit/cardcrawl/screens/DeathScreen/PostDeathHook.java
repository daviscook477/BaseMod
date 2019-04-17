package basemod.patches.com.megacrit.cardcrawl.screens.DeathScreen;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.PostDeathEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cutscenes.Cutscene;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.VictoryScreen;

@SpirePatch(
        clz=DeathScreen.class,
        method=SpirePatch.CONSTRUCTOR
)
@SpirePatch(
        clz=VictoryScreen.class,
        method=SpirePatch.CONSTRUCTOR
)
public class PostDeathHook
{
    private static boolean doHook = true;

    public static void Postfix(Object __obj_instance, Object __monster_group)
    {
        if (__obj_instance instanceof VictoryScreen && !doHook) {
            return;
        }

        BaseMod6.EVENT_BUS.post(new PostDeathEvent());
        BaseMod.publishPostDeath();
    }

    @SpirePatch(
            clz=Cutscene.class,
            method="openVictoryScreen"
    )
    public static class StopMultiVictory
    {
        public static void Prefix(Cutscene __instance)
        {
            doHook = (AbstractDungeon.victoryScreen == null);
        }
    }
}
