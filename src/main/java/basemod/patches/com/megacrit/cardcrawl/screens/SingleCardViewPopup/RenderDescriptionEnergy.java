package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

// This class handles rendering [E] energy symbols on cards in portrait view.
// Unfortunately, javassist doesn't allow Raw patches with `continue`.
// So instead we hack our way around it, by reusing the code for testing for and drawing the "[G]" mana symbol.

@SpirePatch(
    cls="com.megacrit.cardcrawl.screens.SingleCardViewPopup",
    method="renderDescription"
)
public class RenderDescriptionEnergy {
    public static ExprEditor Instrument() {
        return new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals("java.lang.String") && m.getMethodName().equals("equals")) {
                    m.replace("{ $_ = basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup.RenderDescriptionEnergy.replaceEquals(tmp, (java.lang.String)$1); }");
                }
            }
            public void edit(FieldAccess m) throws CannotCompileException {
                if (m.getClassName().equals("com.megacrit.cardcrawl.helpers.ImageMaster") && m.getFieldName().equals("GREEN_ORB")) {
                    m.replace("{ $_ = basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup.RenderDescriptionEnergy.replaceOrbField(tmp, this.card); }");
                }
            }
        };
    }
    public static boolean replaceEquals(String tmp, String originalArg) {
        if (tmp.equals(originalArg)) return true;
        if (tmp.equals("[E] ") && originalArg.equals("[G] ")) return true;
        if (tmp.equals("[E]. ") && originalArg.equals("[G]. ")) return true;
        return false;
    }
    public static TextureAtlas.AtlasRegion replaceOrbField(String tmp, Object card) {
        if (tmp.equals("[E] ") || tmp.equals("[E]. ")) return BaseMod.getCardSmallEnergy((AbstractCard)card);
        return ImageMaster.GREEN_ORB;
    }
}
