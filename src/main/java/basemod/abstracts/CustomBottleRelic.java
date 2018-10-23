package basemod.abstracts;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.function.Predicate;

public interface CustomBottleRelic
{
	Predicate<AbstractCard> isOnCard();
}
