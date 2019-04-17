package basemod6.events;

import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BattleEndEvent extends Event
{
	private AbstractRoom room;

	public BattleEndEvent(AbstractRoom room)
	{
		this.room = room;
	}

	public AbstractRoom getRoom()
	{
		return room;
	}
}
