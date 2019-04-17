package basemod.interfaces;

import com.megacrit.cardcrawl.rooms.AbstractRoom;

@Deprecated
public interface OnStartBattleSubscriber extends ISubscriber {
    void receiveOnBattleStart(AbstractRoom room);
}
