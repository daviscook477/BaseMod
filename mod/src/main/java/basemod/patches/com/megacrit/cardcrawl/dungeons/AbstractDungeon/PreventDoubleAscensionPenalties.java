package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "dungeonTransitionSetup"
)
public class PreventDoubleAscensionPenalties {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static SpireReturn<Void> Insert() {
        if(AbstractDungeon.player.currentHealth < AbstractDungeon.player.maxHealth && AbstractDungeon.floorNum <= 1) {
            AbstractDungeon.dungeonMapScreen.map.atBoss = false;
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardCrawlGame.class, "dungeon");
            int[] result = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{result[result.length - 1]};
        }
    }
}
