package basemod6.events;

public class EnergyRechargeEvent extends Event
{
	private int energyGain;

	public EnergyRechargeEvent(int energyGain)
	{
		this.energyGain = energyGain;
	}

	public int getEnergyGain()
	{
		return energyGain;
	}
}
