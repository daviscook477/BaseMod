package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.PotionGetEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.potions.AbstractPotion;

public class PotionGetHooks
{
    @SpirePatch(
            cls="com.megacrit.cardcrawl.characters.AbstractPlayer",
            method="obtainPotion",
            paramtypes={"int", "com.megacrit.cardcrawl.potions.AbstractPotion"}
    )
    public static class One
    {
        public static void Postfix(AbstractPlayer __instance, int slot, AbstractPotion potion)
        {
            BaseMod6.EVENT_BUS.post(new PotionGetEvent(potion));
            BaseMod.publishPotionGet(potion);
        }
    }

    @SpirePatch(
            cls="com.megacrit.cardcrawl.characters.AbstractPlayer",
            method="obtainPotion",
            paramtypes={"com.megacrit.cardcrawl.potions.AbstractPotion"}
    )
    public static class Two
    {
        public static boolean Postfix(boolean __result, AbstractPlayer __instance, AbstractPotion potion)
        {
            if (__result) {
                BaseMod6.EVENT_BUS.post(new PotionGetEvent(potion));
                BaseMod.publishPotionGet(potion);
            }
            return __result;
        }
    }
}
