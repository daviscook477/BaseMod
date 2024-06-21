package basemod;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;

public class ModAchievementGrid {
    public ArrayList<ModAchievement> items = new ArrayList<>();
    private static final float SPACING = 200.0F * Settings.scale;
    private static final int ITEMS_PER_ROW = 5;
    public String modID;
    public String headerText;

    public ModAchievementGrid(String modID, String headerText) {
        this.modID = modID;
        this.headerText = headerText;
    }

    public void updateAchievementStatus() {
        for (ModAchievement item : items) {
            String achievementKey = item.getKey();
            boolean isUnlocked = UnlockTracker.isAchievementUnlocked(achievementKey);
            item.isUnlocked = isUnlocked;
            item.reloadImg();
        }
    }

    public void render(SpriteBatch sb, float renderY) {
        if (items == null) {
            BaseMod.logger.info("Items list is null in ModAchievementGrid for mod: " + modID);
            return;
        }
        for (int i = 0; i < items.size(); ++i) {
            ModAchievement achievement = items.get(i);
            if (achievement != null) {
                achievement.render(sb, 560.0F * Settings.scale + (i % ITEMS_PER_ROW) * SPACING, renderY - (i / ITEMS_PER_ROW) * SPACING + 680.0F * Settings.yScale);
            } else {
                BaseMod.logger.info("Null achievement found in ModAchievementGrid for mod: " + modID);
            }
        }
    }

    public float calculateHeight() {
        int numRows = (items.size() + ITEMS_PER_ROW - 1) / ITEMS_PER_ROW;
        float height = numRows * SPACING + 50.0F * Settings.scale;
        return height;
    }

    public void update() {
        updateAchievementStatus();
        for (ModAchievement item : items) {
            item.update();
        }
    }
}