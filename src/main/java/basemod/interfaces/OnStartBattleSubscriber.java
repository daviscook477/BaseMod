package basemod.interfaces;

import com.megacrit.cardcrawl.rooms.MonsterRoom;

public interface OnStartBattleSubscriber extends ISubscriber {
    void receiveOnBattleStart(MonsterRoom monsterRoom);
}
