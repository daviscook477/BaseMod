package basemod.patches.com.megacrit.cardcrawl.helpers.CardLibrary;

import basemod.abstracts.CustomCard;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch2(
        clz = CardLibrary.class,
        method = "getCopy",
        paramtypez = {String.class, int.class, int.class}
)
public class MiscLoadedPatch {

    @SpireInsertPatch
    public static void onLoadMisc(AbstractCard ___retVal) {
        if (___retVal instanceof CustomCard) {
            ((CustomCard)___retVal).onLoadedMisc();
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "cardID");

            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
        }
    }

}
