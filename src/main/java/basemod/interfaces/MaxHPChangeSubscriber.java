package basemod.interfaces;

public interface MaxHPChangeSubscriber extends ISubscriber
{
	int receiveMapHPChange(int amount);
}
