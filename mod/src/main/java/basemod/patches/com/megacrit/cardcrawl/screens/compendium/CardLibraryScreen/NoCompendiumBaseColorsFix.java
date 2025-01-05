package main.java.basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;

import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.NoCompendium;
import javassist.CannotCompileException;
import javassist.CtBehavior;

@SpirePatch2(clz = CardLibraryScreen.class, method = "initialize")
public class NoCompendiumBaseColorsFix {

    @SpireInsertPatch(locator = Locator.class)
    public static void Insert(
            CardGroup ___redCards,
            CardGroup ___greenCards,
            CardGroup ___blueCards,
            CardGroup ___purpleCards,
            CardGroup ___colorlessCards,
            CardGroup ___curseCards) {
        ___redCards.group.removeIf(c -> c.getClass().isAnnotationPresent(NoCompendium.class));
        ___greenCards.group.removeIf(c -> c.getClass().isAnnotationPresent(NoCompendium.class));
        ___blueCards.group.removeIf(c -> c.getClass().isAnnotationPresent(NoCompendium.class));
        ___purpleCards.group.removeIf(c -> c.getClass().isAnnotationPresent(NoCompendium.class));
        ___colorlessCards.group.removeIf(c -> c.getClass().isAnnotationPresent(NoCompendium.class));
        ___curseCards.group.removeIf(c -> c.getClass().isAnnotationPresent(NoCompendium.class));
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(CardLibraryScreen.class, "calculateScrollBounds");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
