package basemod.patches.whatmod;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import java.lang.reflect.Field;

@SpirePatch(
    clz=SingleCardViewPopup.class,
    method="renderTips"
)
public class CardView
{
    public static void Postfix(SingleCardViewPopup __instance, SpriteBatch sb)
    {
        if (WhatMod.enabled) {
            try {
                Field cardField = SingleCardViewPopup.class.getDeclaredField("card");
                cardField.setAccessible(true);
                AbstractCard card = (AbstractCard) cardField.get(__instance);

                WhatMod.renderModTooltip(sb, card.getClass());
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }
}
