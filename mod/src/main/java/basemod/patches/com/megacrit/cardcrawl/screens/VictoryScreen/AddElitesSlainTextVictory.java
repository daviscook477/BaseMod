package basemod.patches.com.megacrit.cardcrawl.screens.VictoryScreen;

import basemod.patches.com.megacrit.cardcrawl.screens.DeathScreen.AddElitesSlainTextDeath;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        clz = VictoryScreen.class,
        method = "createGameOverStats"
)
public class AddElitesSlainTextVictory {

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void Insert(VictoryScreen __instance) {
        AddElitesSlainTextDeath.doThing(__instance.stats);
    }

    public static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "add");

            return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[5]};
        }
    }
}
