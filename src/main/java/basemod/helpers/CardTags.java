package basemod.helpers;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.Arrays;
import java.util.List;

public class CardTags
{
	public static boolean hasTag(AbstractCard card, AbstractCard.CardTags tag)
	{
		return card.hasTag(tag);
	}

	public static List<AbstractCard.CardTags> getAllTags(AbstractCard card)
	{
		return card.tags;
	}

	public static void addTags(AbstractCard card, AbstractCard.CardTags... tags)
	{
		card.tags.addAll(Arrays.asList(tags));
	}

	public static void removeTags(AbstractCard card, AbstractCard.CardTags... tags)
	{
		card.tags.removeAll(Arrays.asList(tags));
	}

	public static void removeAllTags(AbstractCard card)
	{
		card.tags.clear();
	}

	public static void toggleTag(AbstractCard card, AbstractCard.CardTags tag)
	{
		if (hasTag(card, tag)) {
			removeTags(card, tag);
		} else {
			addTags(card, tag);
		}
	}
}
