package basemod.patches.com.megacrit.cardcrawl.helpers.TipHelper;

import basemod.abstracts.AbstractCardModifier;
import basemod.abstracts.CustomCard;
import basemod.helpers.CardModifierManager;
import basemod.helpers.TooltipInfo;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.TipHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SpirePatch(
        clz=TipHelper.class,
        method="renderKeywords"
)
public class FakeKeywords
{
    private static float BODY_TEXT_WIDTH = 0;
    private static float TIP_DESC_LINE_SPACING = 0;
    private static float BOX_EDGE_H = 0;

    public static void Prefix(float x, @ByRef float[] y, SpriteBatch sb, ArrayList<String> keywords, AbstractCard ___card)
    {
        if (BODY_TEXT_WIDTH == 0) {
            getConstants();
        }

        float sumTooltipHeight = 0;

        // Calculate height of keyword tooltips
        for (String s : keywords) {
            if (GameDictionary.keywords.containsKey(s)) {
                float textHeight = -FontHelper.getSmartHeight(
                        FontHelper.tipHeaderFont, 
                        TipHelper.capitalize(s), 
                        BODY_TEXT_WIDTH, 
                        TIP_DESC_LINE_SPACING) -
                    FontHelper.getSmartHeight(
                        FontHelper.tipBodyFont,
                        GameDictionary.keywords.get(s),
                        BODY_TEXT_WIDTH,
                        TIP_DESC_LINE_SPACING) - 7.0f * Settings.scale;
                sumTooltipHeight -= textHeight + BOX_EDGE_H * 3.15f;
            }
        }

        // Calculate height of custom tooltips

        if (___card instanceof CustomCard) {
            CustomCard card = (CustomCard) ___card;
            List<TooltipInfo> tooltips = card.getCustomTooltips();
            List<TooltipInfo> pretooltips = card.getCustomTooltipsTop();
            if (tooltips != null && pretooltips != null) {
                tooltips.addAll(pretooltips);
            } else if (tooltips == null && pretooltips != null) {
                tooltips = pretooltips;
            }
            if (tooltips != null) {
                for (TooltipInfo tooltip : tooltips) {
                    float textHeight = -FontHelper.getSmartHeight(
                            FontHelper.tipHeaderFont, 
                            TipHelper.capitalize(tooltip.title), 
                            BODY_TEXT_WIDTH, 
                            TIP_DESC_LINE_SPACING) - 
	                    FontHelper.getSmartHeight(
	                        FontHelper.tipBodyFont,
	                        tooltip.description,
	                        BODY_TEXT_WIDTH,
	                        TIP_DESC_LINE_SPACING) - 7.0f * Settings.scale;
                     textHeight -= 
                    sumTooltipHeight -= textHeight + BOX_EDGE_H * 3.15f;
                }
            }
        }
        //Also add cardmod tip heights
        for (AbstractCardModifier modifier : CardModifierManager.modifiers(___card)) {
            List<TooltipInfo> tooltips = modifier.additionalTooltips(___card);
            if (tooltips != null) {
                for (TooltipInfo tooltip : tooltips) {
                    float textHeight = -FontHelper.getSmartHeight(
                            FontHelper.tipHeaderFont,
                            TipHelper.capitalize(tooltip.title),
                            BODY_TEXT_WIDTH,
                            TIP_DESC_LINE_SPACING) -
                            FontHelper.getSmartHeight(
                                    FontHelper.tipBodyFont,
                                    tooltip.description,
                                    BODY_TEXT_WIDTH,
                                    TIP_DESC_LINE_SPACING) - 7.0f * Settings.scale;
                    textHeight -=
                            sumTooltipHeight -= textHeight + BOX_EDGE_H * 3.15f;
                }
            }
        }

        sumTooltipHeight *= -1; // sumTooltipHeight is negative

        if (sumTooltipHeight > AbstractCard.IMG_HEIGHT) {
            y[0] += sumTooltipHeight - AbstractCard.IMG_HEIGHT;
        }

        // Cancel out the base game's hardcoded "solution"
        if (keywords.size() >= 4) {
            y[0] -= (keywords.size() - 1) * 62 * Settings.scale;
        }

        try {
            if (___card instanceof CustomCard) {
                CustomCard card = (CustomCard) ___card;
                List<TooltipInfo> tooltips = card.getCustomTooltipsTop();
                if (tooltips != null) {
                    for (TooltipInfo tooltip : tooltips) {
                        Field textHeight = TipHelper.class.getDeclaredField("textHeight");
                        textHeight.setAccessible(true);
                        float h = -FontHelper.getSmartHeight(
                                FontHelper.tipHeaderFont,
                                TipHelper.capitalize(tooltip.title),
                                BODY_TEXT_WIDTH,
                                TIP_DESC_LINE_SPACING) -
                                FontHelper.getSmartHeight(
                                        FontHelper.tipBodyFont,
                                        tooltip.description,
                                        BODY_TEXT_WIDTH,
                                        TIP_DESC_LINE_SPACING) - 7.0f * Settings.scale;
                        ;
                        textHeight.set(null, h);

                        Method renderTipBox = TipHelper.class.getDeclaredMethod("renderTipBox", float.class, float.class, SpriteBatch.class, String.class, String.class);
                        renderTipBox.setAccessible(true);
                        renderTipBox.invoke(null, x, y[0], sb, tooltip.title, tooltip.description);
                        y[0] -= h + BOX_EDGE_H * 3.15f;
                    }
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void Postfix(float x, float y, SpriteBatch sb, ArrayList<String> keywords)
    {
        if (BODY_TEXT_WIDTH == 0) {
            getConstants();
        }

        try {
            Field cardField = TipHelper.class.getDeclaredField("card");
            cardField.setAccessible(true);
            AbstractCard acard = (AbstractCard)cardField.get(null);
            if (acard instanceof CustomCard) {
                CustomCard card = (CustomCard)acard;
                List<TooltipInfo> tooltips = card.getCustomTooltips();
                if (tooltips != null) {
                    for (TooltipInfo tooltip : tooltips) {
                        Field textHeight = TipHelper.class.getDeclaredField("textHeight");
                        textHeight.setAccessible(true);
                        float h = -FontHelper.getSmartHeight(
                                FontHelper.tipHeaderFont, 
                                TipHelper.capitalize(tooltip.title), 
                                BODY_TEXT_WIDTH, 
                                TIP_DESC_LINE_SPACING) - 
                            FontHelper.getSmartHeight(
                                FontHelper.tipBodyFont,
                                tooltip.description,
                                BODY_TEXT_WIDTH,
                                TIP_DESC_LINE_SPACING) - 7.0f * Settings.scale;
                        textHeight.set(null, h);

                        Method renderTipBox = TipHelper.class.getDeclaredMethod("renderTipBox", float.class, float.class, SpriteBatch.class, String.class, String.class);
                        renderTipBox.setAccessible(true);
                        renderTipBox.invoke(null, x, y, sb, tooltip.title, tooltip.description);
                        y -= h + BOX_EDGE_H * 3.15f;
                    }
                }
            }
            for (AbstractCardModifier modifier : CardModifierManager.modifiers(acard)) {
                List<TooltipInfo> tooltips = modifier.additionalTooltips(acard);
                if (tooltips != null) {
                    for (TooltipInfo tooltip : tooltips) {
                        Field textHeight = TipHelper.class.getDeclaredField("textHeight");
                        textHeight.setAccessible(true);
                        float h = -FontHelper.getSmartHeight(
                                FontHelper.tipHeaderFont,
                                TipHelper.capitalize(tooltip.title),
                                BODY_TEXT_WIDTH,
                                TIP_DESC_LINE_SPACING) -
                                FontHelper.getSmartHeight(
                                        FontHelper.tipBodyFont,
                                        tooltip.description,
                                        BODY_TEXT_WIDTH,
                                        TIP_DESC_LINE_SPACING) - 7.0f * Settings.scale;
                        textHeight.set(null, h);

                        Method renderTipBox = TipHelper.class.getDeclaredMethod("renderTipBox", float.class, float.class, SpriteBatch.class, String.class, String.class);
                        renderTipBox.setAccessible(true);
                        renderTipBox.invoke(null, x, y, sb, tooltip.title, tooltip.description);
                        y -= h + BOX_EDGE_H * 3.15f;
                    }
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private static void getConstants()
    {
        try {
            Field f = TipHelper.class.getDeclaredField("BODY_TEXT_WIDTH");
            f.setAccessible(true);
            BODY_TEXT_WIDTH = f.getFloat(null);

            f = TipHelper.class.getDeclaredField("TIP_DESC_LINE_SPACING");
            f.setAccessible(true);
            TIP_DESC_LINE_SPACING = f.getFloat(null);

            f = TipHelper.class.getDeclaredField("BOX_EDGE_H");
            f.setAccessible(true);
            BOX_EDGE_H = f.getFloat(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
