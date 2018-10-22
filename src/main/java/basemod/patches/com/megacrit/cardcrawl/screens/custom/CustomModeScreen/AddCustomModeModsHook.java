package basemod.patches.com.megacrit.cardcrawl.screens.custom.CustomModeScreen;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import com.megacrit.cardcrawl.screens.custom.CustomModeScreen;

import java.util.List;

@SpirePatch(
        clz = CustomModeScreen.class,
        method = "initializeMods"
)
public class AddCustomModeModsHook
{
    public static void Postfix(CustomModeScreen _instance) {
        List<CustomMod> modList = (List<CustomMod>) ReflectionHacks.getPrivate(_instance, CustomModeScreen.class, "modList");
        BaseMod.publishAddCustomModeMods(modList);
    }
}
