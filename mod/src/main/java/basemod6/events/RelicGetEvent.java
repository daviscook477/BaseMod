package basemod6.events;

import com.megacrit.cardcrawl.relics.AbstractRelic;

public class RelicGetEvent extends Event
{
	private AbstractRelic relic;

	public RelicGetEvent(AbstractRelic relic)
	{
		this.relic = relic;
	}

	public AbstractRelic getRelic()
	{
		return relic;
	}
}
