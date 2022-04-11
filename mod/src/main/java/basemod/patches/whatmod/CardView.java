package basemod.patches.whatmod;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

                List<Class<?>> clses = new ArrayList<>();
                clses.add(card.getClass());
                CardModifierManager.modifiers(card).stream()
                        .map(AbstractCardModifier::getClass)
                        .forEach(clses::add);
                Class<?>[] arr = clses.toArray(new Class<?>[0]);
                WhatMod.renderModTooltipBottomLeft(sb, arr);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }
}
