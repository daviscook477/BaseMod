package basemod6.events;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class PostPowerApplyEvent extends Event
{
	private AbstractPower power;
	private AbstractCreature target;
	private AbstractCreature source;

	public PostPowerApplyEvent(AbstractPower power, AbstractCreature target, AbstractCreature source)
	{
		this.power = power;
		this.target = target;
		this.source = source;
	}
}
