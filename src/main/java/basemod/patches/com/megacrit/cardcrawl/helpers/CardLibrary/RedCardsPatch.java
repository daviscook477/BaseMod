package basemod.patches.com.megacrit.cardcrawl.helpers.CardLibrary;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;

@SpirePatch(
		clz=CardLibrary.class,
		method="addRedCards",
		paramtypez={}
)
public class RedCardsPatch {
	public static void Postfix() {
		// add new cards
		for (AbstractCard card : BaseMod.getRedCardsToAdd()) {
			CardLibrary.add(card);
		}
		
		// remove old cards
		for (String cardID : BaseMod.getRedCardsToRemove()) {
			CardLibrary.cards.remove(cardID);
			CardLibrary.redCards--;
			CardLibrary.totalCardCount--;
		}
	}
}
