package basemod.patches.com.megacrit.cardcrawl.helpers.CardLibrary;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(cls="com.megacrit.cardcrawl.helpers.CardLibrary", method="initialize")
public class EditCardsPatch {

	public static void Prefix() {
		// have mods register their changes to the card list here
		BaseMod.publishEditCards();
	}
	
}
