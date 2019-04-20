package basemod6.events;

public class PostEnergyRechargeEvent extends Event
{
	private int energyGain;

	public PostEnergyRechargeEvent(int energyGain)
	{
		this.energyGain = energyGain;
	}

	public int getEnergyGain()
	{
		return energyGain;
	}
}
