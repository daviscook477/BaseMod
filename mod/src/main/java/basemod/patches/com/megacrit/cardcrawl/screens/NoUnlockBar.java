package basemod.patches.com.megacrit.cardcrawl.screens;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.Expectation;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.GameOverScreen;
import com.megacrit.cardcrawl.unlock.AbstractUnlock;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.Expr;
import javassist.expr.FieldAccess;

import java.util.ArrayList;

import static basemod.BaseMod.logger;

@SpirePatch(
        clz = GameOverScreen.class,
        method = "calculateUnlockProgress"
)
public class NoUnlockBar {
    @SpireInsertPatch(
            locator = Locator.class,
            localvars = { "unlockLevel", "maxLevel" }
    )
    public static SpireReturn<Void> unlockLimitCheck(GameOverScreen __instance, int unlockLevel, @ByRef boolean[] maxLevel)
    {
        try {
            ArrayList<AbstractUnlock> testBundle = UnlockTracker.getUnlockBundle(AbstractDungeon.player.chosenClass, unlockLevel);

            if (testBundle == null || testBundle.isEmpty())
            {
                maxLevel[0] = true;
                logger.info("Class has no more unlocks.");
                return SpireReturn.Return(null);
            }
            logger.info("Class has an unlock.");
        }
        catch (Exception e)
        {
            logger.error("Unexpected error while testing if player class has a valid unlock bundle.");
            e.printStackTrace();
        }
        return SpireReturn.Continue();
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher(Expectation.FIELD_ACCESS) {
                @Override
                public boolean match(Expr toMatch) {
                    FieldAccess expr = (FieldAccess)toMatch;
                    return expr.getFieldName().equals("unlockLevel");
                }
            };
            return new int[] { LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[1] };
        }
    }
}
