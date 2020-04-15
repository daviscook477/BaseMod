package basemod.helpers;

public class PowerChecker {

    // For the player, use null in target and set isPlayer to true.
    public static boolean checkPower(AbstractCreature target, boolean isPlayer, AbstractPower power) {
        if (isPlayer) {
            return AbstractDungeon.player.hasPower(power.ID);
        } else {
            return target.hasPower(power.ID);
        }
    }

    public static int checkPowerAmount(AbstractCreature target, boolean isPlayer, AbstractPower power) {
        boolean PowerChecksum = checkPower(target, isPlayer, power);
        if(PowerChecksum) {
            if(isPlayer) { return AbstractDungeon.player.getPower(power.ID).amount; }
            else{ return target.getPower(power.ID).amount; }
        }
        return 0;
    }

}
