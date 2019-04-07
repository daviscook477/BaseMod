package basemod.patches.com.megacrit.cardcrawl.characters.CharacterManager;

import basemod6.BaseMod6;
import basemod6.events.EditCharactersEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.CharacterManager;

@SpirePatch(
		clz=CharacterManager.class,
		method=SpirePatch.CONSTRUCTOR
)
public class EditCharactersHook
{
	public static void Postfix(CharacterManager __instance)
	{
		BaseMod6.EVENT_BUS.post(new EditCharactersEvent(__instance));
	}
}
