package basemod6.events;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class EditColorsEvent extends Event
{
	public void addColor(AbstractCard.CardColor color, Color everythingColor,
	                     String attackBg, String skillBg, String powerBg, String energyOrb,
	                     String attackBgPortrait, String skillBgPortrait, String powerBgPortrait, String energyOrbPortrait,
	                     String cardEnergyOrb)
	{
		addColor(color, everythingColor, everythingColor, everythingColor, everythingColor, everythingColor, everythingColor, everythingColor, attackBg, skillBg, powerBg, energyOrb, attackBgPortrait, skillBgPortrait, powerBgPortrait, energyOrbPortrait, cardEnergyOrb);
	}

	public void addColor(AbstractCard.CardColor color, com.badlogic.gdx.graphics.Color bgColor,
	                            com.badlogic.gdx.graphics.Color backColor, com.badlogic.gdx.graphics.Color frameColor,
	                            com.badlogic.gdx.graphics.Color frameOutlineColor, com.badlogic.gdx.graphics.Color descBoxColor,
	                            com.badlogic.gdx.graphics.Color trailVfxColor, com.badlogic.gdx.graphics.Color glowColor,
	                            String attackBg, String skillBg, String powerBg, String energyOrb,
	                            String attackBgPortrait, String skillBgPortrait, String powerBgPortrait, String energyOrbPortrait)
	{
		addColor(color, bgColor, backColor, frameColor, frameOutlineColor, descBoxColor, trailVfxColor, glowColor, attackBg, skillBg, powerBg, energyOrb, attackBgPortrait, skillBgPortrait, powerBgPortrait, energyOrbPortrait, null);
	}

	public void addColor(AbstractCard.CardColor color, com.badlogic.gdx.graphics.Color bgColor,
	                            com.badlogic.gdx.graphics.Color backColor, com.badlogic.gdx.graphics.Color frameColor,
	                            com.badlogic.gdx.graphics.Color frameOutlineColor, com.badlogic.gdx.graphics.Color descBoxColor,
	                            com.badlogic.gdx.graphics.Color trailVfxColor, com.badlogic.gdx.graphics.Color glowColor,
	                            String attackBg, String skillBg, String powerBg, String energyOrb,
	                            String attackBgPortrait, String skillBgPortrait, String powerBgPortrait, String energyOrbPortrait,
	                            String cardEnergyOrb)
	{
		BaseMod._internal_AddColor(color, bgColor, backColor, frameColor, frameOutlineColor, descBoxColor, trailVfxColor, glowColor, attackBg, skillBg, powerBg, energyOrb, attackBgPortrait, skillBgPortrait, powerBgPortrait, energyOrbPortrait, cardEnergyOrb);
	}

	public void removeColor(AbstractCard.CardColor color)
	{
		BaseMod._internal_RemoveColor(color);
	}
}
