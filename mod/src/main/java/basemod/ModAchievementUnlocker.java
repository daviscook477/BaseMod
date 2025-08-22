package basemod;

import com.megacrit.cardcrawl.core.Settings;
import static com.megacrit.cardcrawl.unlock.UnlockTracker.achievementPref;

public class ModAchievementUnlocker {
    public static void unlockAchievement(String id) {
        String currentModID = BaseMod.getAchievementModID();
        if (currentModID == null) {
            BaseMod.logger.error("Attempted to unlock achievement without a registered mod ID: " + id);
            return;
        }

        String fullKey = currentModID + ":" + id;

        if (!Settings.isShowBuild && Settings.isStandardRun()) {
            if (!achievementPref.getBoolean(fullKey, false)) {
                achievementPref.putBoolean(fullKey, true);
                achievementPref.flush();
                BaseMod.logger.info("Unlocked achievement: " + fullKey);
            }
        }
    }
}