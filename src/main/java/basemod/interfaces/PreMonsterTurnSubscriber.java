package basemod.interfaces;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface PreMonsterTurnSubscriber extends ISubscriber {
    boolean receivePreMonsterTurn(AbstractMonster m);
}