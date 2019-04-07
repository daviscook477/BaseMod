package basemod6.events;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.potions.AbstractPotion;

public class EditPotionsEvent extends Event
{
	public void addPotion(Class<? extends AbstractPotion> potionClass, Color liquidColor, Color hybridColor, Color spotsColor, String potionID)
	{
		addPotion(null, potionClass, liquidColor, hybridColor, spotsColor, potionID);
	}

	public void addPotion(AbstractPlayer.PlayerClass playerClass, Class<? extends AbstractPotion> potionClass, Color liquidColor, Color hybridColor, Color spotsColor, String potionID)
	{
		BaseMod._internal_AddPotion(playerClass, potionClass, liquidColor, hybridColor, spotsColor, potionID);
	}

	public void removePotion(String potionID)
	{
		BaseMod._internal_RemovePotion(potionID);
	}
}
