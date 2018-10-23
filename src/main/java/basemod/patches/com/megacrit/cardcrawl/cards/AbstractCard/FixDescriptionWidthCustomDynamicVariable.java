package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.FontHelper;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        cls="com.megacrit.cardcrawl.cards.AbstractCard",
        method="initializeDescription"
)
public class FixDescriptionWidthCustomDynamicVariable
{
    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"gl", "word"}
    )
    public static void Insert(AbstractCard __instance, @ByRef GlyphLayout[] gl, String word)
    {
        if (word.startsWith("!")) {
            gl[0] = new GlyphLayout(FontHelper.cardDescFont_N, "!D");
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher("com.megacrit.cardcrawl.cards.AbstractCard", "DESC_BOX_WIDTH");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
        }
    }
}
