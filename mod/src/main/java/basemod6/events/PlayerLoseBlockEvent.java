package basemod6.events;

public class PlayerLoseBlockEvent extends Event
{
    private int[] amount;

	public PlayerLoseBlockEvent(int[] amount)
	{
		this.amount = amount;
	}

	public int getBlockToLose()
	{
		return amount[0];
	}

	public void setBlockToLose(int amount)
	{
		this.amount[0] = amount;
	}
}
