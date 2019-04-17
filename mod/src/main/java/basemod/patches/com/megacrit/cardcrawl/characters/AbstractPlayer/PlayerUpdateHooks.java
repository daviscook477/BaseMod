package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.PostPlayerUpdateEvent;
import basemod6.events.PrePlayerUpdateEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

@SpirePatch(
        clz=AbstractPlayer.class,
        method="update"
)
public class PlayerUpdateHooks {
    public static void Prefix(AbstractPlayer __instance) {
    	BaseMod6.EVENT_BUS.post(new PrePlayerUpdateEvent(__instance));
        BaseMod.publishPrePlayerUpdate();
    }

    public static void Postfix(AbstractPlayer __instance) {
        BaseMod6.EVENT_BUS.post(new PostPlayerUpdateEvent(__instance));
        BaseMod.publishPostPlayerUpdate();
    }
}
