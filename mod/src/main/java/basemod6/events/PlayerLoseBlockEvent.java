package basemod6.events;

public class PlayerLoseBlockEvent extends ResultEvent<Integer>
{
    private int amount;

	public PlayerLoseBlockEvent(int amount)
	{
		this.amount = amount;
	}

	@Override
	public Integer result()
	{
		return amount;
	}

	public int getBlockToLose()
	{
		return amount;
	}

	public void setBlockToLose(int amount)
	{
		this.amount = amount;
	}
}
