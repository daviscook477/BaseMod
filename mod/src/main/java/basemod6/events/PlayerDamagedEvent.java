package basemod6.events;

public class PlayerDamagedEvent extends Event
{
	private int[] amount;

	public PlayerDamagedEvent(int[] amount)
	{
		this.amount = amount;
	}

	public int getDamage()
	{
		return amount[0];
	}

	public void setDamage(int amount)
	{
		this.amount[0] = amount;
	}
}
