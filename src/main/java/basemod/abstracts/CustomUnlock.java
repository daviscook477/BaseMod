package basemod.abstracts;

import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.unlock.AbstractUnlock;

public class CustomUnlock extends AbstractUnlock {

	public CustomUnlock(String cardID) {
		this(UnlockType.CARD, cardID);
	}

	public CustomUnlock(UnlockType type, String id) {
		this.type = type;
		switch (this.type) {
			case CARD:
				card = CardLibrary.getCard(id);
				key = card.cardID;
				title = card.name;
			case RELIC:
				relic = RelicLibrary.getRelic(id);
				key = relic.relicId;
				title = relic.name;
		}
	}
	
}
