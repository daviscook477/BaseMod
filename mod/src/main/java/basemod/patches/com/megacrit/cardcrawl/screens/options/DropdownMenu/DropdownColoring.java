package basemod.patches.com.megacrit.cardcrawl.screens.options.DropdownMenu;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.function.Function;

public class DropdownColoring {

    @SpirePatch2(clz = DropdownMenu.class, method = SpirePatch.CLASS)
    public static class RowToColor {
        public static SpireField<Function<Integer, Color>> function = new SpireField<Function<Integer, Color>>(() ->((index) -> null));
    }

    @SpirePatch2(cls = "com.megacrit.cardcrawl.screens.options.DropdownMenu$DropdownRow", method = "renderRow")
    public static class Patch {

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(Hitbox.class, "hovered");
                return LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }

        @SpireInsertPatch(locator = Locator.class, localvars = {"renderTextColor"})
        public static void changeColor(Object __instance, @ByRef Color[] renderTextColor, int ___index) {
            DropdownMenu dropdown = ReflectionHacks.getPrivate(__instance, __instance.getClass(), "this$0");
            Color newColor = RowToColor.function.get(dropdown).apply(___index);
            if (newColor != null) {
                renderTextColor[0] = newColor;
            }
        }
    }
}
