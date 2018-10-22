package basemod;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IUIElement {
	void render(SpriteBatch sb);
	void update();
	int renderLayer();
	int updateOrder();
}
