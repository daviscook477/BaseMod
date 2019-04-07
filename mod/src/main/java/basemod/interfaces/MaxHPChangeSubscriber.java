package basemod.interfaces;

@Deprecated
public interface MaxHPChangeSubscriber extends ISubscriber
{
	@Deprecated
	default int receiveMapHPChange(int amount)
	{
		return receiveMaxHPChange(amount);
	}

	default int receiveMaxHPChange(int amount)
	{
		return amount;
	}
}
