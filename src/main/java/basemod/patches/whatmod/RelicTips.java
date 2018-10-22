package basemod.patches.whatmod;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;

@SpirePatch(
    cls="com.megacrit.cardcrawl.relics.AbstractRelic",
    method="initializeTips"
)
public class RelicTips
{
    public static void Postfix(AbstractRelic __instance)
    {
        if (WhatMod.enabled) {
            if (__instance.tips.size() > 0 && __instance.tips.get(0).header.toLowerCase().equals(__instance.name.toLowerCase())) {
                String modName = WhatMod.findModName(__instance.getClass());
                if (modName != null) {
                    __instance.tips.get(0).body = FontHelper.colorString(modName, "p") + " NL " + __instance.tips.get(0).body;
                }
            }
        }
    }
}
