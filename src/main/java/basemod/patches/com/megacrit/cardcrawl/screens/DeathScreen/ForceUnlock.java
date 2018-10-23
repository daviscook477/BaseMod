package basemod.patches.com.megacrit.cardcrawl.screens.DeathScreen;

import basemod.DevConsole;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

@SpirePatch(cls="com.megacrit.cardcrawl.screens.DeathScreen", method="calculateUnlockProgress")
public class ForceUnlock {
	public static void Postfix(Object __obj_instance) {
		DeathScreen screen = (DeathScreen) __obj_instance;
		System.out.println("Managing unlocks");
		if (DevConsole.forceUnlocks) {
			System.out.println("Attempting to force unlock");
			int unlockLevel = DevConsole.unlockLevel;
			screen.unlockBundle = UnlockTracker.getUnlockBundle(
					AbstractDungeon.player.chosenClass,
					(unlockLevel != -1) ? unlockLevel : UnlockTracker.getUnlockLevel(AbstractDungeon.player.chosenClass));
			if (screen.unlockBundle.size() == 0) {
				screen.unlockBundle = null;
				screen.returnButton.appear(Settings.WIDTH / 2.0F, Settings.HEIGHT * 0.15F, DeathScreen.TEXT[40]);
			}
		}
	}
}
