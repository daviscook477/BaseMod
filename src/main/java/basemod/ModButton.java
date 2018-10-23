package basemod;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.function.Consumer;

// DEPRECATED
public class ModButton implements IUIElement{
    private static final float HB_SHRINK = 14.0f;
    
    private Consumer<ModButton> click;
    private Hitbox hb;
    private Texture texture;
    private float x;
    private float y;
    private float w;
    private float h;

    public ModPanel parent;
    
    public ModButton(float xPos, float yPos, ModPanel p, Consumer<ModButton> c) {
    	this(xPos, yPos, ImageMaster.loadImage("img/BlankButton.png"), p, c);
    }
    
    public ModButton(float xPos, float yPos, Texture tex, ModPanel p, Consumer<ModButton> c) {
        texture = tex;
        x = xPos*Settings.scale;
        y = yPos*Settings.scale;
        w = texture.getWidth();
        h = texture.getHeight();
        hb = new Hitbox(x+(HB_SHRINK*Settings.scale), y+(HB_SHRINK*Settings.scale), (w-(2*HB_SHRINK))*Settings.scale, (h-(2*HB_SHRINK))*Settings.scale);
        
        parent = p;
        click = c;
    }
    
    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE); 
        sb.draw(texture, x, y, w*Settings.scale, h*Settings.scale);
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