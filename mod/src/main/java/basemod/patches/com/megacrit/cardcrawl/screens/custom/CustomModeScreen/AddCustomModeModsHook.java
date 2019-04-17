package basemod.patches.com.megacrit.cardcrawl.screens.custom.CustomModeScreen;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod6.BaseMod6;
import basemod6.events.AddCustomModsEvent;
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
        BaseMod6.EVENT_BUS.post(new AddCustomModsEvent(modList));
        BaseMod.publishAddCustomModeMods(modList);
    }
}
