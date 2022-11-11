package basemod.patches.com.megacrit.cardcrawl.helpers.FontHelper;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.helpers.FontHelper;
import javassist.CtBehavior;

@SpirePatch(clz = FontHelper.class, method = "ClearSCPFontTextures")
public class FixSCPCardEnergyFont {
    @SpireInsertPatch(locator = Locator.class)
    public static void Insert() {
        ReflectionHacks.setPrivateStatic(FontHelper.class, "fontFile",
                FontHelper.SCP_cardEnergyFont.getData().fontFile);
    }

    public static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(FreeTypeFontGenerator.FreeTypeFontParameter.class, "borderStraight");
            return LineFinder.findInOrder(ctBehavior, finalMatcher);
        }
    }
}
