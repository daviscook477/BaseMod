package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.relics.Circlet;

@SpirePatch(
        cls="com.megacrit.cardcrawl.characters.AbstractPlayer",
        method="hasRelic"
)
public class HasRelicModID
{
    public static void Prefix(AbstractPlayer __instance, @ByRef String[] targetID)
    {
        if (!targetID[0].equals(Circlet.ID) && !BaseMod.hasModID(targetID[0])) {
            targetID[0] = BaseMod.convertToModID(targetID[0]);
        }
    }
}
