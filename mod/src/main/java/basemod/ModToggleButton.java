package basemod;

import basemod.helpers.UIElementModificationHelper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.function.Consumer;

public class ModToggleButton implements IUIElement {
	private static final float TOGGLE_Y_DELTA = 0f;
	private static final float TOGGLE_X_EXTEND = 12.0f;
	private static final float HB_WIDTH_EXTENDED = 200.0f;
	
	private Consumer<ModToggleButton> toggle;
	private Hitbox hb;
	private float x;
	private float y;
	private float w;
	private float h;
	private boolean extendedHitbox;
	
	public boolean enabled;
	public ModPanel parent;

	public ModToggleButton(float xPos, float yPos, ModPanel p, Consumer<ModToggleButton> c) {
		this(xPos, yPos, false, true, p, c);
	}
	
	public ModToggleButton(float xPos, float yPos, boolean enabled, boolean extendedHitbox, ModPanel p, Consumer<ModToggleButton> c) {
		x = xPos * Settings.scale;
		y = yPos * Settings.scale;
		w = ImageMaster.OPTION_TOGGLE.getWidth();
		h = ImageMaster.OPTION_TOGGLE.getHeight();
		this.extendedHitbox = extendedHitbox;
		if (extendedHitbox) {
			hb = new Hitbox(x - TOGGLE_X_EXTEND * Settings.scale,
					y - TOGGLE_Y_DELTA * Settings.scale,
					HB_WIDTH_EXTENDED * Settings.scale, h * Settings.scale);
		} else {
			hb = new Hitbox(x, y - TOGGLE_Y_DELTA * Settings.scale,
					w * Settings.scale, h * Settings.scale);
		}

		this.enabled = enabled;
		parent = p;
		toggle = c;
	}
	
	public void wrapHitboxToText(String text, float lineWidth, float lineSpacing, BitmapFont font) {
		float tWidth = FontHelper.getSmartWidth(font, text, lineWidth, lineSpacing);
		hb.width = tWidth + h * Settings.scale + TOGGLE_X_EXTEND;
	}
	
	public void render(SpriteBatch sb) {
        if (this.hb.hovered) {
            sb.setColor(Color.CYAN);
        } else if (this.enabled) {
            sb.setColor(Color.LIGHT_GRAY);
        } else {
            sb.setColor(Color.WHITE);
        }
        sb.draw(ImageMaster.OPTION_TOGGLE, x, y, w*Settings.scale, h*Settings.scale);
        if (this.enabled) {
            sb.setColor(Color.WHITE);
            sb.draw(ImageMaster.OPTION_TOGGLE_ON, x, y, w*Settings.scale, h*Settings.scale);
        }
        this.hb.render(sb);
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
            onToggle();
        }
	}
	
	private void onToggle() {
		this.enabled = !enabled;
		toggle.accept(this);
	}

	public void toggle() {
		onToggle();
	}
	
	@Override
	public int renderLayer() {
		return ModPanel.MIDDLE_LAYER;
	}

	@Override
	public int updateOrder() {
		return ModPanel.DEFAULT_UPDATE;
	}

	@Override
	public void set(float xPos, float yPos) {
		x = xPos*Settings.scale;
		y = yPos*Settings.scale;

		if (extendedHitbox) {
			UIElementModificationHelper.moveHitboxByOriginalParameters(hb, x - TOGGLE_X_EXTEND * Settings.scale,  y - TOGGLE_Y_DELTA * Settings.scale);
		} else {
			UIElementModificationHelper.moveHitboxByOriginalParameters(hb, x, y - TOGGLE_Y_DELTA * Settings.scale);
		}
	}

	@Override
	public void setX(float xPos) {
		set(xPos, y/Settings.scale);
	}

	@Override
	public void setY(float yPos) {
		set(x/Settings.scale, yPos);
	}

	@Override
	public float getX() {
		return x/Settings.scale;
	}

	@Override
	public float getY() {
		return y/Settings.scale;
	}
}
