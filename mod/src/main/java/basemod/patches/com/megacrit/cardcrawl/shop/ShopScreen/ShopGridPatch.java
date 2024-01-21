package basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;

import basemod.BaseMod;
import basemod.ShopGrid;
import basemod.abstracts.CustomShopItem;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class ShopGridPatch {

    @SpirePatch2(clz = ShopScreen.class, method = SpirePatch.CLASS)
    public static class ShopScreenPatches {

        @SpirePatch2(clz = ShopScreen.class, method = "init")
        public static class ShopScreenInit {

            @SpirePostfixPatch
            public static void PostShopInitializeHook() {
                BaseMod.publishPostShopInitialize();
            }

            @SpirePrefixPatch
            public static void InitializeGrid(ShopScreen __instance) {
                basemod.ShopGrid.initialize();
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

            @SpireInsertPatch(locator = ArrayAddLocator.class, localvars = { "relic" })
            public static void AddGridRelic(StoreRelic relic) {
                if (!ShopGrid.tryAddItem(new CustomShopItem(relic)))
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

            @SpireInsertPatch(locator = ArrayAddLocator.class, localvars = { "potion" })
            public static void AddGridPotion(StorePotion potion) {
                if (!ShopGrid.tryAddItem(new CustomShopItem(potion)))
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

    public static class StoreRelicPatches {

        @SpirePatch2(clz = StoreRelic.class, method = "update")
        public static class SetCoords {

            @SpireInsertPatch(locator = HBMoveLocator.class)
            public static SpireReturn<Void> Insert(StoreRelic __instance, float rugY) {

                if (__instance.relic != null) { // dandy-TODO: for compatibility, add check to see if relic/potion was added to grid, otherwise continue
                    for (ShopGrid.Row gridRow : ShopGrid.getCurrentPage().rows)
                        for (CustomShopItem item : gridRow.items)
                            if (item.storeRelic == __instance) {
                                if (gridRow.owner.rows.size() > 2) {
                                    __instance.relic.currentY = gridRow.getY(item.row, rugY);
                                } else {
                                    __instance.relic.currentY = rugY + (item.row == 0 ? 400F : 200F) * Settings.xScale;
                                }
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
                if (__instance.isPurchased) {
                    for (ShopGrid.Row gridRow : ShopGrid.getCurrentPage().rows)
                        for (CustomShopItem item : gridRow.items) {
                            if (item.storeRelic == __instance) {
                                item.storeRelic.relic = null;
                                item.storeRelic = null;
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
                if (__instance.relic != null && !ShopGrid.getCurrentPage().contains(__instance)) {
                    return SpireReturn.Return();
                }
                return SpireReturn.Continue();
            }

            public static boolean canRender(StoreRelic instance) {
                if (ShopGrid.getCurrentPage().contains(instance)
                    && ((ShopGrid.getCurrentPage().getItem(instance).gridRow.maxColumns > 5)
                        || ShopGrid.getCurrentPage().rows.size() > 4))
                            return false;
                return true;
            }

            public static boolean canRenderGold(SpriteBatch sb, StoreRelic instance) {
                if (!canRender(instance)) {
                    if (instance.relic.hb.hovered) {
                         // render the cost above the tooltips
                    }
                    return false;
                }
                return true;
            }

            public static float goldY(StoreRelic instance, float yOffset) {
                if (ShopGrid.getCurrentPage().contains(instance) && ShopGrid.getCurrentPage().rows.size() > 2)
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
                                    + "if (basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen.ShopGridPatch.StoreRelicPatches.Render.canRenderGold($0, this))"
                                        + "sb.draw($1, $2, basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen.ShopGridPatch.StoreRelicPatches.Render.goldY(this, RELIC_GOLD_OFFSET_Y), $4, $5);"
                                + "}"
                            );
                        }
                    }
                };
            }

            public static boolean canRenderText(SpriteBatch sb, StoreRelic instance) {
                if (!canRender(instance)) {
                    if (instance.relic.hb.hovered) {
                        // render the text above the tooltips
                    }
                    return false;
                }
                return true;
            }

            public static float textX(StoreRelic instance, float xOffset) {
                if (ShopGrid.getCurrentPage().contains(instance) && ShopGrid.getCurrentPage().getItem(instance).gridRow.maxColumns > 4)
                    return instance.relic.currentX + 3F * Settings.scale;
                return instance.relic.currentX + xOffset;
            }

            public static float textY(StoreRelic instance, float yOffset) {
                if (ShopGrid.getCurrentPage().contains(instance) && ShopGrid.getCurrentPage().rows.size() > 2)
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

    public static class StorePotionPatches {

        @SpirePatch2(clz = StorePotion.class, method = "update")
        public static class Update {

            @SpireInsertPatch(locator = HBMoveLocator.class)
            public static SpireReturn<Void> SetCoords(StorePotion __instance, float rugY) {
                if (__instance.potion != null) { // dandy-TODO: for compatibility, add check to see if relic/potion was added to grid, otherwise continue
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
                if (__instance.isPurchased) {
                    for (ShopGrid.Row gridRow : ShopGrid.getCurrentPage().rows)
                        for (CustomShopItem item : gridRow.items) {
                            if (item.storePotion == __instance) {
                                item.storePotion.potion = null;
                                item.storePotion = null;
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
                if (__instance.potion != null && !ShopGrid.getCurrentPage().contains(__instance)) {
                    return SpireReturn.Return();
                }
                return SpireReturn.Continue();
            }

            public static boolean canRender(StorePotion instance) {
                if (ShopGrid.getCurrentPage().contains(instance)
                    && ((ShopGrid.getCurrentPage().getItem(instance).gridRow.maxColumns > 5)
                        || ShopGrid.getCurrentPage().rows.size() > 4))
                            return false;
                return true;
            }

            public static boolean canRenderGold(SpriteBatch sb, StorePotion instance) {
                if (!canRender(instance)) {
                    if (instance.potion.hb.hovered) {
                         // render the cost above the tooltips
                    }
                    return false;
                }
                return true;
            }

            public static float goldY(StorePotion instance, float yOffset) {
                if (ShopGrid.getCurrentPage().contains(instance) && ShopGrid.getCurrentPage().rows.size() > 2)
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
                                    + "if (basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen.ShopGridPatch.StorePotionPatches.Render.canRenderGold($0, this))"
                                        + "sb.draw($1, $2, basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen.ShopGridPatch.StorePotionPatches.Render.goldY(this, RELIC_GOLD_OFFSET_Y), $4, $5);"
                                + "}"
                            );
                        }
                    }
                };
            }

            public static boolean canRenderText(SpriteBatch sb, StorePotion instance) {
                if (!canRender(instance)) {
                    if (instance.potion.hb.hovered) {
                        // render the text above the tooltips
                    }
                    return false;
                }
                return true;
            }

            public static float textX(StorePotion instance, float xOffset) {
                if (ShopGrid.getCurrentPage().contains(instance) && ShopGrid.getCurrentPage().getItem(instance).gridRow.maxColumns > 4)
                    return instance.potion.posX + 3F * Settings.scale;
                return instance.potion.posX + xOffset;
            }

            public static float textY(StorePotion instance, float yOffset) {
                if (ShopGrid.getCurrentPage().contains(instance) && ShopGrid.getCurrentPage().rows.size() > 2)
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
