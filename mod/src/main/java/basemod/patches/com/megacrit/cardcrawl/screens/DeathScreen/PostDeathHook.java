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
public class PostDeathHook
{
    public static void Postfix(DeathScreen __instance, Object __monster_group)
    {
        BaseMod6.EVENT_BUS.post(new PostDeathEvent());
        BaseMod.publishPostDeath();
    }
}
