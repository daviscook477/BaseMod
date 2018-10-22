package basemod.patches.com.megacrit.cardcrawl.cards.CardGroup;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

@SpirePatch(cls="com.megacrit.cardcrawl.cards.CardGroup", method="moveToExhaustPile")
public class MoveToExhaustPileHook {
	@SpireInsertPatch(rloc=6)
	public static void Insert(Object __obj_instance, Object cObj) {
		BaseMod.publishPostExhaust((AbstractCard) cObj);
	}
}
