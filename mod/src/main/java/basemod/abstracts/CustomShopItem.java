package basemod.abstracts;

import basemod.ReflectionHacks;
import basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen.ShopItemGrid;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;

public class CustomShopItem {

    private ShopScreen screenRef;
    public StoreRelic storeRelic;
    public StorePotion storePotion;

    private String tipTitle = null;
    private String tipBody = null;

    private Texture img;
    private Hitbox hb;
    private float x, y;

    public int price = 0;
    public int slot = 0;
    public int row;

    public boolean isPurchased = false;

    private static final float GOLD_IMG_WIDTH = ReflectionHacks.getPrivateStatic(StoreRelic.class, "GOLD_IMG_WIDTH");
    private static final float GOLD_OFFSET_X = ReflectionHacks.getPrivateStatic(StoreRelic.class, "RELIC_GOLD_OFFSET_X");
    private static final float GOLD_OFFSET_Y = ReflectionHacks.getPrivateStatic(StoreRelic.class, "RELIC_GOLD_OFFSET_Y");
    private static final float PRICE_OFFSET_X = ReflectionHacks.getPrivateStatic(StoreRelic.class, "RELIC_PRICE_OFFSET_X");
    private static final float PRICE_OFFSET_Y = ReflectionHacks.getPrivateStatic(StoreRelic.class, "RELIC_PRICE_OFFSET_Y");

    public CustomShopItem(StoreRelic storeRelic) {
        this.storeRelic = storeRelic;
        this.slot = ReflectionHacks.getPrivate(storeRelic, StoreRelic.class, "slot");
    }

    public CustomShopItem(StorePotion storePotion) {
        this.storePotion = storePotion;
        this.slot = ReflectionHacks.getPrivate(storePotion, StorePotion.class, "slot");
    }

    public CustomShopItem(ShopScreen screenRef, Texture img, int price) {
        this.slot = ShopItemGrid.getNextSlot();
        this.screenRef = screenRef;
        this.img = img;
        this.hb = new Hitbox(img.getWidth() * Settings.scale, img.getHeight() * Settings.scale);
        applyDiscounts(price);
    }

    public CustomShopItem(ShopScreen screenRef, Texture img, int price, String tipTitle, String tipBody) {
        this(screenRef, img, price);
        this.tipTitle = tipTitle;
        this.tipBody = tipBody;
    }

    public void applyDiscounts(int price) {
        this.price = (int)(price
            * (AbstractDungeon.player.hasRelic("The Courier") ? 0.8F : 1.0F)
            * (AbstractDungeon.player.hasRelic("Membership Card") ? 0.5F : 1.0F));
    }

    public void update(float rugY) {
        if (!this.isPurchased) {
            if (storeRelic != null && storeRelic.relic != null) {
                storeRelic.update(rugY);
                this.isPurchased = storeRelic.isPurchased;
                if (this.isPurchased) {
                    this.storeRelic.relic = null;
                    this.storeRelic = null;
                }
            } else if (storePotion != null && storePotion.potion != null) {
                storePotion.update(rugY);
                this.isPurchased = storePotion.isPurchased;
                if (this.isPurchased) {
                    this.storePotion.potion = null;
                    this.storePotion = null;
                }
            } else {
                this.x = 1000.0F * Settings.xScale + 150.0F * this.slot * Settings.xScale;
                this.y = rugY + (this.row == 0 ? 400.0F : 200.0F) * Settings.yScale;

                this.hb.move(this.x, this.y);
                this.hb.update();
                if (this.hb.hovered) {
                    this.screenRef.moveHand(this.x - 190.0F * Settings.scale, this.y - 70.0F * Settings.scale);
                    if (InputHelper.justClickedLeft)
                        this.hb.clickStarted = true;
                }
                if (this.hb.clicked || (this.hb.hovered && CInputActionSet.select.isJustPressed())) {
                    attemptPurchase();
                    this.hb.clicked = false;
                }
            }
            ShopItemGrid.removeEmptyPages();
        }
    }

    public void render(SpriteBatch sb) {
        if (!this.isPurchased) {
            if (storeRelic != null && storeRelic.relic != null)
                this.storeRelic.render(sb);
            else if (storePotion != null && storePotion.potion != null)
                this.storePotion.render(sb);
            else {
                sb.setColor(Color.WHITE);
                // assumes the size of a relic image
                sb.draw(img, x - 64.0F, y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 128, 128, false, false);
                sb.draw(ImageMaster.UI_GOLD, x + GOLD_OFFSET_X, y + GOLD_OFFSET_Y, GOLD_IMG_WIDTH, GOLD_IMG_WIDTH);
                Color color = Color.WHITE;
                if (this.price > AbstractDungeon.player.gold)
                    color = Color.SALMON;
                FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, Integer.toString(this.price), x + PRICE_OFFSET_X, y + PRICE_OFFSET_Y, color);
                if (this.hb.hovered && tipTitle != null && tipBody != null)
                    TipHelper.renderGenericTip(
                        InputHelper.mX + 50.0F * Settings.xScale,
                        InputHelper.mY + 50.0F * Settings.yScale,
                        tipTitle,
                        tipBody
                    );
            }
        }
    }

    public void hide() {
        if (!this.isPurchased) {
            if (storeRelic != null && storeRelic.relic != null) {
                this.storeRelic.hide();
                this.y = storeRelic.relic.currentY;
            }
            else if (storePotion != null && storePotion.potion != null) {
                this.storePotion.hide();
                this.y = storePotion.potion.posY;
            }
            else {
                this.y = Settings.HEIGHT + 200.0F * Settings.scale;
            }
        }
    }

    protected void attemptPurchase() {
        if (!this.isPurchased) {
            if (storeRelic != null && storeRelic.relic != null) {
                this.storeRelic.purchaseRelic();
                this.storeRelic.relic = null;
                this.storeRelic = null;
                this.isPurchased = true;
            } else if (storePotion != null && storePotion.potion != null) {
                this.storePotion.purchasePotion();
                this.storePotion.potion = null;
                this.storePotion = null;
                this.isPurchased = true;
            } else if (AbstractDungeon.player.gold >= this.price){
                purchase();
            } else {
                this.screenRef.playCantBuySfx();
                this.screenRef.createSpeech(ShopScreen.getCantBuyMsg());
            }
        }
    }

    public void purchase() {
        this.isPurchased = true;
        AbstractDungeon.player.loseGold(this.price);
        CardCrawlGame.sound.play("SHOP_PURCHASE", 0.1F);
    }
}
