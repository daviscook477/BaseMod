package basemod.patches.com.megacrit.cardcrawl.helpers.CardLibrary;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;

@SpirePatch(
		clz=CardLibrary.class,
		method="addBlueCards",
		paramtypez={}
)
public class BlueCardsPatch
{
	public static void Postfix()
	{
		// add new cards
		for (AbstractCard card : BaseMod.getBlueCardsToAdd()) {
			CardLibrary.add(card);
		}

		// remove old cards
		for (String cardID : BaseMod.getBlueCardsToRemove()) {
			CardLibrary.cards.remove(cardID);
			CardLibrary.blueCards--;
			CardLibrary.totalCardCount--;
		}
	}
}
