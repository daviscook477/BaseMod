package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import basemod.BaseMod;
import basemod.abstracts.DynamicVariable;
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

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpirePatch(
        clz=SingleCardViewPopup.class,
        method="renderDescription"
)
public class RenderCustomDynamicVariable
{
    public static void Raw(CtBehavior ctMethodToPatch) throws CannotCompileException
    {
        final int[] insertLine = {-1};
        ctMethodToPatch.instrument(new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException
            {
                if (m.getMethodName().equals("renderDynamicVariable")) {
                    if (insertLine[0] == -1) {
                        insertLine[0] = m.getLineNumber();
                    }
                    m.replace("$_ = basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup.RenderCustomDynamicVariable.Inner.myRenderDynamicVariable(this, tmp, $$);");
                }
            }
        });

        if (insertLine[0] > 0) {
            ctMethodToPatch.insertAt(insertLine[0]-1,
                    "if (tmp.length() != 4 && tmp.length() != 5) {" +
                            "start_x += basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup.RenderCustomDynamicVariable.Inner.myRenderDynamicVariable(this, tmp, tmp.charAt(1), start_x, draw_y, i, font, sb, null);" +
                            "}");
        }
    }

    public static class Inner
    {
        private static Logger logger = LogManager.getLogger();
        private static final GlyphLayout gl = new GlyphLayout();

        public static float myRenderDynamicVariable(Object __obj_instance, String key, char ckey, float start_x, float draw_y, int i, BitmapFont font, SpriteBatch sb, Character cend)
        {
            SingleCardViewPopup __instance = (SingleCardViewPopup) __obj_instance;

            // Get any private variables we need
            AbstractCard card;
            float current_x;
            float current_y;
            try {
                Field f = SingleCardViewPopup.class.getDeclaredField("card");
                f.setAccessible(true);
                card = (AbstractCard) f.get(__instance);

                f = SingleCardViewPopup.class.getDeclaredField("current_x");
                f.setAccessible(true);
                current_x = f.getFloat(__instance);

                f = SingleCardViewPopup.class.getDeclaredField("current_y");
                f.setAccessible(true);
                current_y = f.getFloat(__instance);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
                return 0;
            }

            String end = "";

            Pattern pattern = Pattern.compile("!(.+)!(.*) ");
            Matcher matcher = pattern.matcher(key);
            if (matcher.find()) {
                key = matcher.group(1);
                end = matcher.group(2);
            }

            // Main body of method
            StringBuilder stringBuilder = new StringBuilder();
            Color c = Settings.CREAM_COLOR;
            int num = 0;
            DynamicVariable dv = BaseMod.cardDynamicVariableMap.get(key);
            if (dv != null) {
                num = dv.baseValue(card);
                if (dv.upgraded(card)) {
                    c = dv.getUpgradedColor();
                } else {
                    c = dv.getNormalColor();
                }
            } else {
                logger.error("No dynamic card variable found for key \"" + key + "\"!");
            }
            stringBuilder.append(num);
            gl.setText(font, stringBuilder.toString());
            FontHelper.renderRotatedText(sb, font, stringBuilder.toString(),
                    current_x, current_y,
                    start_x - current_x + gl.width / 2.0f,
                    i * 1.53f * -font.getCapHeight() + draw_y - current_y + -12.0f,
                    0.0f, true, c);
            if (end != null) {
                FontHelper.renderRotatedText(sb, font, end,
                        current_x, current_y,
                        start_x - current_x + gl.width + 10.0f * Settings.scale,
                        i * 1.53f * -font.getCapHeight() + draw_y - current_y + -12.0f,
                        0.0f, true, Settings.CREAM_COLOR);
                stringBuilder.append(end);
            }
            stringBuilder.append(' ');
            gl.setText(font, stringBuilder.toString());
            return gl.width;
        }
    }
}
