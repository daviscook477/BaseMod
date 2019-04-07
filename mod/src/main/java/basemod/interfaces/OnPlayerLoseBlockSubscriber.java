package basemod.interfaces;

@Deprecated
public interface OnPlayerLoseBlockSubscriber extends ISubscriber {
    int receiveOnPlayerLoseBlock(int amount);
}
