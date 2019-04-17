package basemod6.events;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class PreRoomRenderEvent extends Event
{
	private SpriteBatch sb;
	private AbstractRoom room;

    public PreRoomRenderEvent(SpriteBatch sb, AbstractRoom room)
	{
		this.sb = sb;
		this.room = room;
	}

	public SpriteBatch getSpriteBatch()
	{
		return sb;
	}

	public AbstractRoom getRoom()
	{
		return room;
	}
}
