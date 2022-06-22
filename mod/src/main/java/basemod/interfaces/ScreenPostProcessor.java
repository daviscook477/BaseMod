package basemod.interfaces;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface ScreenPostProcessor {
    void postProcess(SpriteBatch sb, TextureRegion frameTexture, OrthographicCamera camera);

    // Lower = earlier
    default int priority() {
        return 50;
    }
}
