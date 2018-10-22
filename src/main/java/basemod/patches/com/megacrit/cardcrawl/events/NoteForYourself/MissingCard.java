package basemod.patches.com.megacrit.cardcrawl.events.NoteForYourself;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.IronWave;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.events.shrines.NoteForYourself;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.lang.reflect.Field;

@SpirePatch(
		cls="com.megacrit.cardcrawl.events.shrines.NoteForYourself",
		method="initializeObtainCard"
)
public class MissingCard {
	public static void Replace(NoteForYourself __instance)
	{
		String cardID = CardCrawlGame.playerPref.getString("NOTE_CARD", IronWave.ID);
		AbstractCard card = CardLibrary.getCard(cardID);
		if (card == null) {
			BaseMod.logger.warn("NoteForYourself trying to receive card \"" + cardID + "\" which does not exist. Defaulting to Iron Wave.");
			card = CardLibrary.getCard(IronWave.ID).makeCopy();
		} else {
			card = card.makeCopy();
			for (int i=0; i<CardCrawlGame.playerPref.getInteger("NOTE_UPGRADE", 0); ++i) {
				card.upgrade();
			}
		}

		try {
			Field obtainCard = NoteForYourself.class.getDeclaredField("obtainCard");
			obtainCard.setAccessible(true);

			obtainCard.set(__instance, card);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
}
