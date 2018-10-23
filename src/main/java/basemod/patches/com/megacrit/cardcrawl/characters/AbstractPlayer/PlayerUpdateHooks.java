package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

@SpirePatch(cls = "com.megacrit.cardcrawl.characters.AbstractPlayer", method = "update")
public class PlayerUpdateHooks {
    public static void Prefix(AbstractPlayer __instance) {
        BaseMod.publishPrePlayerUpdate();
    }

    public static void Postfix(AbstractPlayer __instance) {
        BaseMod.publishPostPlayerUpdate();
    }
}
