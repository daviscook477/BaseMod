package basemod;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;

import java.util.List;

import static basemod.BaseMod.modAchievements;
import static com.megacrit.cardcrawl.unlock.UnlockTracker.achievementPref;

public class ModAchievementUnlocker {
    public static void unlockAchievement(String key) {
        if (!Settings.isShowBuild && Settings.isStandardRun()) {
            CardCrawlGame.publisherIntegration.unlockAchievement(key);
            if (!achievementPref.getBoolean(key, false)) {
                BaseMod.logger.info("Attempting to unlock achievement with key: " + key);
                achievementPref.putBoolean(key, true);
                achievementPref.flush();
                // Find the achievement and update its image
                for (List<ModAchievement> achievements : modAchievements.values()) {
                    for (ModAchievement achievement : achievements) {
                        if (achievement.key.equals(key)) {
                            achievement.isUnlocked = true;
                            achievement.updateImage();
                            break;
                        }
                    }
                }
            }
        }
    }
}