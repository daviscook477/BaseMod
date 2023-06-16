package basemod.patches.com.megacrit.cardcrawl.actions.GameActionManager;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import javassist.CannotCompileException;
import javassist.CtBehavior;

@SpirePatch(
        clz = GameActionManager.class,
        method = "cleanCardQueue"
)
public class NoFadeLimbo {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static SpireReturn<Void> youCanStillRemoveTheCardsInHandThough(GameActionManager __instance) {
        return SpireReturn.Return();
    }

    private static class Locator extends SpireInsertLocator
    {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "limbo");

            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
