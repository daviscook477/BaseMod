package basemod6.events;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RenderEvent extends Event
{
	private SpriteBatch sb;

	public RenderEvent(SpriteBatch sb)
	{
		this.sb = sb;
	}

	public SpriteBatch getSpriteBatch()
	{
		return sb;
	}
}
