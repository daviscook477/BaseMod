package basemod.patches.com.megacrit.cardcrawl.unlock.UnlockTracker;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(cls="com.megacrit.cardcrawl.unlock.UnlockTracker", method="refresh")
public class PostRefresh {
	public static void Postfix() {
		BaseMod.publishPostRefresh();
	}
}
