package basemod6.events;

import basemod.BaseMod;
import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditCardsEvent extends Event
{
	private Map<AbstractCard.CardColor, List<AbstractCard>> toAdd = new HashMap<>();
	private Map<AbstractCard.CardColor, List<Pair<String, AbstractCard.CardColor>>> toRemove = new HashMap<>();

	@Override
	public void finish()
	{
		// Add cards
		for (List<AbstractCard> cards : toAdd.values()) {
			for (AbstractCard card : cards) {
				CardLibrary.add(card);
			}
		}
		// Remove cards
        for (Map.Entry<AbstractCard.CardColor, List<Pair<String, AbstractCard.CardColor>>> entry : toRemove.entrySet()) {
			for (Pair<String, AbstractCard.CardColor> card : entry.getValue()) {
				CardLibrary.cards.remove(card.getKey());
				switch (entry.getKey()) {
					case RED:
						--CardLibrary.redCards;
						break;
					case GREEN:
						--CardLibrary.greenCards;
						break;
					case BLUE:
						--CardLibrary.blueCards;
						break;
					case COLORLESS:
						--CardLibrary.colorlessCards;
						break;
					case CURSE:
						--CardLibrary.curseCards;
						break;
					default:
						BaseMod.decrementCardCount(card.getValue());
						break;
				}
				--CardLibrary.totalCardCount;
			}
		}
	}

	public void addCard(AbstractCard card)
	{
		toAdd.putIfAbsent(card.color, new ArrayList<>());
		List<AbstractCard> cards = toAdd.get(card.color);
		cards.add(card);
	}

	public void removeCard(String card, AbstractCard.CardColor color)
	{
		toRemove.putIfAbsent(color, new ArrayList<>());
		List<Pair<String, AbstractCard.CardColor>> cards = toRemove.get(color);
		cards.add(new Pair<>(card, color));
	}

	public void addDynamicVariable(DynamicVariable dv)
	{
		BaseMod.cardDynamicVariableMap.put(dv.key(), dv);
	}
}
