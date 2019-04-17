package basemod6.events;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class PreRenderEvent extends Event
{
	private OrthographicCamera camera;

	public PreRenderEvent(OrthographicCamera camera)
	{
		this.camera = camera;
	}

	public OrthographicCamera getCamera()
	{
		return camera;
	}
}
