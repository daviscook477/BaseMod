package basemod.patches.whatmod;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;

import java.lang.reflect.Field;

@SpirePatch(
    clz=SingleRelicViewPopup.class,
    method="renderTips"
)
public class RelicView
{
    public static void Postfix(SingleRelicViewPopup __instance, SpriteBatch sb)
    {
        if (WhatMod.enabled) {
            try {
                Field relicField = SingleRelicViewPopup.class.getDeclaredField("relic");
                relicField.setAccessible(true);
                AbstractRelic relic = (AbstractRelic) relicField.get(__instance);

                WhatMod.renderModTooltip(sb, relic.getClass());
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }
}
