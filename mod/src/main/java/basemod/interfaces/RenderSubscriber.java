package basemod.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

@Deprecated
public interface RenderSubscriber extends ISubscriber {
    void receiveRender(SpriteBatch sb);
}