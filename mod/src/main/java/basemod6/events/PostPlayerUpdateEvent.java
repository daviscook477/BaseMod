package basemod6.events;

import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class PostPlayerUpdateEvent extends Event
{
	private AbstractPlayer player;

	public PostPlayerUpdateEvent(AbstractPlayer player)
	{
		this.player = player;
	}

	public AbstractPlayer getPlayer()
	{
		return player;
	}
}
