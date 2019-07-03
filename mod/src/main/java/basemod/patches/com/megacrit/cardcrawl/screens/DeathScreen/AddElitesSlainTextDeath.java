package basemod.patches.com.megacrit.cardcrawl.screens.DeathScreen;

import basemod.ReflectionHacks;
import basemod.customacts.CustomDungeon;
import basemod.customacts.savefields.Breadcrumbs;
import basemod.customacts.savefields.ElitesSlain;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import com.megacrit.cardcrawl.localization.ScoreBonusStrings;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.GameOverStat;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Map;

@SpirePatch(
        clz = DeathScreen.class,
        method = "createGameOverStats"
)
public class AddElitesSlainTextDeath {

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void Insert(DeathScreen __instance) {
        doThing(__instance.stats);
    }

    public static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "add");

            return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[5]};
        }
    }

    public static void doThing(ArrayList<GameOverStat> stats) {
        Map<Integer, Integer> elitesKilled = ElitesSlain.getKilledElites();
        Map<Integer, String> breadcrumbs = Breadcrumbs.getBreadCrumbs();
        String[] parts = CardCrawlGame.languagePack.getScoreString(CustomDungeon.makeID("ElitesKilled")).DESCRIPTIONS;
        boolean displayDefaults = (CardCrawlGame.dungeon instanceof TheCity || CardCrawlGame.dungeon instanceof TheBeyond || CardCrawlGame.dungeon instanceof TheEnding);

        for(int i = 1; i <= AbstractDungeon.actNum; i++) {
            boolean customact = breadcrumbs.containsKey(i);
            String localizedString = null;
            int num = 0;
            if(!displayDefaults && !customact) {
                //display default elites slain thing
                switch(i) {
                    case 2: //the city
                        localizedString = ((ScoreBonusStrings)ReflectionHacks.getPrivateStatic(DeathScreen.class, "CITY_ELITE")).NAME;
                        num = CardCrawlGame.elites2Slain;
                        break;
                    case 3: //the beyond
                        localizedString = ((ScoreBonusStrings)ReflectionHacks.getPrivateStatic(DeathScreen.class, "BEYOND_ELITE")).NAME;
                        num = CardCrawlGame.elites3Slain;
                        break;

                    default:
                        continue;

                }
            } else if(customact) {
                if(CustomDungeon.dungeons.containsKey(breadcrumbs.get(i))) {
                    localizedString = parts[0] + CustomDungeon.dungeons.get(breadcrumbs.get(i)).name + parts[2];
                } else {
                    localizedString = parts[0] + parts[1] + " " + i + parts[2];
                }
                num = elitesKilled.containsKey(i) ? elitesKilled.get(i) : 0;
            } else {
                continue;
            }
            stats.add(new GameOverStat(localizedString + " (" + num + ")", null, Integer.toString(num * 10 * i)));
        }
    }
}
