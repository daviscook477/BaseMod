package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

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
		if (__instance.masterMaxOrbs == 0 && __instance.maxOrbs == 0) {
			__instance.masterMaxOrbs = 1;
			__instance.increaseMaxOrbSlots(1, true);
		}
	}
}
