package basemod.interfaces;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

@Deprecated
public interface PreMonsterTurnSubscriber extends ISubscriber {
    boolean receivePreMonsterTurn(AbstractMonster m);
}