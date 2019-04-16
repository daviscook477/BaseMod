package basemod.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

@Deprecated
public interface PostDrawSubscriber extends ISubscriber {
    void receivePostDraw(AbstractCard c);
}