package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import basemod.abstracts.DynamicVariable;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.List;

@SpirePatch(
        clz=AbstractCard.class,
        method="initializeDescription"
)
public class FixDescriptionWidthCustomDynamicVariable
{
    @SpireInsertPatch(
            locator = KeywordLocator.class,
            localvars={"word"}
    )
    public static void uniqueKeywords(AbstractCard __instance, @ByRef String[] word) {
        if (BaseMod.keywordIsUnique(word[0].toLowerCase())) {
            String prefix = BaseMod.getKeywordPrefix(word[0].toLowerCase());
            word[0] = removeLowercasePrefix(word[0], prefix);
        }
    }

    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"gl", "word", "sbuilder2"}
    )
    public static void Insert(AbstractCard __instance, @ByRef GlyphLayout[] gl, @ByRef String[] word, StringBuilder sbuilder2)
    {
        //normal variables as !_! end up with:
        // gl.setText(FontHelper.cardDescFont_N, word)
        // The last ! is ignored for length.
        //custom variables or variables with extra characters after will end up with:
        // gl.setText(FontHelper.cardDescFont_N, word + sbuilder2);
        // The last ! is included in length.

        //This code overrides both cases.

        java.util.regex.Matcher matcher = DynamicVariable.variablePattern.matcher(word[0]);
        if (matcher.find()) {
            //Contains a full !something! and maybe more text on the start/end
            String pre = matcher.group(1);
            String key = matcher.group(2);
            String end = matcher.group(3);
            if (BaseMod.cardDynamicVariableMap.containsKey(key)) {
                //Actually is a variable
                gl[0].setText(FontHelper.cardDescFont_N, pre + "!D" + end + sbuilder2);
            }
        }
        else if (word[0].startsWith("!")) {
            //No closing !, so it's probably an isolated variable with a space on both ends
            gl[0].setText(FontHelper.cardDescFont_N, "!D");
        }
    }

    public static String removeLowercasePrefix(String base, String prefix) {
        if (prefix.length() > base.length())
            return base;

        for (int i = 0; i < prefix.length(); ++i) {
            if (Character.toLowerCase(base.charAt(i)) != Character.toLowerCase(prefix.charAt(i))) {
                return base;
            }
        }
        return base.substring(prefix.length());
    }

    private static class KeywordLocator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(GlyphLayout.class, "reset");
            List<Matcher> preMatches = new ArrayList<>();
            preMatches.add(new Matcher.FieldAccessMatcher(GameDictionary.class, "keywords"));
            return LineFinder.findInOrder(ctMethodToPatch, preMatches, finalMatcher);
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "DESC_BOX_WIDTH");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
