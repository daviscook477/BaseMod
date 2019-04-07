package basemod.interfaces;

import com.megacrit.cardcrawl.cards.DamageInfo;

@Deprecated
public interface OnPlayerDamagedSubscriber extends ISubscriber {
    int receiveOnPlayerDamaged(int amount, DamageInfo info);
}
