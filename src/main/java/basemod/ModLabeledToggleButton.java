package basemod;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.function.Consumer;

public class ModLabeledToggleButton implements IUIElement {
	private static final float TEXT_X_OFFSET = 40.0f;
	private static final float TEXT_Y_OFFSET = 8.0f;
	
	public ModToggleButton toggle;
	public ModLabel text;
	
	public ModLabeledToggleButton(String labelText, float xPos, float yPos,
			Color color, BitmapFont font, boolean enabled, ModPanel p,
			Consumer<ModLabel> labelUpdate, Consumer<ModToggleButton> c) {
		toggle = new ModToggleButton(xPos, yPos, enabled, false, p, c);
		text = new ModLabel(labelText, xPos + TEXT_X_OFFSET, yPos + TEXT_Y_OFFSET,
				color, font, p, labelUpdate);
		
		// TODO: implement multi-line text
		toggle.wrapHitboxToText(labelText, 1000.0f, 0.0f, font);
	}
	
	
	@Override
	public void render(SpriteBatch sb) {
		toggle.render(sb);
		text.render(sb);
	}

	@Override
	public void update() {
		toggle.update();
		text.update();
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
