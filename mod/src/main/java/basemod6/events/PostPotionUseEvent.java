package basemod6.events;

import com.megacrit.cardcrawl.potions.AbstractPotion;

public class PostPotionUseEvent extends Event
{
	private AbstractPotion potion;

	public PostPotionUseEvent(AbstractPotion potion)
	{
		this.potion = potion;
	}

	public AbstractPotion getPotion()
	{
		return potion;
	}
}
