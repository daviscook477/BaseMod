package basemod;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IUIElement {
	void render(SpriteBatch sb);
	void update();
	int renderLayer();
	int updateOrder();
	//Moves the IUIElement and its Hitbox to the specified coordinate
	//Should not be premultiplied by Settings.scale if the constructor of the IUIElement doesn't either.
	default void set(float xPos, float yPos) {}
	default void setX(float xPos) {}
	default void setY(float yPos) {}
	//Returns the x/y coordinate of the IUIElement.
	//Divided by Settings.scale if the constructor multiplies the input by it
	default float getX() {return Integer.MIN_VALUE;}
	default float getY() {return Integer.MIN_VALUE;}
}
