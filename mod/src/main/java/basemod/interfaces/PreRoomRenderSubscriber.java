package basemod.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

@Deprecated
public interface PreRoomRenderSubscriber extends ISubscriber{
	void receivePreRoomRender(SpriteBatch sb);
}
