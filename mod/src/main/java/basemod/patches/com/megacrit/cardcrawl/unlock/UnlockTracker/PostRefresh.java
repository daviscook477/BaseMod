package basemod.patches.com.megacrit.cardcrawl.unlock.UnlockTracker;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.SetUnlocksEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

@SpirePatch(
		clz=UnlockTracker.class,
		method="refresh"
)
public class PostRefresh {
	public static void Postfix() {
		BaseMod6.EVENT_BUS.post(new SetUnlocksEvent());
		BaseMod.publishPostRefresh();
	}
}
