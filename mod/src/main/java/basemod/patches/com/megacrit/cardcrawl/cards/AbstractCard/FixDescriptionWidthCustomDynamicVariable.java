package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.FontHelper;
import javassist.CtBehavior;

@SpirePatch(
        clz=AbstractCard.class,
        method="initializeDescription"
)
public class FixDescriptionWidthCustomDynamicVariable
{
    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"gl", "word"}
    )
    public static void Insert(AbstractCard __instance, @ByRef GlyphLayout[] gl, @ByRef String[] word)
    {
        if (BaseMod.keywordIsUnique(word[0].toLowerCase())) {
            String prefix = BaseMod.getKeywordPrefix(word[0].toLowerCase());
            word[0] = removeLowercasePrefix(word[0], prefix);
            gl[0].width -= (new GlyphLayout(FontHelper.cardDescFont_N, prefix)).width;
        }
        else if (word[0].startsWith("!")) {
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
