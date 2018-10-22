package basemod.patches.com.megacrit.cardcrawl.unlock.UnlockTracker;

import basemod.BaseMod;
import basemod.abstracts.CustomUnlock;
import basemod.abstracts.CustomUnlockBundle;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.unlock.AbstractUnlock;

import java.util.ArrayList;

@SpirePatch(cls="com.megacrit.cardcrawl.unlock.UnlockTracker", method="getUnlockBundle")
public class GetUnlockBundleSwitch {

	public static ArrayList<AbstractUnlock> Postfix(ArrayList<AbstractUnlock> tmpBundle, Object cObj, int unlockLevel) {
		AbstractPlayer.PlayerClass chosenClass = (AbstractPlayer.PlayerClass) cObj;
		CustomUnlockBundle bundle = BaseMod.getUnlockBundleFor(chosenClass, unlockLevel);
		if (bundle != null) {
			ArrayList<CustomUnlock> unlocks = bundle.getUnlocks();
			for (CustomUnlock unlock : unlocks) {
				tmpBundle.add(unlock);
			}
		}
		return tmpBundle;
	}
	
	
}
