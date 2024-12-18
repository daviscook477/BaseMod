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
        //Only notable functionality change would probably be that mode is not set to SPLASH yet. Unlikely to cause an issue?
        //PostInitialize is only expected to happen once, so the value of mode is unlikely to be a factor.
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
