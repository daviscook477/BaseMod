package basemod.patches.com.megacrit.cardcrawl.helpers.RelicLibrary;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.EditRelicsEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(cls="com.megacrit.cardcrawl.helpers.RelicLibrary", method="initialize")
public class EditRelicsPatch {

	public static void Prefix() {
		// have mods register their changes to the relic list here
		BaseMod6.EVENT_BUS.post(new EditRelicsEvent());
		BaseMod.publishEditRelics();
	}
	
}
