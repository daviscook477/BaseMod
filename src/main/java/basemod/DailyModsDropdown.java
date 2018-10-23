package basemod;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.AbstractDailyMod;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.ArrayList;

public class DailyModsDropdown extends ClickableUIElement {

    private ArrayList<AbstractDailyMod> dailyMods;
    private ArrayList<DailyModIcon> dailyModIcons;
    private boolean showModList = false;
    private final float SHADOW_DIST_X = 9.0F * Settings.scale;
    private final float SHADOW_DIST_Y = 14.0F * Settings.scale;
    private final float BOX_EDGE_H = 32.0F * Settings.scale;
    private final float BOX_BODY_H = 64.0F * Settings.scale;
    private final float BOX_W = 104.0F * Settings.scale;
    private final float TEXT_OFFSET_X = 22.0F * Settings.scale;
    private final float TOP_OFFSET_Y = 12.0F * Settings.scale;

    public DailyModsDropdown(ArrayList<AbstractDailyMod> dailyMods, float x, float y) {
        super((Texture) null, x, y,110, 64);
        this.dailyMods = dailyMods;
        dailyModIcons = new ArrayList<>();
        this.setX(x * Settings.scale);
        if (y < 0) {
            this.setY(Settings.HEIGHT + y * Settings.scale);
        } else {
            this.setY(y * Settings.scale);
        }
        initDailyModIcons();
    }

    @Override
    public void update() {
        super.update();

        if (!hitbox.hovered && InputHelper.justClickedLeft) {
            showModList = false;
        }

        if (showModList) {
            dailyModIcons.forEach(ClickableUIElement::update);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        sb.setColor(Color.WHITE);
        // TODO that unicode character
        if (hitbox.hovered) {
            FontHelper.renderFontCenteredHeight(sb, FontHelper.panelNameTitleFont, "Daily Mods \u25BC", x, y + hb_h / 2.0f, Settings.GOLD_COLOR.cpy().lerp(Color.WHITE, 0.3f));
        } else {
            FontHelper.renderFontCenteredHeight(sb, FontHelper.panelNameTitleFont, "Daily Mods \u25BC", x, y + hb_h / 2.0f, Settings.GOLD_COLOR);
        }
        if (showModList) {
            renderBox(sb, y - 60f * Settings.scale);
            dailyModIcons.forEach(mod -> mod.render(sb));
        }
        renderHitbox(sb);
    }

    @Override
    protected void onHover() {}

    @Override
    protected void onUnhover() {}

    @Override
    protected void onClick() {
        showModList = !showModList;
    }

    private void initDailyModIcons(){
        int count = 0;
        for (AbstractDailyMod dailyMod: dailyMods) {
            float yPos = y - TOP_OFFSET_Y - (count * 64F * Settings.scale) - 31F * Settings.scale;
            DailyModIcon icon = new DailyModIcon(dailyMod.img, x + TEXT_OFFSET_X, yPos, 64F, 64F, dailyMod);
            dailyModIcons.add(icon);
            count++;
        }
    }

    private void renderBox(SpriteBatch sb, float y){
        float h = getImagesHeight();
        sb.setColor(Settings.TOP_PANEL_SHADOW_COLOR);
        sb.draw(ImageMaster.KEYWORD_TOP, x + SHADOW_DIST_X, y - SHADOW_DIST_Y, BOX_W, BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BODY, x + SHADOW_DIST_X, y - h - BOX_EDGE_H - SHADOW_DIST_Y, BOX_W, h + BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BOT, x + SHADOW_DIST_X, y - h - BOX_BODY_H - SHADOW_DIST_Y, BOX_W, BOX_EDGE_H);
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.KEYWORD_TOP, x, y, BOX_W, BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BODY, x, y - h - BOX_EDGE_H, BOX_W, h + BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BOT, x, y - h - BOX_BODY_H, BOX_W, BOX_EDGE_H);
    }

    private float getImagesHeight() {
        return (dailyMods.size() - 1) * 64F * Settings.scale;
    }

    private class DailyModIcon extends ClickableUIElement{

        private AbstractDailyMod dailyMod;

        public DailyModIcon(Texture image, float x, float y, float hb_w, float hb_h, AbstractDailyMod dailyMod) {
            super(image, x, y, hb_w, hb_h);
            this.dailyMod = dailyMod;
            this.y = y - (64F * Settings.scale);
            this.x = x;
            this.hitbox.move(this.x + this.hb_w / 2, this.y + this.hb_h / 2);
        }


        @Override
        protected void onHover() {
            TipHelper.renderGenericTip(x + hb_w + 15F * Settings.scale, y, dailyMod.name, dailyMod.description);
        }

        @Override
        protected void onUnhover() {}

        @Override
        protected void onClick() {}
    }


}
