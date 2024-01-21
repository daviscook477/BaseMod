package basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
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
import javassist.CtBehavior;

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
                if (!ShopGrid.currentPage.isEmpty()) {
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
                    for (ShopGrid.Row gridRow : ShopGrid.currentPage.rows)
                        for (CustomShopItem item : gridRow.items)
                            if (item.storeRelic == __instance) {
                                if (gridRow.owner.rows.size() != 2) {
                                    __instance.relic.currentY = gridRow.getY(item.row, rugY);
                                } else {
                                    __instance.relic.currentY = rugY + (item.row == 0 ? 200F : 400F) * Settings.xScale;
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
                    for (ShopGrid.Row gridRow : ShopGrid.currentPage.rows)
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
                if (__instance.relic != null && !ShopGrid.currentPage.contains(__instance)) {
                    return SpireReturn.Return();
                }
                return SpireReturn.Continue();
            }
        }
    }

    public static class StorePotionPatches {

        @SpirePatch2(clz = StorePotion.class, method = "update")
        public static class Update {

            @SpireInsertPatch(locator = HBMoveLocator.class)
            public static SpireReturn<Void> SetCoords(StorePotion __instance, float rugY) {
                if (__instance.potion != null) { // dandy-TODO: for compatibility, add check to see if relic/potion was added to grid, otherwise continue
                    for (ShopGrid.Row gridRow : ShopGrid.currentPage.rows)
                        for (CustomShopItem item : gridRow.items) {
                            if (item.storePotion == __instance) {
                                if (gridRow.owner.rows.size() != 2) {
                                    __instance.potion.posY = gridRow.getY(item.row, rugY);
                                } else {
                                    __instance.potion.posY = rugY + (item.row == 0 ? 200F : 400F) * Settings.xScale;
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
                    for (ShopGrid.Row gridRow : ShopGrid.currentPage.rows)
                        for (CustomShopItem item : gridRow.items) {
                            if (item.storePotion == __instance) {
                                item.storePotion.potion = null;
                                item.storePotion = null;
                                item.isPurchased = true;
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
                if (__instance.potion != null && !ShopGrid.currentPage.contains(__instance)) {
                    return SpireReturn.Return();
                }
                return SpireReturn.Continue();
            }
        }
    }
}
