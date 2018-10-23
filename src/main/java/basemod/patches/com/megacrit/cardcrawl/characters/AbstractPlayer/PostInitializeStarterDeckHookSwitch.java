package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import basemod.BaseMod;
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
		BaseMod.publishPostCreateStartingDeck(__instance.chosenClass, __instance.masterDeck);
    }
}
