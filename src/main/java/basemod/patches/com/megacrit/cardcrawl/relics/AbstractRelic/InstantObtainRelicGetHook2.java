package basemod.patches.com.megacrit.cardcrawl.relics.AbstractRelic;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;

@SpirePatch(cls="com.megacrit.cardcrawl.relics.AbstractRelic", method="instantObtain",paramtypes= {})
public class InstantObtainRelicGetHook2 {
	@SpireInsertPatch(rloc=2)
	public static void Insert(Object relic) {
		BaseMod.publishRelicGet((AbstractRelic) relic);
	}
}
