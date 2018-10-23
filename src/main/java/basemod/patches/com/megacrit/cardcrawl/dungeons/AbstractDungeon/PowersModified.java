package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(cls="com.megacrit.cardcrawl.dungeons.AbstractDungeon", method="onModifyPower")
public class PowersModified {

	public static void Postfix() {
		BaseMod.publishOnPowersModified();
	}
	
}
