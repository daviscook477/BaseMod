package basemod.interfaces;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

@Deprecated
public interface PostPowerApplySubscriber extends ISubscriber {
	void receivePostPowerApplySubscriber(AbstractPower p, AbstractCreature target, AbstractCreature source);
}
