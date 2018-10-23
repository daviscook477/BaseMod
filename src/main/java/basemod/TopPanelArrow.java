package basemod;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.Settings;

public abstract class TopPanelArrow extends ClickableUIElement
{
	public TopPanelArrow(Texture image, float x, float y, float hb_w, float hb_h)
	{
		super(image, x, y, hb_w, hb_h);

		hitbox.move(this.x + image.getWidth() / 2.0f * Settings.scale, this.y + image.getHeight() / 2.0f * Settings.scale);
	}

	@Override
	protected void onHover()
	{
		tint.a = 0.25f;
	}

	@Override
	protected void onUnhover()
	{
		tint.a = 0;
	}
}
