package basemod.abstracts;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.ShopGrid;
import basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen.ShopGridPatch;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Courier;
import com.megacrit.cardcrawl.relics.MembershipCard;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;

public class CustomShopItem {

    public String id;

    public ShopScreen screenRef = null;
    public ShopGrid.Row gridRow;
    public StoreRelic storeRelic = null;
    public StorePotion storePotion = null;

    public String tipTitle = null;
    public String tipBody = null;

    public Texture img = null;
    public Hitbox hb;
    public float x, y;
    public float scale;

    public int price = 0;
    public int row = 0;
    public int col = 0;

    public boolean isPurchased = false;

    public boolean applyDiscounts = true;

    private static final float GOLD_IMG_WIDTH = ReflectionHacks.getPrivateStatic(StoreRelic.class, "GOLD_IMG_WIDTH");
    private static final float GOLD_OFFSET_X = ReflectionHacks.getPrivateStatic(StoreRelic.class, "RELIC_GOLD_OFFSET_X");
    private static final float GOLD_OFFSET_Y = ReflectionHacks.getPrivateStatic(StoreRelic.class, "RELIC_GOLD_OFFSET_Y");
    private static final float PRICE_OFFSET_X = ReflectionHacks.getPrivateStatic(StoreRelic.class, "RELIC_PRICE_OFFSET_X");
    private static final float PRICE_OFFSET_Y = ReflectionHacks.getPrivateStatic(StoreRelic.class, "RELIC_PRICE_OFFSET_Y");

    public CustomShopItem(AbstractRelic relic) {
        this(new StoreRelic(relic, 0, AbstractDungeon.shopScreen));
    }

    public CustomShopItem(AbstractPotion potion) {
        this(new StorePotion(potion, 0, AbstractDungeon.shopScreen));
    }

    public CustomShopItem(StoreRelic storeRelic) {
        if (storeRelic.relic != null) {
            ShopGridPatch.StoreRelicPatches.compatibleWithGrid.set(storeRelic, true);
            this.storeRelic = storeRelic;
            this.price = storeRelic.price;
        } else {
            BaseMod.logger.error("StoreRelic cannot have a null relic");
        }
    }

    public CustomShopItem(StorePotion storePotion) {
        if (storePotion.potion != null) {
            ShopGridPatch.StorePotionPatches.compatibleWithGrid.set(storePotion, true);
            this.storePotion = storePotion;
            this.price = storePotion.price;
        } else {
            BaseMod.logger.error("StorePotion cannot have a null potion");
        }
    }

    public CustomShopItem(String modId, Texture img, int price, String tipTitle, String tipBody) {
        this(modId, AbstractDungeon.shopScreen, img, price, tipTitle, tipBody);
    }

    public CustomShopItem(String id, ShopScreen screenRef, Texture img, int price, String tipTitle, String tipBody) {
        this.id = id;
        this.screenRef = screenRef;
        this.tipTitle = tipTitle;
        this.tipBody = tipBody;
        this.img = img;
        this.hb = new Hitbox(72F * Settings.scale, 72F * Settings.scale);
        applyDiscounts(price);
    }

    public void applyDiscounts(int price) {
        float mult = 1F;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic.relicId.equals(Courier.ID))
                mult *= 0.8F;
            else if (relic.relicId.equals(MembershipCard.ID))
                mult *= 0.5F;
        }
        this.price = (int)(mult * price);
    }

    public void update(float rugY) {
        if (!this.isPurchased) {
            this.x = gridRow.getX(col);
            this.y = gridRow.getY(row, rugY);
            this.hb.move(this.x, this.y);
            this.hb.update();
            if (this.hb.hovered) {
                this.screenRef.moveHand(this.x - 190.0F * Settings.scale, this.y - 70.0F * Settings.scale);
                if (InputHelper.justClickedLeft)
                    this.hb.clickStarted = true;
                this.scale = Settings.scale * 1.25F;
            } else {
                this.scale = MathHelper.scaleLerpSnap(this.scale, Settings.scale);
            }
            if (this.hb.clicked || (this.hb.hovered && CInputActionSet.select.isJustPressed())) {
                attemptPurchase();
                this.hb.clicked = false;
            }
            ShopGrid.removeEmptyPages();
        }
    }

    public void render(SpriteBatch sb) {
        if (!this.isPurchased) {
            if (storeRelic == null && storePotion == null) {
                sb.setColor(Color.WHITE);
                hb.render(sb);
                // assumes the size of a relic image
                sb.draw(img, x - 64.0F, y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, scale, scale, 0.0F, 0, 0, 128, 128, false, false);
                if (gridRow != null && gridRow.maxColumns <= 5 && gridRow.owner.rows.size() <= 4) {
                    float goldY = y;
                    if (gridRow.owner.rows.size() > 3)
                        goldY += 26F * Settings.scale;
                    float textX = x;
                    if (gridRow.maxColumns > 4)
                        textX -= 10 * Settings.scale;
                    float textY = y;
                    if (gridRow.owner.rows.size() > 3)
                        textY += 22F * Settings.scale;

                    sb.draw(ImageMaster.UI_GOLD, x + GOLD_OFFSET_X, goldY + GOLD_OFFSET_Y, GOLD_IMG_WIDTH, GOLD_IMG_WIDTH);
                    Color color = Color.WHITE;
                    if (this.price > AbstractDungeon.player.gold)
                        color = Color.SALMON;
                    FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, Integer.toString(this.price), textX + PRICE_OFFSET_X, textY + PRICE_OFFSET_Y, color);
                    if (this.hb.hovered && tipTitle != null && tipBody != null)
                        TipHelper.renderGenericTip(
                            InputHelper.mX + 50.0F * Settings.xScale,
                            InputHelper.mY + 50.0F * Settings.xScale,
                            tipTitle,
                            tipBody
                        );
                } else if (hb.hovered) {
                    ShopGridPatch.x = x;
                    ShopGridPatch.y = y;
                    ShopGridPatch.price = price;
                    ShopGridPatch.hoveringItem = true;
                }
            }
        }
    }

    public void hide() {
        if (!this.isPurchased) {
            if (storeRelic != null && storeRelic.relic != null) {
                this.storeRelic.hide();
                this.y = storeRelic.relic.currentY;
            } else if (storePotion != null && storePotion.potion != null) {
                this.storePotion.hide();
                this.y = storePotion.potion.posY;
            } else {
                this.y = Settings.HEIGHT + 200.0F * Settings.scale;
            }
        }
    }

    protected void attemptPurchase() {
        if (!this.isPurchased && this.storePotion == null && this.storeRelic == null) {
            if (AbstractDungeon.player.gold >= this.price) {
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

    public boolean isValid() {
        return (storePotion != null && storePotion.potion != null)
            || (storeRelic != null && storeRelic.relic != null)
            || (screenRef != null && tipTitle != null && tipBody != null && img != null);
    }
}
