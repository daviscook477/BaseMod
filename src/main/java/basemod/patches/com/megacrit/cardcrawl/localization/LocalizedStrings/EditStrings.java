package basemod.patches.com.megacrit.cardcrawl.localization.LocalizedStrings;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(cls="com.megacrit.cardcrawl.localization.LocalizedStrings", method=SpirePatch.CONSTRUCTOR)
public class EditStrings {
	public static void Postfix(Object __obj_instance) {
		BaseMod.publishEditStrings();
	}
}
