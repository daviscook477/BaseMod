package basemod.patches.com.megacrit.cardcrawl.unlock.UnlockTracker;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

@SpirePatch(
        clz = UnlockTracker.class,
        method = "countUnlockedCards"
)
public class CountModdedUnlockCards {
    private static Logger logger = LogManager.getLogger(CountModdedUnlockCards.class.getName());

    private static HashMap<AbstractPlayer.PlayerClass, Integer> lockedCardCounts = new HashMap<>();
    private static HashMap<AbstractPlayer.PlayerClass, Integer> unlockedCardCounts = new HashMap<>();

    public static boolean enabled = false; //only enabled once modded unlocks are added.

    public static int getUnlockedCardCount(AbstractPlayer.PlayerClass c)
    {
        if (unlockedCardCounts.containsKey(c))
            return unlockedCardCounts.get(c);
        return 0;
    }

    public static int getLockedCardCount(AbstractPlayer.PlayerClass c, int defaultValue)
    {
        if (lockedCardCounts.containsKey(c))
            return lockedCardCounts.get(c);
        return defaultValue;
    }

    @SpirePostfixPatch
    public static void countModdedUnlocks()
    {
        if (CardCrawlGame.characterManager != null && enabled)
        {
            logger.info("Counting modded unlocks.");

            StringBuilder unlockData;
            for (AbstractPlayer p : BaseMod.getModdedCharacters())
            {
                unlockData = new StringBuilder(p.chosenClass.name().toUpperCase() + " UNLOCKS:\t");

                ArrayList<String> lockedCards = BaseMod.getUnlockCards(p.chosenClass);
                int unlockCount = 0;
                if (lockedCards == null)
                {
                    unlockData.append("0/0");
                    unlockedCardCounts.put(p.chosenClass, 0);
                    lockedCardCounts.put(p.chosenClass, 0);
                }
                else
                {
                    for (String id : lockedCards)
                    {
                        if (!UnlockTracker.isCardLocked(id))
                            ++unlockCount;
                    }

                    unlockData.append(unlockCount).append("/").append(lockedCards.size());
                    unlockedCardCounts.put(p.chosenClass, unlockCount);
                    lockedCardCounts.put(p.chosenClass, lockedCards.size());
                }

                logger.info(unlockData.toString());
            }
        }
    }
}
