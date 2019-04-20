package basemod6.events;

import com.megacrit.cardcrawl.potions.AbstractPotion;

public class PotionUseEvent extends Event
{
	private AbstractPotion potion;

	public PotionUseEvent(AbstractPotion potion)
	{
		this.potion = potion;
	}

	public AbstractPotion getPotion()
	{
		return potion;
	}
}
