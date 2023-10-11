package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.DynamicVariable;
import basemod.helpers.CardModifierManager;
import basemod.helpers.dynamicvariables.BlockVariable;
import basemod.helpers.dynamicvariables.DamageVariable;
import basemod.helpers.dynamicvariables.MagicNumberVariable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.FontHelper;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;

@SpirePatch(
        clz=AbstractCard.class,
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
        private static float subRenderDynamicVariable(Object __obj_instance, String pre, String key, String end, float start_x, float draw_y, int i, BitmapFont font, SpriteBatch sb)
        {
            AbstractCard __instance = (AbstractCard) __obj_instance;

            // Get any private variables we need
            Color textColor = ReflectionHacks.getPrivate(__instance, AbstractCard.class, "textColor");

            // Main body of method
            Color c;
            int num = 0;
            DynamicVariable dv = BaseMod.cardDynamicVariableMap.get(key);
            if (dv != null) {
                if (dv.isModified(__instance)) {
                    num = dv.value(__instance);
                    if (num >= dv.modifiedBaseValue(__instance)) {
                        c = dv.getIncreasedValueColor();
                    } else {
                        c = dv.getDecreasedValueColor();
                    }
                } else {
                    c = dv.getNormalColor();
                    num = dv.modifiedBaseValue(__instance);
                }

                c = c.cpy();
                c.a = textColor.a;
            } else {
                logger.error("No dynamic card variable found for key \"" + key + "\"!");
                c = textColor;
            }

            float totalWidth = 0;
            String variableValue = Integer.toString(num);

            if (!pre.isEmpty()) {
                gl.setText(font, pre);
                FontHelper.renderRotatedText(sb, font, pre,
                        __instance.current_x, __instance.current_y,
                        start_x - __instance.current_x + gl.width / 2.0f,
                        i * 1.45f * -font.getCapHeight() + draw_y - __instance.current_y + -6.0f,
                        __instance.angle, true, textColor);

                totalWidth += gl.width;
                start_x += gl.width;
            }

            gl.setText(font, variableValue);
            FontHelper.renderRotatedText(sb, font, variableValue,
                    __instance.current_x, __instance.current_y,
                    start_x - __instance.current_x + gl.width / 2.0f,
                    i * 1.45f * -font.getCapHeight() + draw_y - __instance.current_y + -6.0f,
                    __instance.angle, true, c);

            totalWidth += gl.width;
            start_x += gl.width;

            if (!end.isEmpty()) {
                gl.setText(font, end);
                FontHelper.renderRotatedText(sb, font, end,
                        __instance.current_x, __instance.current_y,
                        start_x - __instance.current_x + gl.width / 2.0f,
                        i * 1.45f * -font.getCapHeight() + draw_y - __instance.current_y + -6.0f,
                        __instance.angle, true, textColor);

                totalWidth += gl.width;
            }

            return totalWidth;
        }
    }
}
