package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import javassist.CtBehavior;

import java.util.ArrayList;

// This fixes an issue that is present in the base game. In the separate logic for Chinese-language text parsing,
// if the first word of a card description is a keyword, the first line of card text ends up being the empty string.
// In Traditional Chinese, LocalizedString.PERIOD is also the empty string, which causes the logic that tries to fix
// cases like this to crash due to try to access array index -1. (I suspect the logic is intended for fixing periods at
// the end of the description that would otherwise spill over to their own line, which explains why it doesn't account
// for finding lines like this at the start of the string).
// Simplified Chinese does not have this issue because LocalizedString.PERIOD is 。(in theory, if a card description
// started with a 。, the same problem would happen).
// To fix this, we insert our own code to remove lines at the start of the description that equal the localized period
// string. That removes the meaningless lines and lets the rest of the logic run without crashing for all Chinese text.
@SpirePatch(clz = AbstractCard.class, method = "initializeDescriptionCN")
public class PreventCrashDueToKeywordAtStartOfCardTextForTraditionalChinesePatch {
    @SpireInsertPatch(locator = Locator.class)
    public static void preventCrash(AbstractCard __instance) {
        while (__instance.description.size() > 0 && __instance.description.get(0).text.equals(LocalizedStrings.PERIOD)) {
            __instance.description.remove(0);
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "size");
            return LineFinder.findInOrder(ctBehavior, finalMatcher);
        }
    }
}
