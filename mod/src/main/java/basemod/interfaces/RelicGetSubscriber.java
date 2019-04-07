package basemod.interfaces;

import com.megacrit.cardcrawl.relics.AbstractRelic;

@Deprecated
public interface RelicGetSubscriber extends ISubscriber {
	void receiveRelicGet(AbstractRelic r);
}
