package basemod.animations;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public abstract class AbstractAnimation
{
    public enum Type
    {
        NONE, SPRITE, MODEL
    }

    public abstract Type type();

    public void renderSprite(SpriteBatch batch) {}
    public void renderModel(ModelBatch batch, Environment env) {}
}
