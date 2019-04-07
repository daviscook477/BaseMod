package basemod6.events;

import basemod.BaseMod;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.CharacterManager;

public class EditCharactersEvent extends Event
{
	private CharacterManager characterManager;

	public EditCharactersEvent(CharacterManager characterManager)
	{
		this.characterManager = characterManager;
	}

	public void addCharacter(AbstractPlayer character,
	                         String selectButtonPath,
	                         String portraitPath,
	                         String customModeButtonPath)
	{
		BaseMod._internal_AddCharacter(characterManager, character, selectButtonPath, portraitPath, customModeButtonPath);
	}

	public void addCharacter(AbstractPlayer character,
	                         String selectButtonPath,
	                         String portraitPath)
	{
		addCharacter(character, selectButtonPath, portraitPath, null);
	}
}
