package basemod.patches.com.megacrit.cardcrawl.core.CardCrawlGame;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.PostInitEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(cls="com.megacrit.cardcrawl.core.CardCrawlGame", method="create")
public class PostInitializeHook {
    public static void Postfix(Object __obj_instance) {
        BaseMod6.EVENT_BUS.post(new PostInitEvent());
        BaseMod.publishPostInitialize();
    }
}
