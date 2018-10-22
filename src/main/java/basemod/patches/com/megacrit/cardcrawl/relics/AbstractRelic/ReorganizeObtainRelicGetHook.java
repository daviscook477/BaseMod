package basemod.patches.com.megacrit.cardcrawl.relics.AbstractRelic;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.relics.AbstractRelic;

@SpirePatch(cls="com.megacrit.cardcrawl.relics.AbstractRelic", method="reorganizeObtain")
public class ReorganizeObtainRelicGetHook {
	@SpireInsertPatch(rloc=2)
	public static void Insert(Object relic, AbstractPlayer p,int slot, boolean callOnEquip, int relicAmount) {
		BaseMod.publishRelicGet((AbstractRelic) relic);;
	}
}
