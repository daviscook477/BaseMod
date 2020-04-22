package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

@SpirePatch(
		cls="com.megacrit.cardcrawl.characters.AbstractPlayer",
		method="channelOrb"
)
public class GiveOrbSlotOnChannel
{
	public static void Prefix(AbstractPlayer __instance, AbstractOrb orbToSet)
	{
		// For modded characters, grant an orb slot when channeling to facilitate cross-class card
		// generation. For official characters, do nothing, to avoid changing the behavior of the
		// base game (via Watcher's Foreign Influence, the Note for Yourself event, etc.).
		if (BaseMod.isBaseGameCharacter(__instance)) {
			return;
		}
		if (__instance.masterMaxOrbs == 0 && __instance.maxOrbs == 0) {
			__instance.masterMaxOrbs = 1;
			__instance.increaseMaxOrbSlots(1, true);
		}
	}
}
