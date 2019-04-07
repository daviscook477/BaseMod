package basemod6.events;

public class MaxHPChangeEvent extends Event
{
	private int[] amount;

	public MaxHPChangeEvent(int[] amount)
	{
		this.amount = amount;
	}

	public int getMaxHPChange()
	{
		return amount[0];
	}

	public void setMaxHPChange(int amount)
	{
		this.amount[0] = amount;
	}
}
