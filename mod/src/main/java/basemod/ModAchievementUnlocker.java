package basemod;

import com.megacrit.cardcrawl.core.Settings;

import java.util.List;

import static basemod.BaseMod.modAchievements;
import static com.megacrit.cardcrawl.unlock.UnlockTracker.achievementPref;

public class ModAchievementUnlocker {
    public static void unlockAchievement(String key) {
        if (!Settings.isShowBuild && Settings.isStandardRun()) {
            if (!achievementPref.getBoolean(key, false)) {
                achievementPref.putBoolean(key, true);
                achievementPref.flush();

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