package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        clz=AbstractCard.class,
        method="initializeDescriptionCN"
)
public class CNUniqueKeywords {

    @SpireInsertPatch(
            locator = Locator.class,
            localvars = {"word"}
    )
    public static void Insert(AbstractCard __instance, @ByRef String[] word) {
        if (BaseMod.keywordIsUnique(word[0])) {
            word[0] = word[0].replaceFirst(BaseMod.getKeywordPrefix(word[0]), "");
        }
    }

    public static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(GlyphLayout.class, "setText");

            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
