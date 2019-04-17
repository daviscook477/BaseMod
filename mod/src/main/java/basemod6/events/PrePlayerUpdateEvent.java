package basemod6.events;

import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class PrePlayerUpdateEvent extends Event
{
	private AbstractPlayer player;

	public PrePlayerUpdateEvent(AbstractPlayer player)
	{
		this.player = player;
	}

	public AbstractPlayer getPlayer()
	{
		return player;
	}
}
