package basemod.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface PostRenderSubscriber extends ISubscriber {
    void receivePostRender(SpriteBatch sb);
}