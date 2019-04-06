package basemod.patches.com.megacrit.cardcrawl.localization.LocalizedStrings;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.EditStringsEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(cls="com.megacrit.cardcrawl.localization.LocalizedStrings", method=SpirePatch.CONSTRUCTOR)
public class EditStrings {
	public static void Postfix(Object __obj_instance) {
		BaseMod6.EVENT_BUS.post(new EditStringsEvent());
		BaseMod.publishEditStrings();
	}
}
