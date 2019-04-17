package basemod6.events;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PostRenderEvent extends Event
{
	private SpriteBatch sb;

	public PostRenderEvent(SpriteBatch sb)
	{
		this.sb = sb;
	}

	public SpriteBatch getSpriteBatch()
	{
		return sb;
	}
}
