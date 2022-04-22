package basemod.patches.com.megacrit.cardcrawl.screens.runHistory.TinyCard;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.runHistory.TinyCard;

public class HitboxPatch {
    @SpirePatch2(clz = TinyCard.class, method = SpirePatch.CONSTRUCTOR)
    public static class FixHitboxHeight {
        @SpirePostfixPatch
        public static void scaleProperly(TinyCard __instance) {
            __instance.hb.height *= Settings.scale;
        }
    }
}
