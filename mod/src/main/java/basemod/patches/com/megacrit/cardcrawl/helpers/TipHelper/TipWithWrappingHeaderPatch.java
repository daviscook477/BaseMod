package basemod.patches.com.megacrit.cardcrawl.helpers.TipHelper;

import java.lang.reflect.Field;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class TipWithWrappingHeaderPatch {
    public static float headerHeight = 0;
    private static float BODY_TEXT_WIDTH = 0;
    private static float TIP_DESC_LINE_SPACING = 0;
    
    @SpirePatch(
            clz=TipHelper.class,
            method="renderTipBox"
    )
    public static class PushDownTipBoxBody {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("renderFontLeftTopAligned")) {
                        //make it work like the renderSmartText call below it
                        m.replace(FontHelper.class.getName() + ".renderSmartText($1, " +
                            FontHelper.class.getName() +
                            ".tipHeaderFont, $3, $4, $5, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING, " +
                            Settings.class.getName() + ".GOLD_COLOR);"                        
                       );
                    } else if(m.getMethodName().equals("renderSmartText")) {
                        //draw below the (possibly wrapping) header
                        m.replace(
                        "$_ = $proceed($1, $2, $3, $4, y + BODY_OFFSET_Y - " +
                        TipWithWrappingHeaderPatch.class.getName() + ".headerHeight, $6, $7, $8);");
                   }
                }
            };
        }
        
        @SpireInsertPatch(
                rloc=0,
                localvars={"textHeight"}
        )
        public static void Insert(float x, float y, SpriteBatch sb, String title, String description, @ByRef float[] textHeight){
            if (TipWithWrappingHeaderPatch.BODY_TEXT_WIDTH == 0){
                getConstants();
            }
            TipWithWrappingHeaderPatch.headerHeight = -FontHelper.getSmartHeight(
                    FontHelper.tipHeaderFont, 
                    title, 
                    BODY_TEXT_WIDTH, 
                    TIP_DESC_LINE_SPACING);
            textHeight[0] += TipWithWrappingHeaderPatch.headerHeight;
        }
    }
    
    @SpirePatch(
            clz=TipHelper.class,
            method="renderBox"
    )
    public static class PushDownPowerBoxBody {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("renderFontLeftTopAligned")) {
                        //make it work like the renderSmartText call below it
                        m.replace(FontHelper.class.getName() + ".renderSmartText($1, " +
                        FontHelper.class.getName() +
                        ".tipHeaderFont, $3, $4, $5, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING, " +
                        Settings.class.getName() + ".GOLD_COLOR);"                        
                       );
                    } else if(m.getMethodName().equals("renderSmartText")) {
                        //draw below the (possibly wrapping) header
                        m.replace(
                        "$_ = $proceed($1, $2, $3, $4, y + BODY_OFFSET_Y - " +
                        TipWithWrappingHeaderPatch.class.getName() + ".headerHeight, $6, $7, $8);");
                    }
                }
            };
        }
        
        @SpireInsertPatch(
                rloc=0,
                localvars={"textHeight"}
        )
        public static void Insert(SpriteBatch sb, String word, float x, float y, @ByRef float[] textHeight){
            if (TipWithWrappingHeaderPatch.BODY_TEXT_WIDTH == 0){
                getConstants();
            }
            TipWithWrappingHeaderPatch.headerHeight = -FontHelper.getSmartHeight(
                    FontHelper.tipHeaderFont, 
                    TipHelper.capitalize(word), 
                    BODY_TEXT_WIDTH, 
                    TIP_DESC_LINE_SPACING);
            textHeight[0] += TipWithWrappingHeaderPatch.headerHeight;
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
            
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
