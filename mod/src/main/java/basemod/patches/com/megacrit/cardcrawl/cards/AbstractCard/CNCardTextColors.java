package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DescriptionLine;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

// This patch, solves the issue that colored text [color]text[] is set to a wrong width for non alphabetic languages by AbstractCard.initializeDescriptionCN() .
// The vanilla code checks the text block if it starts with an "[" for energy icon, but unfortunately colored text is of the form [color]text[] , so its width will be set
// to the energy orb's. In this patch we insert a check before that happens and skip the following code by setting the current text block to empty.
// Together with the patch FixInitializeDescriptionCNWidthLogic, colored text will be rendered correctly.
// For a comparison before and after the patch, visit https://github.com/daviscook477/BaseMod/pull/427

@SpirePatch(
        clz=AbstractCard.class,
        method="initializeDescriptionCN"
)
public class CNCardTextColors {
    @SpireInsertPatch(
            locator = Locator.class,
            localvars = {"word", "currentWidth", "numLines", "sbuilder", "CN_DESC_BOX_WIDTH"}
    )
    public static void Insert(AbstractCard __instance, @ByRef String[] word, @ByRef float[] currentWidth, @ByRef int[] numLines, @ByRef StringBuilder[] sbuilder, float CN_DESC_BOX_WIDTH) {
        if (word[0].startsWith("[#") && word[0].endsWith("[]")) {
            String wordTrim = word[0].substring(9, word[0].length() - 2).trim();
            String colorTag = word[0].substring(0, 9);
            char[] lettersOfWord = wordTrim.toCharArray();
            int wordLength = lettersOfWord.length;
            // using the same logic of the last else branch of the main if else branch from the original function, except inserts a pair of
            // inserting a pair of color tags at the side of each char .
            for(int i = 0; i < wordLength; ++i) {
                char letter = lettersOfWord[i];
                float letterWidth = new GlyphLayout(FontHelper.cardDescFont_N, String.valueOf(letter)).width;
                sbuilder[0].append(" " + colorTag).append(letter).append("[]");
                if (!Settings.manualLineBreak) {
                    // If the current line is reaching its end, then the ending punctuation's width should be ignored or it would result in
                    // a new line with a single punctuation as the text
                    if( (letter == LocalizedStrings.PERIOD.charAt(0)) && ((currentWidth[0] + letterWidth)  >= 0.8 * CN_DESC_BOX_WIDTH)  ){
                        letterWidth = 0F;
                    }
                    if((letter == '，') && ((currentWidth[0] + letterWidth)  >= 0.8 * CN_DESC_BOX_WIDTH ) ){
                        letterWidth = 0F;
                    }
                    if((letter == '、') && ((currentWidth[0] + letterWidth)  >= 0.8 * CN_DESC_BOX_WIDTH  )){
                        letterWidth = 0F;
                    }
                    if (currentWidth[0] + letterWidth > CN_DESC_BOX_WIDTH) {
                        ++numLines[0];
                        __instance.description.add(new DescriptionLine(sbuilder[0].toString(), currentWidth[0]));
                        sbuilder[0].setLength(0);
                        currentWidth[0] = letterWidth;
                    } else {
                        currentWidth[0] += letterWidth;
                    }
                }
            }
            sbuilder[0].append(" ");
            word[0] = ""; // setting to empty to skip the following code because the block of colored text is handled already here
        }
    }

    public static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(String.class, "equals");

            return new int[] {LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[0]};
        }
    }
}
