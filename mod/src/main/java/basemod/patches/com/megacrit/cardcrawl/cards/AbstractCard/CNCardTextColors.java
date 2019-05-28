package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DescriptionLine;
import com.megacrit.cardcrawl.helpers.FontHelper;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.lang.reflect.Field;
import java.util.ArrayList;

@SpirePatch(
        clz=AbstractCard.class,
        method="initializeDescriptionCN"
)
public class CNCardTextColors {
    @SpireInsertPatch(
            locator = Locator.class,
            localvars = {"word", "currentWidth", "numLines", "CN_DESC_BOX_WIDTH"}
    )
    public static void Insert(AbstractCard __instance, @ByRef String[] word, @ByRef float[] currentWidth, @ByRef int[] numLines, float CN_DESC_BOX_WIDTH) {
        StringBuilder sbuilderRFHacks = (StringBuilder) ReflectionHacks.getPrivateStatic(AbstractCard.class, "sbuilder");

        if (word[0].startsWith("[#") && word[0].endsWith("[]")) {
            String wordTrim = word[0].substring(9, word[0].length() - 2);
            String buffer = "一";
            float wordWidth = new GlyphLayout(FontHelper.cardDescFont_N, wordTrim).width;
            float bufferWidth = new GlyphLayout(FontHelper.cardDescFont_N, buffer).width;
            float limitPlusBuffer = CN_DESC_BOX_WIDTH + bufferWidth;
            if (currentWidth[0] + wordWidth > limitPlusBuffer) {
                ++numLines[0];
                __instance.description.add(new DescriptionLine(sbuilderRFHacks.toString(), currentWidth[0]));
                sbuilderRFHacks = new StringBuilder();
                currentWidth[0] = wordWidth;
                sbuilderRFHacks.append(word[0]);
            } else {
                currentWidth[0] += wordWidth;
                sbuilderRFHacks.append(word[0]);
            }
            word[0] = "";
        }
    }

    public static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(String.class, "trim");

            return new int[] {LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[1]};
        }
    }
}
