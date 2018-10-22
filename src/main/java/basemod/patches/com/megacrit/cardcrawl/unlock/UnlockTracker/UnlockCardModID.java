package basemod.patches.com.megacrit.cardcrawl.unlock.UnlockTracker;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(
        cls="com.megacrit.cardcrawl.unlock.UnlockTracker",
        method="unlockCard"
)
public class UnlockCardModID
{
    public static void Prefix(@ByRef String[] key)
    {
        key[0] = BaseMod.convertToModID(key[0]);
    }
}
