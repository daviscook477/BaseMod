package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

// This patch, corrects the incorrect text rendering issue for non alphabetic languages(ZHS, JPN, KOR etc) when there exists double space in cards.json
// For a comparison before and after the patch, visit https://github.com/daviscook477/BaseMod/pull/427

public class FixInitializeDescriptionCNWidthLogic {

    private static float currentWidthStore = -999F; // Initializing to an impossible value for checks, if it becomes any other value it means the value got updated.

    @SpirePatch(
            clz = AbstractCard.class,
            method = "initializeDescriptionCN"
    )
    public static class InsertBefore {
        @SpireInsertPatch(
                rloc = 14,
                localvars = {"currentWidth"}
        )
        public static void Insert(AbstractCard __instance, @ByRef float[] currentWidth) {
            currentWidthStore = currentWidth[0];     // insert before the assignemnt currentWidth = 0; to store the original width
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "initializeDescriptionCN"
    )
    public static class InsertAfter {
        @SpireInsertPatch(
                rloc = 15,
                localvars = {"currentWidth","word","sbuilder"}
        )
        public static void Insert(AbstractCard __instance, @ByRef float[] currentWidth, @ByRef String[] word, @ByRef StringBuilder[] sbuilder) {
            if(sbuilder[0].length() != 0 && currentWidthStore != -999F)  currentWidth[0] = currentWidthStore ; // restores the width back if the width was set to 0 incorrectly
        }
    }


}
