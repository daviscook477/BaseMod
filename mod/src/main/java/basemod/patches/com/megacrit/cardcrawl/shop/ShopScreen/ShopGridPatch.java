package basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.ShopGrid;
import basemod.abstracts.CustomShopItem;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class ShopGridPatch {

    private static final float GOLD_IMG_WIDTH = ImageMaster.UI_GOLD.getWidth() * Settings.scale;

    private static final float SHADOW_DIST_X = 4F * Settings.scale;

    private static final float SHADOW_DIST_Y = 4F * Settings.scale;

    private static final float BOX_W = 100F * Settings.scale;

    private static final float OFFSET_X = -50F * Settings.scale;

    private static final float OFFSET_Y = -78F * Settings.scale;

    private static final float TEXT_OFFSET_X = -16F * Settings.scale;

    private static final float TEXT_OFFSET_Y = 16F * Settings.scale;

    private static final float RELIC_GOLD_OFFSET_X = -57F * Settings.scale;

    private static final float RELIC_GOLD_OFFSET_Y = -102F * Settings.scale;

    public static boolean hoveringItem = false;

    public static float x;

    public static float y;

    public static int price;

    public static void renderPrice(SpriteBatch sb) {
        final float BOX_EDGE_H = ReflectionHacks.getPrivateStatic(TipHelper.class, "BOX_EDGE_H");
        final float TIP_DESC_LINE_SPACING = ReflectionHacks.getPrivateStatic(TipHelper.class, "TIP_DESC_LINE_SPACING");
        Color color = ReflectionHacks.getPrivateStatic(TipHelper.class, "BASE_COLOR");
        float h = FontHelper.getHeight(FontHelper.tipBodyFont, Integer.toString(price), 1F);
        sb.setColor(Settings.TOP_PANEL_SHADOW_COLOR);
        sb.draw(ImageMaster.KEYWORD_TOP, x + OFFSET_X + SHADOW_DIST_X, y + OFFSET_Y - SHADOW_DIST_Y, BOX_W, BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BOT, x + OFFSET_X + SHADOW_DIST_X, y + OFFSET_Y - h - SHADOW_DIST_Y, BOX_W, BOX_EDGE_H + Math.min(h, 0F));
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.KEYWORD_TOP, x + OFFSET_X, y + OFFSET_Y, BOX_W, BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BOT, x + OFFSET_X, y + OFFSET_Y - h, BOX_W, BOX_EDGE_H);
        sb.draw(ImageMaster.UI_GOLD, x + RELIC_GOLD_OFFSET_X, y + RELIC_GOLD_OFFSET_Y, GOLD_IMG_WIDTH, GOLD_IMG_WIDTH);
        if (price > AbstractDungeon.player.gold)
            color = Color.SALMON;
        FontHelper.renderSmartText(sb, FontHelper.tipBodyFont, Integer.toString(price), x + TEXT_OFFSET_X + OFFSET_X + GOLD_IMG_WIDTH, y + TEXT_OFFSET_Y + OFFSET_Y, BOX_W, TIP_DESC_LINE_SPACING, color);
    }

    @SpirePatch2(clz = ShopScreen.class, method = SpirePatch.CLASS)
    public static class ShopScreenPatches {

        @SpirePatch2(clz = ShopScreen.class, method = "init")
        public static class ShopScreenInit {

            @SpirePostfixPatch
            public static void PostShopInitializeHook() {
                BaseMod.publishPostShopInitialize();
            }
        }

        @SpirePatch2(clz = ShopScreen.class, method = "open")
        public static class ShopScreenOpen {

            @SpirePostfixPatch
            public static void HideGridOnOpen() {
                if (!ShopGrid.getCurrentPage().isEmpty()) {
                    ShopGrid.hide();
                }
            }
        }

        @SpirePatch2(clz = ShopScreen.class, method = "initRelics")
        public static class InitRelics {

            @SpirePrefixPatch
            public static void InitializeGrid() {
                basemod.ShopGrid.initialize();
                BaseMod.publishPostGridInitialize();
            }

            @SpireInsertPatch(locator = ArrayAddLocator.class, localvars = { "relic" })
            public static void AddGridRelic(StoreRelic relic) {
                CustomShopItem item = new CustomShopItem(relic);
                item.applyDiscounts = false;
                if (!ShopGrid.tryAddItem(item))
                    BaseMod.logger.warn("not adding default shop relic because grid is full, is this intentional?");
            }

            private static class ArrayAddLocator extends SpireInsertLocator {

                @Override
                public int[] Locate(CtBehavior ct) throws Exception {
                    return LineFinder.findInOrder(ct, new Matcher.MethodCallMatcher(ArrayList.class, "add"));
                }
            }
        }

        @SpirePatch2(clz = ShopScreen.class, method = "initPotions")
        public static class PostInitPotions {

            @SpirePostfixPatch
            public static void ClearEmptyPagesAfterInit() {
                ShopGrid.removeEmptyPages();
                ShopGrid.removeEmptyRows();
            }

            @SpireInsertPatch(locator = ArrayAddLocator.class, localvars = { "potion" })
            public static void AddGridPotion(StorePotion potion) {
                CustomShopItem item = new CustomShopItem(potion);
                item.applyDiscounts = false;
                if (!ShopGrid.tryAddItem(item))
                    BaseMod.logger.warn("not adding default potion because grid is full, is this intentional?");
            }

            private static class ArrayAddLocator extends SpireInsertLocator {

                @Override
                public int[] Locate(CtBehavior ct) throws Exception {
                    return LineFinder.findInOrder(ct, new Matcher.MethodCallMatcher(ArrayList.class, "add"));
                }
            }
        }

        @SpirePatch2(clz = ShopScreen.class, method = "updateRelics")
        public static class Update {

            @SpirePrefixPatch
            public static void UpdateGrid(float ___rugY) {
                ShopGrid.update(___rugY);
            }
        }

        @SpirePatch2(clz = ShopScreen.class, method = "render")
        public static class Render {

            @SpirePrefixPatch
            public static void ResetHoveringItem() {
                hoveringItem = false;
            }

            @SpirePostfixPatch
            public static void RenderItemPrice(SpriteBatch sb) {
                if (hoveringItem)
                    renderPrice(sb);
            }

            @SpireInsertPatch(locator = RenderRelicsLocator.class)
            public static void RenderGrid(SpriteBatch sb, float ___rugY) {
                ShopGrid.render(sb, ___rugY);
            }

            private static class RenderRelicsLocator extends SpireInsertLocator {
                @Override
                public int[] Locate(CtBehavior ct) throws Exception {
                    return LineFinder.findInOrder(ct, new Matcher.MethodCallMatcher(ShopScreen.class, "renderRelics"));
                }
            }
        }
    }

    @SpirePatch2(clz = StoreRelic.class, method = SpirePatch.CLASS)
    public static class StoreRelicPatches {

        public static SpireField<Boolean> compatibleWithGrid = new SpireField<>(() -> false);

        @SpirePatch2(clz = StoreRelic.class, method = "update")
        public static class SetCoords {

            @SpireInsertPatch(locator = HBMoveLocator.class)
            public static SpireReturn<Void> Insert(StoreRelic __instance, float rugY) {
                if (__instance.relic != null && compatibleWithGrid.get(__instance)) {
                    for (ShopGrid.Row gridRow : ShopGrid.getCurrentPage().rows)
                        for (CustomShopItem item : gridRow.items)
                            if (item.storeRelic == __instance) {
                                __instance.relic.currentY = gridRow.getY(item.row, rugY);
                                __instance.relic.currentX = gridRow.getX(item.col);
                                return SpireReturn.Continue();
                            }
                    return SpireReturn.Return();
                }
                return SpireReturn.Continue();
            }

            private static class HBMoveLocator extends SpireInsertLocator {
                @Override
                public int[] Locate(CtBehavior ctb) throws Exception {
                    Matcher finalMatcher = new Matcher.MethodCallMatcher(Hitbox.class, "move");
                    return LineFinder.findInOrder(ctb, new ArrayList<Matcher>(), finalMatcher);
                }
            }
        }

        @SpirePatch2(clz = StoreRelic.class, method = "purchaseRelic")
        public static class PurchaseRelic {
            @SpirePostfixPatch
            public static void RemoveRelic(StoreRelic __instance) {
                if (__instance.isPurchased && compatibleWithGrid.get(__instance)) {
                    for (ShopGrid.Row gridRow : ShopGrid.getCurrentPage().rows)
                        for (CustomShopItem item : gridRow.items) {
                            if (item.storeRelic == __instance) {
                                item.isPurchased = true;
                                ShopGrid.removeEmptyPages();
                                return;
                            }
                        }
                    ShopGrid.removeEmptyPages();
                }
            }
        }

        @SpirePatch2(clz = StoreRelic.class, method = "render")
        public static class Render {

            @SpirePrefixPatch
            public static SpireReturn<Void> CheckIfRelicInCurrentPage(StoreRelic __instance, SpriteBatch sb) {
                if (__instance.relic != null && compatibleWithGrid.get(__instance) && !ShopGrid.getCurrentPage().contains(__instance)) {
                    return SpireReturn.Return();
                }
                return SpireReturn.Continue();
            }

            public static boolean canRender(StoreRelic instance) {
                if (compatibleWithGrid.get(instance) && ShopGrid.getCurrentPage().contains(instance)
                    && ((ShopGrid.getCurrentPage().getItem(instance).gridRow.maxColumns > 5)
                        || ShopGrid.getCurrentPage().rows.size() > 4))
                            return false;
                return true;
            }

            public static boolean canRenderGold(StoreRelic instance) {
                if (!canRender(instance)) {
                    if (instance.relic.hb.hovered) {
                        hoveringItem = true;
                        x = instance.relic.currentX;
                        y = instance.relic.currentY;
                        price = instance.price;
                    }
                    return false;
                }
                return true;
            }

            public static float goldY(StoreRelic instance, float yOffset) {
                if (compatibleWithGrid.get(instance) && ShopGrid.getCurrentPage().contains(instance) && ShopGrid.getCurrentPage().rows.size() > 3)
                    return instance.relic.currentY - 75F * Settings.yScale;
                return instance.relic.currentY + yOffset;
            }

            @SpireInstrumentPatch
            public static ExprEditor ChangeGoldPosition() {
                return new ExprEditor() {
                    public void edit(MethodCall m) throws CannotCompileException {
                        if (m.getMethodName().equals("draw")) {
                            m.replace(""
                                + "{"
                                    + "if (basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen.ShopGridPatch.StoreRelicPatches.Render.canRenderGold(this))"
                                        + "sb.draw($1, $2, basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen.ShopGridPatch.StoreRelicPatches.Render.goldY(this, RELIC_GOLD_OFFSET_Y), $4, $5);"
                                + "}"
                            );
                        }
                    }
                };
            }

            public static boolean canRenderText(SpriteBatch sb, StoreRelic instance) {
                return canRender(instance);
            }

            public static float textX(StoreRelic instance, float xOffset) {
                if (compatibleWithGrid.get(instance) && ShopGrid.getCurrentPage().contains(instance) && ShopGrid.getCurrentPage().getItem(instance).gridRow.maxColumns > 4)
                    return instance.relic.currentX + 3F * Settings.scale;
                return instance.relic.currentX + xOffset;
            }

            public static float textY(StoreRelic instance, float yOffset) {
                if (compatibleWithGrid.get(instance) && ShopGrid.getCurrentPage().contains(instance) && ShopGrid.getCurrentPage().rows.size() > 3)
                    return instance.relic.currentY - 40F * Settings.scale;
                return instance.relic.currentY + yOffset;
            }

            @SpireInstrumentPatch
            public static ExprEditor ChangeTextPosition() {
                return new ExprEditor() {
                    public void edit(MethodCall m) throws CannotCompileException {
                        if (m.getMethodName().equals("renderFontLeftTopAligned")) {
                            m.replace(""
                                + "{"
                                    + "if (basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen.ShopGridPatch.StoreRelicPatches.Render.canRenderText($1, this))"
                                        + "com.megacrit.cardcrawl.helpers.FontHelper.renderFontLeftTopAligned($1, $2, $3, "
                                        + "basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen.ShopGridPatch.StoreRelicPatches.Render.textX(this, RELIC_PRICE_OFFSET_X), "
                                        + "basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen.ShopGridPatch.StoreRelicPatches.Render.textY(this, RELIC_PRICE_OFFSET_Y), $6);"
                                + "}"
                            );
                        }
                    }
                };
            }
        }
    }

    @SpirePatch2(clz = StorePotion.class, method = SpirePatch.CLASS)
    public static class StorePotionPatches {

        public static SpireField<Boolean> compatibleWithGrid = new SpireField<>(() -> false);

        @SpirePatch2(clz = StorePotion.class, method = "update")
        public static class Update {

            @SpireInsertPatch(locator = HBMoveLocator.class)
            public static SpireReturn<Void> SetCoords(StorePotion __instance, float rugY) {
                if (__instance.potion != null && compatibleWithGrid.get(__instance)) {
                    for (ShopGrid.Row gridRow : ShopGrid.getCurrentPage().rows)
                        for (CustomShopItem item : gridRow.items) {
                            if (item.storePotion == __instance) {
                                if (gridRow.owner.rows.size() > 2) {
                                    __instance.potion.posY = gridRow.getY(item.row, rugY);
                                } else {
                                    __instance.potion.posY = rugY + (item.row == 0 ? 400F : 200F) * Settings.xScale;
                                }
                                __instance.potion.posX = gridRow.getX(item.col);
                                return SpireReturn.Continue();
                            }
                        }
                    return SpireReturn.Return();
                }
                return SpireReturn.Continue();
            }

            private static class HBMoveLocator extends SpireInsertLocator {
                @Override
                public int[] Locate(CtBehavior ctb) throws Exception {
                    Matcher finalMatcher = new Matcher.MethodCallMatcher(Hitbox.class, "move");
                    return LineFinder.findInOrder(ctb, new ArrayList<Matcher>(), finalMatcher);
                }
            }
        }

        @SpirePatch2(clz = StorePotion.class, method = "purchasePotion")
        public static class PurchasePotion {
            public static void Postfix(StorePotion __instance) {
                if (__instance.isPurchased && compatibleWithGrid.get(__instance)) {
                    for (ShopGrid.Row gridRow : ShopGrid.getCurrentPage().rows)
                        for (CustomShopItem item : gridRow.items) {
                            if (item.storePotion == __instance) {
                                item.isPurchased = true;
                                ShopGrid.removeEmptyPages();
                                break;
                            }
                        }
                    ShopGrid.removeEmptyPages();
                }
            }
        }

        @SpirePatch2(clz = StorePotion.class, method = "render")
        public static class Render {

            @SpirePrefixPatch
            public static SpireReturn<Void> CheckIfPotionInCurrentPage(StorePotion __instance, SpriteBatch sb) {
                if (__instance.potion != null && compatibleWithGrid.get(__instance) && !ShopGrid.getCurrentPage().contains(__instance)) {
                    return SpireReturn.Return();
                }
                return SpireReturn.Continue();
            }

            public static boolean canRender(StorePotion instance) {
                if (compatibleWithGrid.get(instance) && ShopGrid.getCurrentPage().contains(instance)
                    && ((ShopGrid.getCurrentPage().getItem(instance).gridRow.maxColumns > 5)
                        || ShopGrid.getCurrentPage().rows.size() > 4))
                            return false;
                return true;
            }

            public static boolean canRenderGold(StorePotion instance) {
                if (!canRender(instance)) {
                    if (instance.potion.hb.hovered) {
                        hoveringItem = true;
                        x = instance.potion.posX;
                        y = instance.potion.posY;
                        price = instance.price;
                    }
                    return false;
                }
                return true;
            }

            public static float goldY(StorePotion instance, float yOffset) {
                if (compatibleWithGrid.get(instance) && ShopGrid.getCurrentPage().contains(instance) && ShopGrid.getCurrentPage().rows.size() > 3)
                    return instance.potion.posY - 75F * Settings.yScale;
                return instance.potion.posY + yOffset;
            }

            @SpireInstrumentPatch
            public static ExprEditor ChangeGoldPosition() {
                return new ExprEditor() {
                    public void edit(MethodCall m) throws CannotCompileException {
                        if (m.getMethodName().equals("draw")) {
                            m.replace(""
                                + "{"
                                    + "if (basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen.ShopGridPatch.StorePotionPatches.Render.canRenderGold(this))"
                                        + "sb.draw($1, $2, basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen.ShopGridPatch.StorePotionPatches.Render.goldY(this, RELIC_GOLD_OFFSET_Y), $4, $5);"
                                + "}"
                            );
                        }
                    }
                };
            }

            public static boolean canRenderText(SpriteBatch sb, StorePotion instance) {
                return canRender(instance);
            }

            public static float textX(StorePotion instance, float xOffset) {
                if (compatibleWithGrid.get(instance) && ShopGrid.getCurrentPage().contains(instance) && ShopGrid.getCurrentPage().getItem(instance).gridRow.maxColumns > 4)
                    return instance.potion.posX + 3F * Settings.scale;
                return instance.potion.posX + xOffset;
            }

            public static float textY(StorePotion instance, float yOffset) {
                if (compatibleWithGrid.get(instance) && ShopGrid.getCurrentPage().contains(instance) && ShopGrid.getCurrentPage().rows.size() > 3)
                    return instance.potion.posY - 40F * Settings.scale;
                return instance.potion.posY + yOffset;
            }

            @SpireInstrumentPatch
            public static ExprEditor ChangeTextPosition() {
                return new ExprEditor() {
                    public void edit(MethodCall m) throws CannotCompileException {
                        if (m.getMethodName().equals("renderFontLeftTopAligned")) {
                            m.replace(""
                                + "{"
                                    + "if (basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen.ShopGridPatch.StorePotionPatches.Render.canRenderText($1, this))"
                                        + "com.megacrit.cardcrawl.helpers.FontHelper.renderFontLeftTopAligned($1, $2, $3, "
                                        + "basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen.ShopGridPatch.StorePotionPatches.Render.textX(this, RELIC_PRICE_OFFSET_X), "
                                        + "basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen.ShopGridPatch.StorePotionPatches.Render.textY(this, RELIC_PRICE_OFFSET_Y), $6);"
                                + "}"
                            );
                        }
                    }
                };
            }
        }
    }
}
