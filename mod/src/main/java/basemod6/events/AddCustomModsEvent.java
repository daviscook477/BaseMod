package basemod6.events;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.RedCards;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.custom.CustomMod;

import java.util.List;

public class AddCustomModsEvent extends Event
{
	private List<CustomMod> modList;

	public AddCustomModsEvent(List<CustomMod> modList)
	{
		this.modList = modList;

		CustomMod charMod = new CustomMod("Modded Character Cards", "p", false);
		for (AbstractPlayer character : BaseMod.getModdedCharacters()) {
			CustomMod mod = new CustomMod(RedCards.ID, "g", true);
			mod.ID = character.chosenClass.name() + charMod.name;
			mod.name = character.getLocalizedCharacterName() + charMod.name;
			mod.description = character.getLocalizedCharacterName() + charMod.description;
			String label = FontHelper.colorString("[" + mod.name + "]", mod.color) + " " + mod.description;
			ReflectionHacks.setPrivate(mod, CustomMod.class, "label", label);
			float height = -FontHelper.getSmartHeight(FontHelper.charDescFont, label, 1050.0F * Settings.scale, 32.0F * Settings.scale) + 70.0F * Settings.scale;
			ReflectionHacks.setPrivate(mod, CustomMod.class, "height", height);

			addCustomMod(mod);
		}
	}

	public void addCustomMod(CustomMod mod)
	{
		int lastIndex = modList.size();
		for (int i=0; i<modList.size(); ++i) {
			if (modList.get(i).color.equals(mod.color)) {
				lastIndex = i + 1;
			}
		}
		modList.add(lastIndex, mod);
	}
}
