package basemod.patches.com.megacrit.cardcrawl.screens.VictoryScreen;

import basemod.BaseMod;
import basemod.customacts.savefields.ElitesSlain;
import basemod.patches.com.megacrit.cardcrawl.screens.DeathScreen.CalcScoreCustomActDeath;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.VictoryScreen;

import java.util.Map;

public class CalcScoreCustomActVictory {

    @SpirePatch(
            clz = VictoryScreen.class,
            method = "calcScore"
    )
    public static class DeathScreenPatch {
        public static int Postfix(int tmp, boolean isVictory) {
            return CalcScoreCustomActDeath.doThing(tmp);
        }
    }
}
