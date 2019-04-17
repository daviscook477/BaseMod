package basemod.interfaces;

import com.megacrit.cardcrawl.rooms.AbstractRoom;

@Deprecated
public interface PostBattleSubscriber extends ISubscriber {
	void receivePostBattle(AbstractRoom battleRoom);
}
