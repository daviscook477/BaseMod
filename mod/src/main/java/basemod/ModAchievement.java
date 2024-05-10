package basemod;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.stats.AchievementItem;

public class ModAchievement extends AchievementItem {
    private TextureAtlas.AtlasRegion unlockedImg;
    private TextureAtlas.AtlasRegion lockedImg;
    public TextureAtlas.AtlasRegion currentImg;
    private static final Color LOCKED_COLOR = new Color(1.0F, 1.0F, 1.0F, 0.8F);
    public TextureAtlas atlas;

    public ModAchievement(String title, String desc, String key, boolean hidden, TextureAtlas.AtlasRegion unlockedImage, TextureAtlas.AtlasRegion lockedImage, TextureAtlas atlas) {
        super(title, desc, "", key, hidden);
        this.unlockedImg = unlockedImage;
        this.lockedImg = lockedImage;
        this.currentImg = lockedImage;
        this.atlas = atlas;
    }

    public void reloadImg() {
        if (this.isUnlocked) {
            this.unlockedImg = this.atlas.findRegion(this.unlockedImg.name);
        } else {
            this.lockedImg = this.atlas.findRegion(this.lockedImg.name);
        }
    }

    public void render(SpriteBatch sb, float x, float y) {
        TextureAtlas.AtlasRegion currentImg = this.isUnlocked ? this.unlockedImg : this.lockedImg;
        this.currentImg = currentImg;
        Color currentColor = this.isUnlocked ? Color.WHITE : LOCKED_COLOR;
        sb.setColor(currentColor);
        if (this.hb.hovered) {
            sb.draw(currentImg, x - (float)currentImg.packedWidth / 2.0F, y - (float)currentImg.packedHeight / 2.0F, (float)currentImg.packedWidth / 2.0F, (float)currentImg.packedHeight / 2.0F, (float)currentImg.packedWidth, (float)currentImg.packedHeight, Settings.scale * 1.1F, Settings.scale * 1.1F, 0.0F);
        } else {
            sb.draw(currentImg, x - (float)currentImg.packedWidth / 2.0F, y - (float)currentImg.packedHeight / 2.0F, (float)currentImg.packedWidth / 2.0F, (float)currentImg.packedHeight / 2.0F, (float)currentImg.packedWidth, (float)currentImg.packedHeight, Settings.scale, Settings.scale, 0.0F);
        }

        this.hb.move(x, y);
        this.hb.render(sb);
    }
}