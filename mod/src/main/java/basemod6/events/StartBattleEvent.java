package basemod6.events;

import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class StartBattleEvent extends Event
{
	private AbstractRoom room;

	public StartBattleEvent(AbstractRoom room)
	{
		this.room = room;
	}

	public AbstractRoom getRoom()
	{
		return room;
	}
}
