package basemod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SelectCardsInHandAction
    extends AbstractGameAction
{

    private Predicate<AbstractCard> predicate;
    private Consumer<List<AbstractCard>> callback;
    private String text;
    private boolean anyNumber, canPickZero;
    private ArrayList<AbstractCard> hand;
    private ArrayList<AbstractCard> tempHand;

    /**
    * @param amount - max number of cards player can select
    * @param textForSelect - text that will be displayed at the top of the screen. It will be automatically attached to base game "Select X card/s to " text
    * @param anyNumber - whether player has to select exact number of cards or any number up to.
    * false for exact number
    * @param canPickZero - whether player can skip selection by picking zero cards.
    * @param cardFilter - filter that will be applied to cards in hand.
    * @param cardFilter - Filters the cards in the group.
    * Example: if you want to display only skills, it would be c -> c.type == CardType.SKILL
    * If you don't need the filter, set it as c -> true
    * @param callback - What to do with cards selected.
    * Example: if you want to lose 1 hp and upgrade each card selected, it would look like
    * list -> {
    * addToBot(
    * new LoseHPAction(player, player, list.size());
    * list.forEach(c -> c.upgrade());
    * )}
    *
    * if there's no callback the action will not trigger simply because you told player to "select cards to do nothing with them"
    * */

    public SelectCardsInHandAction(int amount, String textForSelect, boolean anyNumber, boolean canPickZero, Predicate<AbstractCard> cardFilter, Consumer<List<AbstractCard>> callback)
    {
        this.amount = amount;
        this.duration = this.startDuration = Settings.ACTION_DUR_XFAST;
        text = textForSelect;
        this.anyNumber = anyNumber;
        this.canPickZero = canPickZero;
        this.predicate = cardFilter;
        this.callback = callback;
        this.hand = AbstractDungeon.player.hand.group;
        tempHand = new ArrayList<>();
        tempHand.addAll(hand);
    }

    public SelectCardsInHandAction(int amount, String textForSelect, Predicate<AbstractCard> cardFilter, Consumer<List<AbstractCard>> callback)
    {
        this(amount, textForSelect, false, false, cardFilter, callback);
    }

    public SelectCardsInHandAction(int amount, String textForSelect, Consumer<List<AbstractCard>> callback)
    {
        this(amount, textForSelect, false, false, (c -> true), callback);
    }

    public SelectCardsInHandAction(String textForSelect, Predicate<AbstractCard> cardFilter, Consumer<List<AbstractCard>> callback)
    {
        this(1, textForSelect, false, false, cardFilter, callback);
    }

    public SelectCardsInHandAction(String textForSelect, Consumer<List<AbstractCard>> callback)
    {
        this(1, textForSelect, false, false, (c -> true), callback);
    }

    @Override
    public void update() {
        if (this.duration == this.startDuration)
        {
            if ((hand.size() == 0) || (hand.stream().noneMatch(predicate)) || callback == null)
            {
                isDone = true;
                return;
            }

            if (hand.stream().filter(predicate).count() <= amount && !anyNumber && !canPickZero)
            {
                callback.accept(hand.stream().filter(predicate).collect(Collectors.toList()));
                AbstractDungeon.player.hand.refreshHandLayout();
                AbstractDungeon.player.hand.applyPowers();
                isDone = true;
                return;
            }

            tempHand.removeIf(predicate);
            if (tempHand.size() > 0) hand.removeIf(c -> tempHand.contains(c));

            AbstractDungeon.handCardSelectScreen.open(text, amount, anyNumber, canPickZero);
            tickDuration();
            return;
        }

        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved)
        {
            callback.accept(AbstractDungeon.handCardSelectScreen.selectedCards.group);
            hand.addAll(AbstractDungeon.handCardSelectScreen.selectedCards.group);
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            returnCards();
            isDone = true;
            return;
        }
        tickDuration();
    }

    private void returnCards()
    {
        if (tempHand.size() > 0) hand.addAll(tempHand);
        AbstractDungeon.player.hand.refreshHandLayout();
        AbstractDungeon.player.hand.applyPowers();
    }
}
