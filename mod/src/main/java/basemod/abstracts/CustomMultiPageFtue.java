//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package basemod.abstracts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.ui.FtueTip;
import com.megacrit.cardcrawl.vfx.combat.BattleStartEffect;

public class CustomMultiPageFtue extends FtueTip {
    private static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString("Main Tutorial");
    private static final String[] LABEL = tutorialStrings.LABEL;
    private static final int W = 760;
    private static final int H = 580;
    private final Texture[] images;
    private final String[] messages;
    private final Color screen = Color.valueOf("1c262a00");
    private float x;
    private final float[] xs;
    private float targetX;
    private float startX;
    private float scrollTimer = 0.0F;
    private static final float SCROLL_TIME = 0.3F;
    private int currentSlot = 0;
    private final int lastSlot;

    public CustomMultiPageFtue(Texture[] images, String[] messages) {
        this.images = images;
        this.messages = messages;
        this.xs = new float[images.length];
        this.lastSlot = 1 - images.length;

        AbstractDungeon.player.releaseCard();
        if (AbstractDungeon.isScreenUp) {
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }

        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.screen = CurrentScreen.FTUE;
        AbstractDungeon.overlayMenu.showBlackScreen();
        this.x = 0.0F;
        for (int i = 0; i < this.xs.length; i++) {
            if (i == 0) {
                this.xs[i] = 567.0F * Settings.scale;
            } else {
                this.xs[i] = this.xs[i - 1] + Settings.WIDTH;
            }
        }
        AbstractDungeon.overlayMenu.proceedButton.show();
        if (this.currentSlot == this.lastSlot) {
            AbstractDungeon.overlayMenu.proceedButton.setLabel(LABEL[1]);
        } else {
            AbstractDungeon.overlayMenu.proceedButton.setLabel(LABEL[0]);
        }
    }

    public void update() {
        if (this.screen.a != 0.8F) {
            this.screen.a += Gdx.graphics.getDeltaTime();
            if (this.screen.a > 0.8F) {
                this.screen.a = 0.8F;
            }
        }

        if (AbstractDungeon.overlayMenu.proceedButton.isHovered && InputHelper.justClickedLeft || CInputActionSet.proceed.isJustPressed()) {
            CInputActionSet.proceed.unpress();
            if (this.currentSlot == this.lastSlot) {
                CardCrawlGame.sound.play("DECK_CLOSE");
                AbstractDungeon.closeCurrentScreen();
                AbstractDungeon.overlayMenu.proceedButton.hide();
                AbstractDungeon.effectList.clear();
                AbstractDungeon.topLevelEffects.add(new BattleStartEffect(false));
                return;
            }

            AbstractDungeon.overlayMenu.proceedButton.hideInstantly();
            AbstractDungeon.overlayMenu.proceedButton.show();
            CardCrawlGame.sound.play("DECK_CLOSE");
            --this.currentSlot;
            this.startX = this.x;
            this.targetX = (float)(this.currentSlot * Settings.WIDTH);
            this.scrollTimer = SCROLL_TIME;
            if (this.currentSlot == this.lastSlot) {
                AbstractDungeon.overlayMenu.proceedButton.setLabel(LABEL[1]);
            }
        }

        if (this.scrollTimer != 0.0F) {
            this.scrollTimer -= Gdx.graphics.getDeltaTime();
            if (this.scrollTimer < 0.0F) {
                this.scrollTimer = 0.0F;
            }
        }

        this.x = Interpolation.fade.apply(this.targetX, this.startX, this.scrollTimer / SCROLL_TIME);
    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.screen);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float)Settings.WIDTH, (float)Settings.HEIGHT);
        sb.setColor(Color.WHITE);
        for (int i = 0; i < this.images.length; i++) {
            sb.draw(this.images[i],
                    this.x + this.xs[i] - 380.0F,
                    Settings.HEIGHT / 2.0F - 290.0F,
                    380.0F,
                    290.0F,
                    W,
                    H,
                    Settings.scale,
                    Settings.scale,
                    0.0F,
                    0,
                    0,
                    W,
                    H,
                    false,
                    false);
        }
        float offsetY = 0.0F;
        if (Settings.BIG_TEXT_MODE) {
            offsetY = 110.0F * Settings.scale;
        }

        for (int i = 0; i < this.images.length; i++) {
            FontHelper.renderSmartText(
                    sb,
                    FontHelper.panelNameFont, messages[i],
                    this.x + this.xs[i] + 400.0F * Settings.scale,
                    (float) Settings.HEIGHT / 2.0F - FontHelper.getSmartHeight(
                            FontHelper.panelNameFont,
                            messages[i],
                            700.0F * Settings.scale,
                            40.0F * Settings.scale) / 2.0F + offsetY,
                    700.0F * Settings.scale,
                    40.0F * Settings.scale,
                    Settings.CREAM_COLOR);
        }

        FontHelper.renderFontCenteredWidth(
                sb,
                FontHelper.panelNameFont,
                LABEL[2],
                (float)Settings.WIDTH / 2.0F,
                (float)Settings.HEIGHT / 2.0F - 360.0F * Settings.scale,
                Settings.GOLD_COLOR);

        FontHelper.renderFontCenteredWidth(
                sb,
                FontHelper.tipBodyFont,
                LABEL[3] + Math.abs(this.currentSlot - 1) + LABEL[4].replaceAll("3", String.valueOf(this.images.length)),
                (float)Settings.WIDTH / 2.0F,
                (float)Settings.HEIGHT / 2.0F - 400.0F * Settings.scale,
                Settings.CREAM_COLOR);

        AbstractDungeon.overlayMenu.proceedButton.render(sb);
    }
}
