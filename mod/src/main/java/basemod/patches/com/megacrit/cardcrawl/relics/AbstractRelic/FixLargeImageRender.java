package basemod.patches.com.megacrit.cardcrawl.relics.AbstractRelic;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class FixLargeImageRender {
    @SpirePatch(
            clz = AbstractRelic.class,
            method = "render",
            paramtypez = {
                    SpriteBatch.class,
                    boolean.class,
                    Color.class})
    public static class Render {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                private boolean modified = false;

                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (!modified) {
                        if (f.getFieldName().equals("largeImg")) {
                            modified = true;
                            f.replace(String.format("{$_ = %s.shouldTreatAsNull($0);}", Render.class.getName()));
                        }
                    }
                }
            };
        }

        public static Object shouldTreatAsNull(AbstractRelic r) {
            // The CustomRelic constructor fills in AbstractRelic.largeImg with a copy of the default-sized texture.
            // However, AbstractRelic has logic to render the regular texture at 2x scale, *if* the largeImg is null.
            // So, we'll temporarily say it's null to invoke that logic.
            return (r instanceof CustomRelic) ? null : r.largeImg;
        }
    }

}
