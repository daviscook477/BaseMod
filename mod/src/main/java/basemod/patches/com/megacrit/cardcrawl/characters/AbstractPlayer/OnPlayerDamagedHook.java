package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.PlayerDamagedEvent;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import javassist.CtBehavior;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "damage",
        paramtypez = {DamageInfo.class}
)
public class OnPlayerDamagedHook {
    @SpireInsertPatch(
            localvars = {"damageAmount"},
            locator = OnPlayerDamagedHook.LocatorPre.class
    )
    public static void InsertPre(AbstractPlayer __instance, DamageInfo info, @ByRef int[] damageAmount) {
        BaseMod6.EVENT_BUS.post(new PlayerDamagedEvent(damageAmount));
        int damage = BaseMod.publishOnPlayerDamaged(damageAmount[0], info);
        if (damage < 0) {
            damage = 0;
        }
        damageAmount[0] = damage;
    }

    private static class LocatorPre extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "decrementBlock");
            return LineFinder.findInOrder(ctBehavior, matcher);
        }
    }
}
