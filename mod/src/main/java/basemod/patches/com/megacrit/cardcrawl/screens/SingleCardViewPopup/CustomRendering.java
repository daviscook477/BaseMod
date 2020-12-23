package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

public class CustomRendering {
    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "renderCardBanner"
    )
    public static class RenderBannerSwitch
    {
        public static SpireReturn<?> Prefix(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card, float ___drawScale)
        {
            //If it is not a custom card it cant possibly have the method getBannerLargeRegion, so use normal rendering
            if (!(___card instanceof CustomCard)) {
                return SpireReturn.Continue();
            }

            TextureAtlas.AtlasRegion region = ((CustomCard) ___card).getBannerLargeRegion();
            if (region == null) {
                return SpireReturn.Continue();
            }

            renderHelper(sb, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, region);

            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "renderFrame"
    )
    public static class RenderCustomFrame
    {
        public static SpireReturn<?> Prefix(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card, float ___drawScale)
        {
            //If it's not a CustomCard, no custom rendering
            if (!(___card instanceof CustomCard)) {
                return SpireReturn.Continue();
            }

            CustomCard card = (CustomCard) ___card;

            if (card.frameLargeRegion != null) //Does it have a custom frame?
            {
                renderHelper(sb, (float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, card.frameLargeRegion);

                if (card.frameMiddleLargeRegion != null) //Does it have dynamic frame parts?
                {
                    float tWidth = 0;
                    float tOffset = 0;

                    switch (card.type)
                    {
                        case ATTACK:
                            tWidth = AbstractCard.typeWidthAttack;
                            tOffset = AbstractCard.typeOffsetAttack;
                            break;
                        case SKILL:
                            tWidth = AbstractCard.typeWidthSkill;
                            tOffset = AbstractCard.typeOffsetSkill;
                            break;
                        case POWER:
                            tWidth = AbstractCard.typeWidthPower;
                            tOffset = AbstractCard.typeOffsetPower;
                            break;
                        case STATUS:
                            tWidth = AbstractCard.typeWidthStatus;
                            tOffset = AbstractCard.typeOffsetStatus;
                            break;
                        case CURSE:
                            tWidth = AbstractCard.typeWidthCurse;
                            tOffset = AbstractCard.typeOffsetCurse;
                            break;
                    }

                    if (tWidth > 1.1f)
                    {
                        dynamicFrameRenderHelper(sb, ImageMaster.CARD_COMMON_FRAME_MID, 0.0F, ___drawScale, tWidth);
                        dynamicFrameRenderHelper(sb, ImageMaster.CARD_COMMON_FRAME_LEFT, -tOffset, ___drawScale, 1.0F);
                        dynamicFrameRenderHelper(sb, ImageMaster.CARD_COMMON_FRAME_RIGHT, tOffset, ___drawScale, 1.0F);
                    }
                }
                return SpireReturn.Return(null);
            }

            return SpireReturn.Continue();
        }
    }

    private static void renderHelper(SpriteBatch sb, float x, float y, TextureAtlas.AtlasRegion img) {
        if (img != null)
            sb.draw(img, x + img.offsetX - (float)img.originalWidth / 2.0F, y + img.offsetY - (float)img.originalHeight / 2.0F, (float)img.originalWidth / 2.0F - img.offsetX, (float)img.originalHeight / 2.0F - img.offsetY, (float)img.packedWidth, (float)img.packedHeight, Settings.scale, Settings.scale, 0.0F);
    }

    private static void dynamicFrameRenderHelper(SpriteBatch sb, TextureAtlas.AtlasRegion img, float xOffset, float drawScale, float xScale) {
        if (img != null)
            sb.draw(img, (float)Settings.WIDTH / 2.0F + img.offsetX - (float)img.originalWidth / 2.0F + xOffset * drawScale, (float)Settings.HEIGHT / 2.0F + img.offsetY - (float)img.originalHeight / 2.0F, (float)img.originalWidth / 2.0F - img.offsetX, (float)img.originalHeight / 2.0F - img.offsetY, (float)img.packedWidth, (float)img.packedHeight, Settings.scale * xScale, Settings.scale, 0.0F);
    }
}
