package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenderDescriptionEnergy
{
    private static Pattern r = Pattern.compile("\\[([RGBE])\\](\\.?) ");

    @SpirePatch(
            cls="com.megacrit.cardcrawl.cards.AbstractCard",
            method="renderDescription"
    )
    public static class AlterTmp
    {
        @SpireInsertPatch(
                rloc=31,
                localvars={"tmp"}
        )
        public static void Insert(AbstractCard __instance, SpriteBatch sb, @ByRef String[] tmp)
        {
            Matcher m = r.matcher(tmp[0]);
            if (m.find()) {
                tmp[0] = "[E]" + (m.group(2).equals(".") ? "." : "") + " ";
            }
        }
    }

    @SpirePatch(
            cls="com.megacrit.cardcrawl.cards.AbstractCard",
            method="renderDescription"
    )
    public static class RenderSmallEnergyOrb
    {
        private static final float CARD_ENERGY_IMG_WIDTH = 24.0f * Settings.scale;

        @SpireInsertPatch(
                rloc=185,
                localvars={"spacing", "i", "start_x", "draw_y", "font", "textColor", "tmp", "gl"}
        )
        public static void Insert(AbstractCard __instance, SpriteBatch sb, float spacing, int i, @ByRef float[] start_x, float draw_y,
                                  BitmapFont font, Color textColor, @ByRef String[] tmp, GlyphLayout gl)
        {
            Matcher m = r.matcher(tmp[0]);
            if (m.find()) {
                gl.width = CARD_ENERGY_IMG_WIDTH * __instance.drawScale;
                float tmp2 = (__instance.description.size() - 4) * spacing;
                __instance.renderSmallEnergy(sb, BaseMod.getCardSmallEnergy(__instance),
                        (start_x[0] - __instance.current_x) / Settings.scale / __instance.drawScale,
                        -tmp2 - 172.0f + CARD_ENERGY_IMG_WIDTH * __instance.drawScale + i * spacing * 2.0f);
                if (m.group(2).equals(".")) {
                    FontHelper.renderRotatedText(sb, font, ".",
                            __instance.current_x, __instance.current_y,
                            start_x[0] - __instance.current_x + CARD_ENERGY_IMG_WIDTH * __instance.drawScale,
                            i * 1.45f * -font.getCapHeight() + draw_y - __instance.current_y - 6.0f,
                            __instance.angle, true, textColor);
                }
                start_x[0] += gl.width;
                tmp[0] = "";
            }
        }
    }

    @SpirePatch(
            cls="com.megacrit.cardcrawl.cards.AbstractCard",
            method="initializeDescription"
    )
    public static class AlterEnergyKeyword
    {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"word"}
        )
        public static void Insert(AbstractCard __instance, String word)
        {
            if (word.equals("[E]") && !__instance.keywords.contains("[E]")) {
                __instance.keywords.add("[E]");
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
            {
                com.evacipated.cardcrawl.modthespire.lib.Matcher finalMatcher = new com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher(
                        "java.lang.String", "toLowerCase");

                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<com.evacipated.cardcrawl.modthespire.lib.Matcher>(), finalMatcher);
            }
        }

        public static void Postfix(AbstractCard __instance)
        {
            int[] idxs = new int[3];
            idxs[0] = __instance.keywords.indexOf("[R]");
            idxs[1] = __instance.keywords.indexOf("[G]");
            idxs[2] = __instance.keywords.indexOf("[B]");

            int idx = Integer.MAX_VALUE;
            for (int i : idxs) {
                if (i >= 0 && i < idx) {
                    idx = i;
                }
            }

            if (idx >= 0 && idx != Integer.MAX_VALUE) {
                if (!__instance.keywords.contains("[E]")) {
                    __instance.keywords.add(idx, "[E]");
                }
                __instance.keywords.remove("[R]");
                __instance.keywords.remove("[G]");
                __instance.keywords.remove("[B]");
            }
        }
    }
}
