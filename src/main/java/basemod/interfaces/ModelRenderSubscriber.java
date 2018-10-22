package basemod.interfaces;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public interface ModelRenderSubscriber extends ISubscriber {
	void receiveModelRender(ModelBatch batch, Environment env);
}
