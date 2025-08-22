package basemod.patches.com.megacrit.cardcrawl.screens.stats.StatsScreen;

import basemod.BaseMod;
import basemod.ModAchievementGrid;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;
import javassist.CtBehavior;

import java.util.Iterator;

@SpirePatch(clz = StatsScreen.class, method = "renderStatScreen")
public class AchievementGridRenderPatch {
    private static boolean achievementsPublished = false;
    @SpireInsertPatch(locator = Locator.class, localvars = {"renderY"})
    public static void Insert(StatsScreen __instance, SpriteBatch sb, @ByRef float[] renderY) {
        if (!achievementsPublished) {
            BaseMod.publishEditAchievements(__instance);
            achievementsPublished = true;
        }
        boolean firstGrid = true;
        for (ModAchievementGrid grid : BaseMod.modAchievementGrids.values()) {
            if (firstGrid) {
                renderY[0] += 50.0F * Settings.scale;
                firstGrid = false;
            }
            StatsScreen.renderHeader(sb, grid.headerText, 300.0F * Settings.scale, renderY[0]);
            grid.render(sb, renderY[0]);
            renderY[0] -= grid.calculateHeight();
            renderY[0] -= 100.0F * Settings.scale;
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(Iterator.class, "hasNext");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

}