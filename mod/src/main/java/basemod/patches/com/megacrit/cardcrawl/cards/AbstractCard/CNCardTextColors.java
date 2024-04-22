package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DescriptionLine;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

// This patch, solves the issue that colored text [color]text[] is set to a wrong width for non alphabetic languages by AbstractCard.initializeDescriptionCN() .
// The vanilla code checks the text block if it starts with an "[" for energy icon, but unfortunately colored text is of the form [color]text[] , so its width wil be set
// to the energy orb's. In this patch we insert a check before that happens and skip the following code by setting the current text block to empty.
// Together with the patch FixInitializeDescriptionCNWidthLogic, colored text will be rendered correctly.
// For a comparison before and after the patch, visit


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
            String wordTrim = word[0].substring(9, word[0].length() - 2);
            float wordWidth = new GlyphLayout(FontHelper.cardDescFont_N, wordTrim).width;

            // Vanilla automatically ignores ending punctuation width, but it's trikier to do so with color tags at sides, so instead we manually detect ending
            // punctuations(by checking if the combined string crosses some threshold so the punctuation in the new block of text will likely be an ending one)
            // and ignore its width(by subtracting the extra counted width)
            if(wordTrim.contains(LocalizedStrings.PERIOD) && (currentWidth[0] + wordWidth)  >= 0.8 * CN_DESC_BOX_WIDTH  ){
                float periodWidth = new GlyphLayout(FontHelper.cardDescFont_N, LocalizedStrings.PERIOD).width;
                wordWidth -= periodWidth;
            }
            if(wordTrim.contains("，") && (currentWidth[0] + wordWidth)  >= 0.8 * CN_DESC_BOX_WIDTH  ){
                float periodWidth = new GlyphLayout(FontHelper.cardDescFont_N, "，").width;
                wordWidth -= periodWidth;
            }
            if(wordTrim.contains("、") && (currentWidth[0] + wordWidth)  >= 0.8 * CN_DESC_BOX_WIDTH  ){
                float periodWidth = new GlyphLayout(FontHelper.cardDescFont_N, "、").width;
                wordWidth -= periodWidth;
            }
            if (currentWidth[0] + wordWidth > CN_DESC_BOX_WIDTH) {
                ++numLines[0];
                __instance.description.add(new DescriptionLine(sbuilder[0].toString(), currentWidth[0]));
                sbuilder[0].setLength(0);
                currentWidth[0] = wordWidth;
                sbuilder[0].append(word[0]);
            } else {
                currentWidth[0] += wordWidth;
                sbuilder[0].append(word[0]);
            }
            word[0] = "";
        }
    }

    public static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(String.class, "equals");

            return new int[] {LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[0]};
        }
    }
}
