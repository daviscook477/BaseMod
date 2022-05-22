package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DescriptionLine;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenderDescriptionEnergy
{
    private static final Pattern r = Pattern.compile("\\[([RGBWE])](\\.?) ");

    //Energy Icon Positioning
    @SpirePatch(
            clz = AbstractCard.class,
            method = "renderSmallEnergy"
    )
    public static class AdjustSmallEnergyRendering {
        @SpireInstrumentPatch
        public static ExprEditor adjustParams()
        {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    //This method draws based on the bottom left corner of the icon
                    //x and y are passed in as offset from current_x and current_y to bottom left corner, ignoring card's scale and rotation.
                    //This patch removes scaling incorrectly applied to the region's offsetX.
                    if ("draw".equals(m.getMethodName())) {
                        m.replace(
                                "x = x / ((Float)" + ShrinkLongDescription.Scale.class.getName() + ".descriptionScale.get(this)).floatValue();" +
                                        "y = (y / ((Float)" + ShrinkLongDescription.Scale.class.getName() + ".descriptionScale.get(this)).floatValue()) - 26;" +
                                        //Offset is added last as it just needs to have all the scales applied normally
                                        "$proceed($1, " +
                                        "current_x + x + region.offsetX, " +
                                        "current_y + y + region.offsetY, " +
                                        "-x - region.offsetX, -y - region.offsetY, " + //Use x and y as offset, meaning the scaling point is the center of the card
                                        "$6, $7, " + //region width and height
                                        "$8 * ((Float)" + ShrinkLongDescription.Scale.class.getName() + ".descriptionScale.get(this)).floatValue(), " +
                                        "$9 * ((Float)" + ShrinkLongDescription.Scale.class.getName() + ".descriptionScale.get(this)).floatValue(), " +
                                        //Settings.scale * drawScale * custom basemod description scaling
                                        "angle, " + //Rotate around card center
                                        "$11, $12, $13, $14, $15, $16);" //region info
                        );
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "renderDescription"
    )
    public static class AdjustEnergyWidth {
        public static String PERIOD_SPACE = " "; //Updated in postInitialize

        @SpireInstrumentPatch
        public static ExprEditor adjustGlWidth()
        {
            return new ExprEditor() {
                int glWidthSetIndex = 0;
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.isWriter() && f.getFieldName().equals("width") && f.getClassName().equals(GlyphLayout.class.getName())) {
                        if (glWidthSetIndex < 8) {
                            if (glWidthSetIndex % 2 == 0) {
                                f.replace("gl.width = CARD_ENERGY_IMG_WIDTH" +
                                        " * drawScale" +
                                        " * ((Float)" + ShrinkLongDescription.Scale.class.getName() + ".descriptionScale.get(this)).floatValue();");
                            }
                            else if (glWidthSetIndex == 1) {
                                //Specifically for red energy. The original becomes way too wide at low scale.
                                f.replace(
                                        "gl.setText(font, \" \");" +
                                                "gl.width = gl.width + CARD_ENERGY_IMG_WIDTH" +
                                                " * drawScale" +
                                                " * ((Float)" + ShrinkLongDescription.Scale.class.getName() + ".descriptionScale.get(this)).floatValue();");
                            }
                            else { //glWidthSetIndex % 2 == 1
                                //With period.
                                f.replace(
                                        "gl.setText(font, " + AdjustEnergyWidth.class.getName() + ".PERIOD_SPACE);" +
                                                "gl.width = gl.width + CARD_ENERGY_IMG_WIDTH" +
                                                " * drawScale" +
                                                " * ((Float)" + ShrinkLongDescription.Scale.class.getName() + ".descriptionScale.get(this)).floatValue();");
                            }
                            ++glWidthSetIndex;
                        }
                    }
                }

                int renderRotatedIndex = 0, adjusted = 0;
                boolean renderedSmallEnergy = false;
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (adjusted >= 4)
                        return;

                    if (m.getMethodName().equals("renderRotatedText") && m.getClassName().equals(FontHelper.class.getName())) {
                        if (renderRotatedIndex >= 3 && renderedSmallEnergy) {
                            m.replace(
                                    "$proceed($1, $2, $3, $4, $5, " +
                                            "start_x - current_x + " +
                                            "CARD_ENERGY_IMG_WIDTH * this.drawScale * ((Float)" + ShrinkLongDescription.Scale.class.getName() + ".descriptionScale.get(this)).floatValue()," +
                                            "$7, $8, $9, $10);"
                            );
                            ++adjusted;
                            renderedSmallEnergy = false;
                        }
                        ++renderRotatedIndex;
                    }
                    else if ("renderSmallEnergy".equals(m.getMethodName()) && m.getClassName().equals(AbstractCard.class.getName())) {
                        renderedSmallEnergy = true;
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "renderDescriptionCN"
    )
    public static class AdjustCNEnergyWidth {
        @SpireInstrumentPatch
        public static ExprEditor adjustGlWidth()
        {
            return new ExprEditor() {
                int glWidthSetIndex = 0;
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.isWriter() && f.getFieldName().equals("width") && f.getClassName().equals(GlyphLayout.class.getName())) {
                        if (glWidthSetIndex < 4) {
                            f.replace("gl.width = CARD_ENERGY_IMG_WIDTH" +
                                    " * drawScale" +
                                    " * ((Float)" + ShrinkLongDescription.Scale.class.getName() + ".descriptionScale.get(this)).floatValue();");
                            ++glWidthSetIndex;
                        }
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz=AbstractCard.class,
            method="renderDescription"
    )
    @SpirePatch(
            clz=AbstractCard.class,
            method="renderDescriptionCN"
    )
    public static class RenderSmallEnergyOrb
    {
        private static final float CARD_ENERGY_IMG_WIDTH = 24.0f * Settings.scale;

        //Adjust standard energy rendering
        @SpireInstrumentPatch
        public static ExprEditor adjustParams()
        {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if ("renderSmallEnergy".equals(m.getMethodName())) {
                        m.replace(
                                "$proceed($1, $2, $3," + //sb and image, no change
                                        "(draw_y + (float)i * 1.45F * -font.getCapHeight() - 6f - current_y" +
                                        //offset y as used by FontHelper's render method. `-current_y` converts it to an offset.
                                        //An exact pixel offset that is already scaled.
                                        " + font.getCapHeight())" +
                                        //Extra upwards adjustment based on font height because of Stupid Text Rendering Code
                                        " / drawScale / " + Settings.class.getName() + ".scale" +
                                        //Un-scale it because the icon rendering utilizes scaling again.
                                        ");"
                        );
                    }
                }
            };
        }

        //Add custom energy rendering
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"spacing", "i", "start_x", "draw_y", "font", "textColor", "tmp", "gl"}
        )
        public static void Insert(AbstractCard __instance, SpriteBatch sb, float spacing, int i, @ByRef float[] start_x, float draw_y,
                                  BitmapFont font, Color textColor, @ByRef String[] tmp, GlyphLayout gl)
        {
            Matcher m = r.matcher(tmp[0]);
            if (tmp[0].equals("[E]") || m.find()) {
                __instance.renderSmallEnergy(sb, BaseMod.getCardSmallEnergy(__instance),
                        (start_x[0] - __instance.current_x) / Settings.scale / __instance.drawScale,
                        (draw_y + i * 1.45f * -font.getCapHeight() - 6f - __instance.current_y + font.getCapHeight()) / Settings.scale / __instance.drawScale);

                if (!tmp[0].equals("[E]") && m.group(2).equals(".")) {
                    FontHelper.renderRotatedText(sb, font, LocalizedStrings.PERIOD,
                            __instance.current_x, __instance.current_y,
                            start_x[0] - __instance.current_x + CARD_ENERGY_IMG_WIDTH * __instance.drawScale * ShrinkLongDescription.Scale.descriptionScale.get(__instance),
                            i * 1.45f * -font.getCapHeight() + draw_y - __instance.current_y - 6.0f,
                            __instance.angle, true, textColor);
                    gl.setText(font, AdjustEnergyWidth.PERIOD_SPACE);
                    gl.width += CARD_ENERGY_IMG_WIDTH * __instance.drawScale * ShrinkLongDescription.Scale.descriptionScale.get(__instance);
                }
                else {
                    gl.width = CARD_ENERGY_IMG_WIDTH * __instance.drawScale * ShrinkLongDescription.Scale.descriptionScale.get(__instance);
                }

                start_x[0] += gl.width;
                tmp[0] = "";
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception
            {
                com.evacipated.cardcrawl.modthespire.lib.Matcher matcher = new com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher(GlyphLayout.class, "setText");
                int[] lines = LineFinder.findAllInOrder(ctBehavior, new ArrayList<>(), matcher);
                return new int[]{lines[lines.length-1]}; // Only last occurrence
            }
        }
    }

    @SpirePatch(
            clz=AbstractCard.class,
            method="initializeDescriptionCN"
    )
    public static class FixEForChinese
    {
        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"word", "currentWidth", "sbuilder", "numLines", "CARD_ENERGY_IMG_WIDTH", "CN_DESC_BOX_WIDTH"}
        )
        public static void Insert(AbstractCard __instance, @ByRef String[] word, @ByRef float[] currentWidth,
                                  @ByRef StringBuilder[] currentLine, @ByRef int[] numLines,
                                  float CARD_ENERGY_IMG_WIDTH, float CN_DESC_BOX_WIDTH)
        {
            if (word[0].equals("[E]")) {
                if (currentWidth[0] + CARD_ENERGY_IMG_WIDTH > CN_DESC_BOX_WIDTH) {
                    ++numLines[0];
                    __instance.description.add(new DescriptionLine(currentLine[0].toString(), currentWidth[0]));
                    currentLine[0] = new StringBuilder();
                    currentWidth[0] = CARD_ENERGY_IMG_WIDTH;
                    currentLine[0].append(" ").append(word[0]).append(" ");
                } else {
                    currentLine[0].append(" ").append(word[0]).append(" ");
                    currentWidth[0] += CARD_ENERGY_IMG_WIDTH;
                }
                word[0] = "";
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
            {
                com.evacipated.cardcrawl.modthespire.lib.Matcher finalMatcher = new com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher(
                        String.class, "toCharArray");

                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz=AbstractCard.class,
            method="initializeDescription"
    )
    @SpirePatch(
            clz=AbstractCard.class,
            method="initializeDescriptionCN"
    )
    public static class InitializeDescriptionPatches
    {
        //Adjust width calculation
        @SpireInstrumentPatch
        public static ExprEditor adjustParams()
        {
            return new ExprEditor() {
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.isReader() && "CARD_ENERGY_IMG_WIDTH".equals(f.getFieldName()) && AbstractCard.class.getName().equals(f.getClassName())) {
                        f.replace("$_ = $proceed()" +
                                " * ((Float)" + ShrinkLongDescription.Scale.class.getName() + ".descriptionScale.get(this)).floatValue();");
                    }
                }
            };
        }

        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"word"}
        )
        public static void AlterEnergyKeyword(AbstractCard __instance, String word)
        {
            if ("[E]".equals(word) && !__instance.keywords.contains("[E]")) {
                __instance.keywords.add("[E]");
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
            {
                com.evacipated.cardcrawl.modthespire.lib.Matcher finalMatcher = new com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher(
                        String.class, "toLowerCase");

                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
