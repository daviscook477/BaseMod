package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import basemod.BaseMod;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.ShrinkLongDescription;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;


public class RenderDescriptionEnergy
{
    // This class handles rendering [E] energy symbols on cards in portrait view.
    // Unfortunately, javassist doesn't allow Raw patches with `continue`.
    // So instead we hack our way around it, by reusing the code for testing for and drawing the "[G]" mana symbol.
    @SpirePatch(
            clz=SingleCardViewPopup.class,
            method="renderDescription"
    )
    public static class RenderCustomEnergy {
        public static ExprEditor Instrument()
        {
            return new ExprEditor() {
                int redOrb = 0, greenOrb = 0;

                public void edit(MethodCall m) throws CannotCompileException
                {
                    if (redOrb >= 2 && greenOrb < 2 && m.getClassName().equals(String.class.getName()) && m.getMethodName().equals("equals")) {
                        m.replace("$_ = " + RenderDescriptionEnergy.class.getName() + ".replaceEquals(tmp, (java.lang.String)$1);");
                    }
                    else if ("renderSmallEnergy".equals(m.getMethodName())) {
                        m.replace(
                                "$proceed($1, $2, $3, " + //sb, image, and x are all fine.
                                        "(draw_y + (float)i * 1.53F * -font.getCapHeight() - 12f - current_y" +
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
                public void edit(FieldAccess m) throws CannotCompileException
                {
                    if (m.getClassName().equals(AbstractCard.class.getName())) {
                        if (m.getFieldName().equals("orb_green")) {
                            m.replace("$_ = " + RenderDescriptionEnergy.class.getName() + ".replaceOrbField(tmp, this.card);");
                            ++greenOrb;
                        }
                        else if (m.getFieldName().equals("orb_red")) {
                            ++redOrb;
                        }
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz=SingleCardViewPopup.class,
            method="renderDescriptionCN"
    )
    public static class RenderCustomEnergyCN {
        public static ExprEditor Instrument()
        {
            return new ExprEditor() {
                int redOrb = 0, greenOrb = 0;

                public void edit(MethodCall m) throws CannotCompileException
                {
                    if (redOrb >= 1 && greenOrb < 1 && m.getClassName().equals(String.class.getName()) && m.getMethodName().equals("equals")) {
                        m.replace("$_ = " + RenderDescriptionEnergy.class.getName() + ".replaceEquals(tmp, (java.lang.String)$1);");
                    }
                    else if ("renderSmallEnergy".equals(m.getMethodName())) {
                        m.replace(
                                "$proceed($1, $2, $3, " + //sb, image, and x are all fine.
                                        "(draw_y + (float)i * 1.53F * -font.getCapHeight() - 12f - current_y" +
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
                public void edit(FieldAccess m) throws CannotCompileException
                {
                    if (m.getClassName().equals(AbstractCard.class.getName())) {
                        if (m.getFieldName().equals("orb_green")) {
                            m.replace("$_ = " + RenderDescriptionEnergy.class.getName() + ".replaceOrbField(tmp, this.card);");
                            ++greenOrb;
                        }
                        else if (m.getFieldName().equals("orb_red")) {
                            ++redOrb;
                        }
                    }
                }
            };
        }
    }

    @SuppressWarnings("unused")
    public static boolean replaceEquals(String tmp, String originalArg)
    {
        if (tmp.equals(originalArg)) {
            return true;
        }
        if (tmp.length() != originalArg.length())
            return false;
        for (int i = 0; i < tmp.length(); ++i) {
            if (i == 1) {
                if (tmp.charAt(i) != 'E' || originalArg.charAt(i) != 'G')
                    return false;
            }
            else {
                if (tmp.charAt(i) != originalArg.charAt(i))
                    return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unused")
    public static TextureAtlas.AtlasRegion replaceOrbField(String tmp, Object card)
    {
        if (tmp.charAt(1) == 'E') { // tmp.equals("[E]") || tmp.equals("[E] ") || tmp.equals("[E]. ")) {
            return BaseMod.getCardSmallEnergy((AbstractCard)card);
        }
        return AbstractCard.orb_green;
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "renderSmallEnergy"
    )
    public static class RemoveSingleSmallEnergyOffsetExtraScaling {
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
                                "x = x / ((Float)" + basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.ShrinkLongDescription.Scale.class.getName() + ".descriptionScale.get(card)).floatValue();" +
                                        "y = (y / ((Float)" + ShrinkLongDescription.Scale.class.getName() + ".descriptionScale.get(card)).floatValue()) - 28;" +
                                        //Offset is added last as it just needs to have all the scales applied normally
                                        "$proceed($1, " +
                                        "current_x + x + region.offsetX, " +
                                        "current_y + y + region.offsetY, " +
                                        "-x - region.offsetX, -y - region.offsetY, " + //Use x and y as offset, meaning the scaling point is the center of the card
                                        "$6, $7, " + //region width and height
                                        "$8 * ((Float)" + ShrinkLongDescription.Scale.class.getName() + ".descriptionScale.get(card)).floatValue(), " +
                                        "$9 * ((Float)" + ShrinkLongDescription.Scale.class.getName() + ".descriptionScale.get(card)).floatValue(), " +
                                        "$10, " + //Angle is fixed at just 0
                                        "$11, $12, $13, $14, $15, $16);" //region info
                        );
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz=SingleCardViewPopup.class,
            method="renderDescription"
    ) //CN doesn't have the special . case or weird scaling on [R] so no changes needed
    public static class AdjustEnergyWidth {

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
                                f.replace("$proceed(card_energy_w" +
                                        " * drawScale" +
                                        " * ((Float)" + ShrinkLongDescription.Scale.class.getName() + ".descriptionScale.get(card)).floatValue());");
                            }
                            else if (glWidthSetIndex == 1) {
                                //Specifically for red energy. The original becomes way too wide at low scale.
                                f.replace(
                                        "gl.setText(font, \" \");" +
                                                "gl.width = gl.width + card_energy_w" +
                                                " * drawScale" +
                                                " * ((Float)" + ShrinkLongDescription.Scale.class.getName() + ".descriptionScale.get(card)).floatValue();");
                            }
                            else { //glWidthSetIndex % 2 == 1
                                //With period.
                                f.replace(
                                        "gl.setText(font, " + basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.RenderDescriptionEnergy.AdjustEnergyWidth.class.getName() + ".PERIOD_SPACE);" +
                                                "$proceed(gl.width + card_energy_w" +
                                                " * drawScale" +
                                                " * ((Float)" + ShrinkLongDescription.Scale.class.getName() + ".descriptionScale.get(card)).floatValue());");
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
                        if (renderRotatedIndex >= 2 && renderedSmallEnergy) {
                            m.replace(
                                    "$proceed($1, $2, $3, $4, $5, " +
                                            "start_x - current_x + " +
                                            "card_energy_w * drawScale * ((Float)" + ShrinkLongDescription.Scale.class.getName() + ".descriptionScale.get(card)).floatValue(), " +
                                            "$7, $8, $9, $10);"
                            );
                            ++adjusted;
                            renderedSmallEnergy = false;
                        }
                        ++renderRotatedIndex;
                    }
                    else if ("renderSmallEnergy".equals(m.getMethodName()) && m.getClassName().equals(SingleCardViewPopup.class.getName())) {
                        renderedSmallEnergy = true;
                    }
                }
            };
        }
    }
}
