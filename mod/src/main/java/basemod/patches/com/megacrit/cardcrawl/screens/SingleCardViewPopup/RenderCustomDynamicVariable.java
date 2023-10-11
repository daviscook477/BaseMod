package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.DynamicVariable;
import basemod.helpers.CardModifierManager;
import basemod.helpers.dynamicvariables.BlockVariable;
import basemod.helpers.dynamicvariables.DamageVariable;
import basemod.helpers.dynamicvariables.MagicNumberVariable;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.CardModifierPatches;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;

@SpirePatch(
        clz=SingleCardViewPopup.class,
        method="renderDescription"
)
public class RenderCustomDynamicVariable
{
    public static void Raw(CtBehavior ctMethodToPatch) throws CannotCompileException
    {
        ctMethodToPatch.instrument(new ExprEditor() {
            boolean rounded = false;
            boolean modified = false;

            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getMethodName().equals("renderDynamicVariable")) {
                    m.replace("$_ = " + Inner.class.getName() + ".myRenderDynamicVariable(this, tmp, $$);");
                    return;
                }

                if (modified)
                    return;

                if (rounded && m.getMethodName().equals("charAt")) {
                    m.replace(
                            "if (" + Inner.class.getName() + ".checkDynamicVariable(tmp)) {" +
                                    "start_x += " + Inner.class.getName() + ".renderDynamicVariable(this, tmp, start_x, draw_y, i, font, sb);" +
                                    "tmp = \"!\";" +
                                    "$_ = '!';" +
                                    "}" +
                                    "else {" +
                                    "$_ = $proceed($$);" +
                                    "}"
                    );
                    modified = true;
                }
                else if (m.getMethodName().equals("round")) {
                    rounded = true;
                }
            }
        });
    }

    public static class Inner
    {
        private static Logger logger = LogManager.getLogger();
        private static final GlyphLayout gl = new GlyphLayout();

        public static boolean checkDynamicVariable(String key) {
            Matcher matcher = DynamicVariable.variablePattern.matcher(key);
            if (matcher.find()) {
                key = matcher.group(2);
                return BaseMod.cardDynamicVariableMap.containsKey(key);
            }
            return false;
        }

        //Old method left for compatibility
        public static float myRenderDynamicVariable(Object __obj_instance, String key, char ckey, float start_x, float draw_y, int i, BitmapFont font, SpriteBatch sb, Character cend) {

            return subRenderDynamicVariable(__obj_instance, "", "" + ckey, (cend == null ? "" : "" + cend), start_x, draw_y, i, font, sb);
        }

        public static float renderDynamicVariable(Object __obj_instance, String key, float start_x, float draw_y, int i, BitmapFont font, SpriteBatch sb)
        {
            String pre = "", end = "";

            Matcher matcher = DynamicVariable.variablePattern.matcher(key);
            if (matcher.find()) {
                pre = matcher.group(1);
                key = matcher.group(2);
                end = matcher.group(3);
            }

            return subRenderDynamicVariable(__obj_instance, pre, key, end, start_x, draw_y, i, font, sb);
        }

        @SuppressWarnings("ConstantConditions")
        private static float subRenderDynamicVariable(Object __obj_instance, String pre, String key, String end, float start_x, float draw_y, int i, BitmapFont font, SpriteBatch sb) {
            SingleCardViewPopup __instance = (SingleCardViewPopup) __obj_instance;

            // Get any private variables we need
            AbstractCard card = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "card");
            float current_x = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "current_x");
            float current_y = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "current_y");

            // Main body of method
            Color c = Settings.CREAM_COLOR;
            int num = 0;
            DynamicVariable dv = BaseMod.cardDynamicVariableMap.get(key);
            if (dv != null) {
                num = dv.modifiedBaseValue(card);
                if (dv.upgraded(card)) {
                    c = dv.getUpgradedColor(card);
                } else {
                    c = dv.getNormalColor();
                }
            } else {
                logger.error("No dynamic card variable found for key \"" + key + "\"!");
            }

            float totalWidth = 0;
            String variableValue = Integer.toString(num);

            if (!pre.isEmpty()) {
                gl.setText(font, pre);
                FontHelper.renderRotatedText(sb, font, pre,
                        current_x, current_y,
                        start_x - current_x + gl.width / 2.0f,
                        i * 1.53f * -font.getCapHeight() + draw_y - current_y + -12.0f,
                        0.0f, true, Settings.CREAM_COLOR);

                totalWidth += gl.width;
                start_x += gl.width;
            }

            gl.setText(font, variableValue);
            FontHelper.renderRotatedText(sb, font, variableValue,
                    current_x, current_y,
                    start_x - current_x + gl.width / 2.0f,
                    i * 1.53f * -font.getCapHeight() + draw_y - current_y + -12.0f,
                    0.0f, true, c);

            totalWidth += gl.width;
            start_x += gl.width;

            if (!end.isEmpty()) {
                gl.setText(font, end);
                FontHelper.renderRotatedText(sb, font, end,
                        current_x, current_y,
                        start_x - current_x + gl.width / 2.0f,
                        i * 1.53f * -font.getCapHeight() + draw_y - current_y + -12.0f,
                        0.0f, true, Settings.CREAM_COLOR);

                totalWidth += gl.width;
            }

            return totalWidth;
        }
    }
}
