package basemod6.events;

import com.megacrit.cardcrawl.potions.AbstractPotion;

public class PrePotionUseEvent extends Event
{
	private AbstractPotion potion;

	public PrePotionUseEvent(AbstractPotion potion)
	{
		this.potion = potion;
	}

	public AbstractPotion getPotion()
	{
		return potion;
	}
}
