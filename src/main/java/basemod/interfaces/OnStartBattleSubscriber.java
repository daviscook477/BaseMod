package basemod.interfaces;

import com.megacrit.cardcrawl.rooms.AbstractRoom;

public interface OnStartBattleSubscriber extends ISubscriber {
    void receiveOnBattleStart(AbstractRoom room);
}
