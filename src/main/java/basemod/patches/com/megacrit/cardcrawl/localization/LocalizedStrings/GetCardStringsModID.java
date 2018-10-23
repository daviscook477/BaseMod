package basemod.patches.com.megacrit.cardcrawl.localization.LocalizedStrings;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.localization.LocalizedStrings;

@SpirePatch(
        cls="com.megacrit.cardcrawl.localization.LocalizedStrings",
        method="getCardStrings"
)
public class GetCardStringsModID
{
    public static void Prefix(LocalizedStrings __instance, @ByRef String[] cardName)
    {
        cardName[0] = BaseMod.convertToModID(cardName[0]);
    }
}
