package basemod.patches.com.megacrit.cardcrawl.core.CardCrawlGame;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import javassist.CtBehavior;

@SpirePatch(cls="com.megacrit.cardcrawl.core.CardCrawlGame", method="create")
public class PostInitializeHook {
    public static void Postfix(Object __obj_instance) {
        //Leaving for compatibility? Method call is removed, but for a single line method it's unlikely someone relied on its presence
    }

    @SpireInsertPatch(
            locator = WithinTryLocator.class
    )
    public static void PostInitialize(CardCrawlGame __instance) {
        // Manually set mode to SPLASH since insert now happens before that gets set in the original method
        // Not setting it would be unlikely to cause issues, but might as well to be safe
        CardCrawlGame.mode = CardCrawlGame.GameMode.SPLASH;
        BaseMod.publishPostInitialize();
    }

    private static class WithinTryLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardCrawlGame.class, "mode");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
