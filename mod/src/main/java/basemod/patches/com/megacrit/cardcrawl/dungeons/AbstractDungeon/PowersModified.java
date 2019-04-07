package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.PowersModifiedEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(cls="com.megacrit.cardcrawl.dungeons.AbstractDungeon", method="onModifyPower")
public class PowersModified {

	public static void Postfix() {
		BaseMod6.EVENT_BUS.post(new PowersModifiedEvent());
		BaseMod.publishOnPowersModified();
	}
	
}
