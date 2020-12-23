package basemod.patches.com.megacrit.cardcrawl.screens.DeathScreen;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.GameOverScreen;
import com.megacrit.cardcrawl.unlock.AbstractUnlock;

import java.util.ArrayList;

@SpirePatch(
		clz=GameOverScreen.class,
		method="calculateUnlockProgress"
)
public class NotFoundFix {
	public static void Postfix(GameOverScreen __instance) {
		ArrayList<AbstractUnlock> unlockBundle = ReflectionHacks.getPrivate(__instance, GameOverScreen.class, "unlockBundle");
		if (unlockBundle != null && unlockBundle.size() <= 0) {
			// game checks for null, not for wrong size
			ReflectionHacks.setPrivate(__instance, GameOverScreen.class, "unlockBundle", null);
		}
	}
}
