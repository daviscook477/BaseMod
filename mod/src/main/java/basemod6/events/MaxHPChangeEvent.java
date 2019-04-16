package basemod6.events;

public class MaxHPChangeEvent extends ResultEvent<Integer>
{
	private int amount;

	public MaxHPChangeEvent(int amount)
	{
		this.amount = amount;
	}

	@Override
	public Integer result()
	{
		return amount;
	}

	public int getMaxHPChange()
	{
		return amount;
	}

	public void setMaxHPChange(int amount)
	{
		this.amount = amount;
	}
}
