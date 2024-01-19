package basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;

import basemod.BaseMod;
import basemod.ShopGrid;
import basemod.abstracts.CustomShopItem;
import javassist.CtBehavior;

public class ShopGridPatch {

    @SpirePatch2(clz = ShopScreen.class, method = "init")
    public static class ShopScreenInit {

        @SpirePostfixPatch
        public static void PostShopInitializeHook() {
            BaseMod.publishPostShopInitialize();
        }

        @SpirePrefixPatch
        public static void InitializeGrid () {
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

        @SpirePostfixPatch
        public static void AddGridRelics(ArrayList<StoreRelic> ___relics) {
            for (StoreRelic relic : ___relics) {
                CustomShopItem item = new CustomShopItem();
                item.storeRelic = relic;
                ShopGrid.tryAddItem(item);
            }
        }
    }

    @SpirePatch2(clz = ShopScreen.class, method = "initPotions")
    public static class PostInitPotions {

        @SpirePostfixPatch
        public static void AddGridPotionsAndSetCoords(ArrayList<StorePotion> ___potions) {
            for (StorePotion potion : ___potions) {
                CustomShopItem item = new CustomShopItem();
                item.storePotion = potion;
                ShopGrid.tryAddItem(item);
            }

            for (ShopGrid.Row row : ShopGrid.currentPage.rows) {
                for (CustomShopItem item : row.items) {
                    if (item.storePotion != null) {
                        StorePotionPatches.row.set(item.storePotion, item.row);
                        StorePotionPatches.col.set(item.storePotion, item.col);
                    } else if (item.storeRelic != null) {
                        StoreRelicPatches.row.set(item.storeRelic, item.row);
                        StoreRelicPatches.col.set(item.storeRelic, item.col);
                    }
                }
            }
        }
    }

    @SpirePatch2(clz = ShopScreen.class, method = "updateRelics")
    public static class UpdateGridRelics {

        @SpirePrefixPatch
        public static void UpdateCurrentPage(float ___rugY) {
            ShopGrid.currentPage.update(___rugY);
        }
    }

    @SpirePatch2(clz = StoreRelic.class, method = SpirePatch.CLASS)
    public static class StoreRelicPatches {

        public static SpireField<Integer> row = new SpireField<>(() -> 0);
        public static SpireField<Integer> col = new SpireField<>(() -> 0);
        public static SpireField<ShopGrid.Row> gridRow = new SpireField<>(() -> null);

        @SpirePatch2(clz = StoreRelic.class, method = "update")
        public static class SetCoords {

            @SpireInsertPatch(locator = HBMoveLocator.class)
            public static void Insert(StoreRelic __instance, float rugY) {
                if (__instance.relic != null) {
                    ShopGrid.Row relicRow = gridRow.get(__instance);
                    __instance.relic.currentY = relicRow.getY(row.get(__instance), rugY);
                    __instance.relic.currentX = relicRow.getX(col.get(__instance));
                }
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
        public static class UpdateGridRow {
            public static void Postfix(StoreRelic __instance) {
                if (__instance.isPurchased) {
                    ShopGrid.Row gridRow = StoreRelicPatches.gridRow.get(__instance);
                    for (CustomShopItem item : gridRow.items) {
                        if (item.storeRelic == __instance) {
                            item.storeRelic.relic = null;
                            item.storeRelic = null;
                            item.isPurchased = true;
                            break;
                        }
                    }
                    ShopGrid.removeEmptyPages();
                }
            }
        }
    }

    @SpirePatch2(clz = StorePotion.class, method = SpirePatch.CLASS)
    public static class StorePotionPatches {

        public static SpireField<Integer> row = new SpireField<>(() -> 0);
        public static SpireField<Integer> col = new SpireField<>(() -> 0);
        public static SpireField<ShopGrid.Row> gridRow = new SpireField<>(() -> null);

        @SpirePatch2(clz = StorePotion.class, method = "update")
        public static class SetCoords {

            @SpireInsertPatch(locator = Locator.class)
            public static void Insert(StorePotion __instance, float rugY) {
                if (__instance.potion != null) {
                    ShopGrid.Row potionRow = gridRow.get(__instance);
                    __instance.potion.posY = potionRow.getY(row.get(__instance), rugY);
                    __instance.potion.posX = potionRow.getX(col.get(__instance));
                }
            }

            private static class Locator extends SpireInsertLocator {
                @Override
                public int[] Locate(CtBehavior ctb) throws Exception {
                    Matcher finalMatcher = new Matcher.MethodCallMatcher(Hitbox.class, "move");
                    return LineFinder.findInOrder(ctb, new ArrayList<Matcher>(), finalMatcher);
                }
            }
        }

        @SpirePatch2(clz = StorePotion.class, method = "purchasePotion")
        public static class UpdateGridRow {
            public static void Postfix(StorePotion __instance) {
                if (__instance.isPurchased) {
                    ShopGrid.Row potionRow = gridRow.get(__instance);
                    for (CustomShopItem item : potionRow.items) {
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
    }
}
