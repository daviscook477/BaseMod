package basemod.interfaces;

import com.megacrit.cardcrawl.relics.AbstractRelic;

public interface RelicGetSubscriber extends ISubscriber {
	void receiveRelicGet(AbstractRelic r);
}
