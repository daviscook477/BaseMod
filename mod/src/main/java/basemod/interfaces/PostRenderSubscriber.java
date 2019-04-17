package basemod.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

@Deprecated
public interface PostRenderSubscriber extends ISubscriber {
    void receivePostRender(SpriteBatch sb);
}