package basemod.interfaces;

import com.badlogic.gdx.graphics.OrthographicCamera;

@Deprecated
public interface PreRenderSubscriber extends ISubscriber {
	void receiveCameraRender(OrthographicCamera camera);
}
