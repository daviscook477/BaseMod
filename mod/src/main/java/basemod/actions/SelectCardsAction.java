package basemod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SelectCardsAction
    extends AbstractGameAction

{
    private Predicate<AbstractCard> predicate;
    private Consumer<List<AbstractCard>> callback;
    private String text;
    private boolean anyNumber;
    private CardGroup selectGroup;

    /**
    * @param group - ArrayList of cards to filter and select from.
    * Example: AbstractDungeon.player.discardPile.group
    * @param amount - maximum number of cards allowed for selectoin
    * @param textForSelect - text that will be displayed on the grid select screen at the bottom. It will show just this text with nothing else added by itself.
    * @param anyNumber - whether player has to select exact number of cards (amount) or any number up to, including 0.
    * false for exact number.
    * @param cardFilter - Filters the cards in the group.
    * Example: if you want to display only skills, it would be c -> c.type == CardType.SKILL
    * If you don't need the filter, set it as c -> true
    * @param callback - What to do with cards selected.
    * Example: if you want player to lose 1 hp per card and upgrade each card selected, it would look like
    * list -> {
    * addToBot(
    * new LoseHPAction(player, player, list.size());
    * list.forEach(c -> c.upgrade());
    * )}
    *
    * if there's no callback the action will not trigger simply because you told player to "select cards to do nothing with them"
    * */

    public SelectCardsAction(ArrayList<AbstractCard> group, int amount, String textForSelect, boolean anyNumber, Predicate<AbstractCard> cardFilter, Consumer<List<AbstractCard>> callback)
    {
        this.amount = amount;
        this.duration = this.startDuration = Settings.ACTION_DUR_XFAST;
        text = textForSelect;
        this.anyNumber = anyNumber;
        this.callback = callback;
        this.selectGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.selectGroup.group.addAll(group.stream().filter(cardFilter).collect(Collectors.toList()));
    }

    public SelectCardsAction(ArrayList<AbstractCard> group, String textForSelect, boolean anyNumber, Predicate<AbstractCard> cardFilter, Consumer<List<AbstractCard>> callback)
    {
        this(group, 1, textForSelect, anyNumber, cardFilter, callback);
    }

    public SelectCardsAction(ArrayList<AbstractCard> group, String textForSelect, Predicate<AbstractCard> cardFilter, Consumer<List<AbstractCard>> callback)
    {
        this(group, 1, textForSelect, false, cardFilter, callback);
    }

    public SelectCardsAction(ArrayList<AbstractCard> group, String textForSelect, Consumer<List<AbstractCard>> callback)
    {
        this(group, 1, textForSelect, false, c -> true, callback);
    }

    public SelectCardsAction(ArrayList<AbstractCard> group, int amount, String textForSelect, Consumer<List<AbstractCard>> callback)
    {
        this(group, amount, textForSelect, false, c -> true, callback);
    }

    @Override
    public void update()
    {
        if (this.duration == this.startDuration)
        {
            if ((selectGroup.size() == 0) || callback == null)
            {
                isDone = true;
                return;
            }

            if (selectGroup.size() <= amount && !anyNumber)
            {
                callback.accept(selectGroup.group);
                isDone = true;
                return;
            }

            AbstractDungeon.gridSelectScreen.open(selectGroup, amount, anyNumber, text);
            tickDuration();
        }

        if (AbstractDungeon.gridSelectScreen.selectedCards.size() != 0)
        {
            callback.accept(AbstractDungeon.gridSelectScreen.selectedCards);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            AbstractDungeon.player.hand.refreshHandLayout();
            isDone = true;
            return;
        }
        tickDuration();
    }
}