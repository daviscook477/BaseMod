package basemod.patches.com.megacrit.cardcrawl.core.CardCrawlGame;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.EditPotionsEvent;
import basemod6.events.PostInitEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;

@SpirePatch(
        clz=CardCrawlGame.class,
        method="create"
)
public class PostInitializeHook {
    public static void Postfix(CardCrawlGame __instance) {
        BaseMod6.EVENT_BUS.post(new EditPotionsEvent());
        BaseMod6.EVENT_BUS.post(new PostInitEvent());
        BaseMod.publishPostInitialize();
    }
}
