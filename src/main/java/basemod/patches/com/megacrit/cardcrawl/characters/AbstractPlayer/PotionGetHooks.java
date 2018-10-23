package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import basemod.BaseMod;
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
                BaseMod.publishPotionGet(potion);
            }
            return __result;
        }
    }
}
