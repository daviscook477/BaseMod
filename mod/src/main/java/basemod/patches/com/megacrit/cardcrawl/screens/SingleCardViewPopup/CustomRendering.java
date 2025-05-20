package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import basemod.abstracts.CustomCard;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.RenderCardDescriptors;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import java.util.List;

import static basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.RenderCardDescriptors.getAllDescriptors;

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

            List<String> descriptors = getAllDescriptors(___card);

            CustomCard card = (CustomCard) ___card;
            TextureAtlas.AtlasRegion frame = card.frameLargeRegion,
                    frameMid = card.frameMiddleLargeRegion,
                    frameLeft = card.frameLeftLargeRegion,
                    frameRight = card.frameRightLargeRegion;

            if (frame == null) {
                if (descriptors.isEmpty()) {
                    return SpireReturn.Continue();
                }

                switch (card.rarity) {
                    case UNCOMMON:
                        if (frameMid == null) {
                            frameMid = ImageMaster.CARD_UNCOMMON_FRAME_MID_L;
                            frameLeft = ImageMaster.CARD_UNCOMMON_FRAME_LEFT_L;
                            frameRight = ImageMaster.CARD_UNCOMMON_FRAME_RIGHT_L;
                        }
                        switch (card.type) {
                            case ATTACK:
                                frame = ImageMaster.CARD_FRAME_ATTACK_UNCOMMON_L;
                                break;
                            case POWER:
                                frame = ImageMaster.CARD_FRAME_POWER_UNCOMMON_L;
                                break;
                            default:
                                frame = ImageMaster.CARD_FRAME_SKILL_UNCOMMON_L;
                                break;
                        }
                        break;
                    case RARE:
                        if (frameMid == null) {
                            frameMid = ImageMaster.CARD_RARE_FRAME_MID_L;
                            frameLeft = ImageMaster.CARD_RARE_FRAME_LEFT_L;
                            frameRight = ImageMaster.CARD_RARE_FRAME_RIGHT_L;
                        }
                        switch (card.type) {
                            case ATTACK:
                                frame = ImageMaster.CARD_FRAME_ATTACK_RARE_L;
                                break;
                            case POWER:
                                frame = ImageMaster.CARD_FRAME_POWER_RARE_L;
                                break;
                            default:
                                frame = ImageMaster.CARD_FRAME_SKILL_RARE_L;
                                break;
                        }
                        break;
                    default:
                        if (frameMid == null) {
                            frameMid = ImageMaster.CARD_COMMON_FRAME_MID_L;
                            frameLeft = ImageMaster.CARD_COMMON_FRAME_LEFT_L;
                            frameRight = ImageMaster.CARD_COMMON_FRAME_RIGHT_L;
                        }
                        switch (card.type) {
                            case ATTACK:
                                frame = ImageMaster.CARD_FRAME_ATTACK_COMMON_L;
                                break;
                            case POWER:
                                frame = ImageMaster.CARD_FRAME_POWER_COMMON_L;
                                break;
                            default:
                                frame = ImageMaster.CARD_FRAME_SKILL_COMMON_L;
                                break;
                        }
                        break;
                }
            }

            float tWidth = 0;
            float tOffset = 0;

            switch (card.type)
            {
                case ATTACK:
                    descriptors.add(0, AbstractCard.TEXT[0]);
                    tWidth = AbstractCard.typeWidthAttack;
                    tOffset = AbstractCard.typeOffsetAttack;
                    break;
                case SKILL:
                    descriptors.add(0, AbstractCard.TEXT[1]);
                    tWidth = AbstractCard.typeWidthSkill;
                    tOffset = AbstractCard.typeOffsetSkill;
                    break;
                case POWER:
                    descriptors.add(0, AbstractCard.TEXT[2]);
                    tWidth = AbstractCard.typeWidthPower;
                    tOffset = AbstractCard.typeOffsetPower;
                    break;
                case CURSE:
                    descriptors.add(0, AbstractCard.TEXT[3]);
                    tWidth = AbstractCard.typeWidthCurse;
                    tOffset = AbstractCard.typeOffsetCurse;
                    break;
                case STATUS:
                    descriptors.add(0, AbstractCard.TEXT[7]);
                    tWidth = AbstractCard.typeWidthStatus;
                    tOffset = AbstractCard.typeOffsetStatus;
                    break;
                default:
                    descriptors.add(0, AbstractCard.TEXT[5]);
                    break;
            }

            renderHelper(sb, (float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, frame);

            if (descriptors.size() > 1) {
                String text = String.join(RenderCardDescriptors.SEPARATOR, descriptors);
                GlyphLayout gl = new GlyphLayout();
                FontHelper.panelNameFont.getData().setScale(1f);
                gl.setText(FontHelper.panelNameFont, text);
                tOffset = (gl.width - 70 * Settings.scale) / 2f;
                tWidth = (gl.width - 0f) / (62 * Settings.scale);
            }

            if (tWidth > 1.1f && frameMid != null) {
                dynamicFrameRenderHelper(sb, frameMid, 0.0F, ___drawScale, tWidth);
                dynamicFrameRenderHelper(sb, frameLeft, -tOffset, ___drawScale, 1.0F);
                dynamicFrameRenderHelper(sb, frameRight, tOffset, ___drawScale, 1.0F);
            }

            return SpireReturn.Return(null);
        }
    }

    private static void renderHelper(SpriteBatch sb, float x, float y, TextureAtlas.AtlasRegion img) {
        if (img != null)
            sb.draw(img, x + img.offsetX - (float)img.originalWidth / 2.0F, y + img.offsetY - (float)img.originalHeight / 2.0F, (float)img.originalWidth / 2.0F - img.offsetX, (float)img.originalHeight / 2.0F - img.offsetY, (float)img.packedWidth, (float)img.packedHeight, Settings.scale, Settings.scale, 0.0F);
    }

    private static void dynamicFrameRenderHelper(SpriteBatch sb, TextureAtlas.AtlasRegion img, float xOffset, float drawScale, float xScale) {
        Vector2 tmp = new Vector2(0, 0);
        tmp.set(xOffset, 0);
        sb.draw(
                img,
                Settings.WIDTH / 2f + img.offsetX - (img.originalWidth / 2f + 2) + tmp.x,
                Settings.HEIGHT / 2f + img.offsetY - img.originalHeight / 2f + tmp.y,
                img.originalWidth / 2f - img.offsetX + 2,
                img.originalHeight / 2f - img.offsetY,
                img.packedWidth,
                img.packedHeight,
                Settings.scale * xScale,
                Settings.scale,
                0f
        );
    }
}
