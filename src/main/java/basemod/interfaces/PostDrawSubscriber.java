package basemod.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface PostDrawSubscriber extends ISubscriber {
    void receivePostDraw(AbstractCard c);
}