package basemod.patches.com.megacrit.cardcrawl.rooms.AbstractRoom;

import basemod.customacts.savefields.ElitesSlain;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Map;

@SpirePatch(
        clz = AbstractRoom.class,
        method = "update"
)
public class IncrementElitesSlain {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void Insert(AbstractRoom __instance) {
        Map<Integer, Integer> killedElites = ElitesSlain.getKilledElites();
        if(!killedElites.containsKey(AbstractDungeon.actNum)) {
            killedElites.put(AbstractDungeon.actNum, 1);
        } else {
            killedElites.put(AbstractDungeon.actNum, killedElites.get(AbstractDungeon.actNum) + 1);
        }
    }

    public static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardCrawlGame.class, "elitesModdedSlain");

            return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[1]};
        }
    }
}
