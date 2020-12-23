package basemod.patches.com.megacrit.cardcrawl.screens.DeathScreen;

import basemod.DevConsole;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.GameOverScreen;
import com.megacrit.cardcrawl.ui.buttons.ReturnToMenuButton;
import com.megacrit.cardcrawl.unlock.AbstractUnlock;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;

@SpirePatch(
		clz=GameOverScreen.class,
		method="calculateUnlockProgress"
)
public class ForceUnlock {
	public static void Postfix(GameOverScreen __instance) {
		System.out.println("Managing unlocks");
		if (DevConsole.forceUnlocks) {
			System.out.println("Attempting to force unlock");
			int unlockLevel = DevConsole.unlockLevel;
			ArrayList<AbstractUnlock> unlockBundle = UnlockTracker.getUnlockBundle(
					AbstractDungeon.player.chosenClass,
					(unlockLevel != -1) ? unlockLevel : UnlockTracker.getUnlockLevel(AbstractDungeon.player.chosenClass));
			if (unlockBundle.size() == 0) {
				unlockBundle = null;
				ReturnToMenuButton returnButton = ReflectionHacks.getPrivate(__instance, GameOverScreen.class, "returnButton");
				if (returnButton != null) {
					returnButton.appear(Settings.WIDTH / 2.0F, Settings.HEIGHT * 0.15F, DeathScreen.TEXT[40]);
				}
			}
			ReflectionHacks.setPrivate(__instance, GameOverScreen.class, "unlockBundle", unlockBundle);
		}
	}
}
