package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.ui.FtueTip;

public class MultiCardPreviewRenderer {
    private static Float cardTipPad = null;

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "renderTips"
    )
    public static class RenderMultiCardPreviewInSingleViewPatch {

        public static void Postfix(SingleCardViewPopup __instance, SpriteBatch sb) {
            AbstractCard card = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "card");
            if (!card.isLocked && card.isSeen && !MultiCardPreview.multiCardPreview.get(card).isEmpty()) {
                if (cardTipPad == null) {
                    cardTipPad = ReflectionHacks.getPrivateStatic(AbstractCard.class, "CARD_TIP_PAD");
                }

                float horizontal = -((AbstractCard.IMG_WIDTH * 0.8F) + cardTipPad);
                boolean horizontalOnly = MultiCardPreview.horizontal.get(card);
                Hitbox prevHb = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "prevHb");
                float vertical = 0;
                if (!horizontalOnly) {
                    vertical = ((AbstractCard.IMG_HEIGHT * 0.8F) + cardTipPad);
                    if (prevHb != null) {
                        vertical += prevHb.height;
                    }
                }

                Vector2 position = new Vector2((1920F * Settings.scale) - (1435.0F * Settings.scale), 795.0F * Settings.scale);
                Vector2 offset = new Vector2(horizontal, vertical);
                boolean verticalNext = true;
                if (card.cardsToPreview != null) {
                    reposition(position, offset, horizontalOnly);
                    verticalNext = false;
                }
                for (AbstractCard toPreview : MultiCardPreview.multiCardPreview.get(card)) {
                    renderCard(sb, toPreview, 0.8f, position, offset, horizontalOnly || !verticalNext);
                    verticalNext = !verticalNext;
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "renderCardTip"
    )
    public static class RenderMultiCardPreviewPatch {

        public static void Postfix(AbstractCard __instance, SpriteBatch sb) {
            if ((!__instance.isLocked && __instance.isSeen && !Settings.hideCards && (boolean)ReflectionHacks.getPrivate(__instance, AbstractCard.class, "renderTip") || (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.FTUE) && (ReflectionHacks.getPrivate(AbstractDungeon.ftue, FtueTip.class, "c") == __instance)) && !MultiCardPreview.multiCardPreview.get(__instance).isEmpty()) {
                if (AbstractDungeon.player != null && AbstractDungeon.player.isDraggingCard) {
                    return;
                }
                boolean rightSide = __instance.current_x > Settings.WIDTH * 0.75F;
                if (cardTipPad == null) {
                    cardTipPad = ReflectionHacks.getPrivateStatic(AbstractCard.class, "CARD_TIP_PAD");
                }
                boolean horizontalOnly = MultiCardPreview.horizontal.get(__instance);
                float renderX = (((AbstractCard.IMG_WIDTH / 2.0F) + ((AbstractCard.IMG_WIDTH / 2.0F) * 0.8F) + (cardTipPad)) * __instance.drawScale);
                float horizontal = ((AbstractCard.IMG_WIDTH * 0.8F) + cardTipPad) * __instance.drawScale;
                if (!rightSide) {
                    horizontal *= -1;
                }
                float vertical = 0;
                if (!horizontalOnly) {
                    vertical = ((AbstractCard.IMG_HEIGHT * 0.8F) + cardTipPad) * __instance.drawScale;
                }
                if (rightSide) {
                    renderX = __instance.current_x + renderX;
                } else {
                    renderX = __instance.current_x - renderX;
                }
                float renderY = __instance.current_y + ((AbstractCard.IMG_HEIGHT / 2.0F) - (AbstractCard.IMG_HEIGHT / 2.0F * 0.8F)) * __instance.drawScale;
                Vector2 position = new Vector2(renderX, renderY);
                Vector2 offset = new Vector2(horizontal, vertical);

                boolean verticalNext = true;
                if (__instance.cardsToPreview != null) {
                    reposition(position, offset, horizontalOnly);
                    verticalNext = false;
                }
                for (AbstractCard toPreview : MultiCardPreview.multiCardPreview.get(__instance)) {
                    renderCard(sb, toPreview, __instance.drawScale * 0.8f, position, offset, horizontalOnly || !verticalNext);
                    verticalNext = !verticalNext;
                }
            }
        }
    }

    private static void renderCard(SpriteBatch sb, AbstractCard toPreview, float scale, Vector2 position, Vector2 offset, boolean horizontalNext) {
        toPreview.drawScale = scale;
        toPreview.current_x = position.x;
        toPreview.current_y = position.y;
        toPreview.render(sb);
        reposition(position, offset, horizontalNext);
    }

    private static void reposition(Vector2 position, Vector2 offset, boolean horizontalNext) {
        if (horizontalNext) {
            position.x += offset.x;
            position.y += offset.y;
        } else {
            position.y -= offset.y;
        }
    }
}
