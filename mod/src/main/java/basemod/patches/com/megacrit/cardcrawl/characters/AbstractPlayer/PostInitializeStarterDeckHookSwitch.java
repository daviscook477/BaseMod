package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.PostCreateStartingDeckEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

@SpirePatch(
		clz=AbstractPlayer.class,
		method="initializeStarterDeck"
)
public class PostInitializeStarterDeckHookSwitch
{
    public static void Postfix(AbstractPlayer __instance)
	{
		BaseMod6.EVENT_BUS.post(new PostCreateStartingDeckEvent(__instance.chosenClass, __instance.masterDeck));
		BaseMod.publishPostCreateStartingDeck(__instance.chosenClass, __instance.masterDeck);
    }
}
