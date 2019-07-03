package basemod.patches.com.megacrit.cardcrawl.screens.DeathScreen;

import basemod.BaseMod;
import basemod.customacts.savefields.ElitesSlain;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.DeathScreen;

import java.util.Map;

public class CalcScoreCustomActDeath {

    @SpirePatch(
            clz = DeathScreen.class,
            method = "calcScore"
    )
    public static class DeathScreenPatch {
        public static int Postfix(int tmp, boolean isVictory) {
            return doThing(tmp);
        }
    }

    public static int doThing(int tmp) {
        Map<Integer, Integer> elitesKilled = ElitesSlain.getKilledElites();

        for(final Map.Entry<Integer, Integer> entry : elitesKilled.entrySet()) {
            tmp += entry.getValue() * 10 * entry.getKey();
        }

        return tmp;
    }
}
