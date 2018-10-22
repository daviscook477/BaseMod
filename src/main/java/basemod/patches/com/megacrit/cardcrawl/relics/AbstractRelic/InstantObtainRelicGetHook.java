package basemod.patches.com.megacrit.cardcrawl.relics.AbstractRelic;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.relics.AbstractRelic;

@SpirePatch(cls="com.megacrit.cardcrawl.relics.AbstractRelic", method="instantObtain",paramtypes= {"com.megacrit.cardcrawl.characters.AbstractPlayer","int","boolean"})
public class InstantObtainRelicGetHook {
	@SpireInsertPatch(rloc=2)
	public static void Insert(Object relic, AbstractPlayer p, int slot, boolean callOnEquip) {
		BaseMod.publishRelicGet((AbstractRelic) relic);
	}
}
