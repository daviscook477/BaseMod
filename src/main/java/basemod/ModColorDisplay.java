package basemod;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.function.Consumer;

public class ModColorDisplay implements IUIElement {
    public float hbShrink = 16.0f;
    
    public float r = 1.0f;
    public float g = 1.0f;
    public float b = 1.0f;
    public float a = 1.0f;
    public float aOutline = 1.0f;
    
    public float x;
    public float y;
    public float w;
    public float h;
    
    public Consumer<ModColorDisplay> click;
    public Texture texture;
    public Texture outline;

    private Hitbox hb;
    
    public ModColorDisplay(float x, float y, Texture texture, Texture outline, Consumer<ModColorDisplay> click) {
        this.texture = texture;
        this.outline = outline;
        
        this.x = x;
        this.y = y;
        this.w = texture.getWidth();
        this.h = texture.getHeight();
        
        this.click = click;
        
        float hbx = (x + hbShrink) * Settings.scale;
        float hby = (y + hbShrink) * Settings.scale;
        float hbw = (w - 2*hbShrink) * Settings.scale;
        float hbh = (h - 2*hbShrink) * Settings.scale;
        
        this.hb = new Hitbox(hbx, hby, hbw, hbh);
    }

    public ModColorDisplay(float x, float y, float hbShrink, Texture texture, Texture outline, Consumer<ModColorDisplay> click) {
        this(x, y, texture, outline, click);
        this.hbShrink = hbShrink;

        float hbx = (x + hbShrink) * Settings.scale;
        float hby = (y + hbShrink) * Settings.scale;
        float hbw = (w - 2*hbShrink) * Settings.scale;
        float hbh = (h - 2*hbShrink) * Settings.scale;

        this.hb = new Hitbox(hbx, hby, hbw, hbh);
    }

    public void render(SpriteBatch sb) {
        if (outline != null) {
            sb.setColor(new Color(0.0f, 0.0f, 0.0f, aOutline));
            sb.draw(outline, x * Settings.scale, y * Settings.scale, w * Settings.scale, h * Settings.scale);
        }
        
        sb.setColor(new Color(r, g, b, a)); 
        sb.draw(texture, x * Settings.scale, y * Settings.scale, w * Settings.scale, h * Settings.scale);
        
        hb.render(sb);
    }
    
    public void update() {
        hb.update();
        
        if (hb.justHovered) {
            CardCrawlGame.sound.playV("UI_HOVER", 0.75f);
        }
        
        if (hb.hovered) {
            if (InputHelper.justClickedLeft) {
                CardCrawlGame.sound.playA("UI_CLICK_1", -0.1f);
                hb.clickStarted = true;
            }
        }
        
        if (hb.clicked) {
            hb.clicked = false;
            onClick();
        }
    }
    
    private void onClick() {
        click.accept(this);
    }

	@Override
	public int renderLayer() {
		return ModPanel.MIDDLE_LAYER;
	}

	@Override
	public int updateOrder() {
		return ModPanel.DEFAULT_UPDATE;
	}
}