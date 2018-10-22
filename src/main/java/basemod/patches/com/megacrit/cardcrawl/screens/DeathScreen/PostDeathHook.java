package basemod.patches.com.megacrit.cardcrawl.screens.DeathScreen;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(cls="com.megacrit.cardcrawl.screens.DeathScreen", method=SpirePatch.CONSTRUCTOR)
public class PostDeathHook {
    public static void Postfix(Object __obj_instance, Object __monster_group) {
        BaseMod.publishPostDeath();
    }
}
