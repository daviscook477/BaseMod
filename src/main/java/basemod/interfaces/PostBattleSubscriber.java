package basemod.interfaces;

import com.megacrit.cardcrawl.rooms.AbstractRoom;

public interface PostBattleSubscriber extends ISubscriber {
	void receivePostBattle(AbstractRoom battleRoom);
}
