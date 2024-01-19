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

        @SpireInsertPatch(locator = Locator.class)
        public static void InitializeGrid () {
            basemod.ShopGrid.initialize();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ct) throws Exception {
                return LineFinder.findInOrder(ct, new Matcher.MethodCallMatcher(ShopScreen.class, "initRelics"));
            }
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
    public static class AddGridRelics {
        public static void Postfix(ArrayList<StoreRelic> ___relics) {
            for (StoreRelic relic : ___relics)
                ShopGrid.addItem(new CustomShopItem(relic));
        }
    }

    @SpirePatch2(clz = ShopScreen.class, method = "initPotions")
    public static class AddGridPotions {
        public static void Postfix(ArrayList<StorePotion> ___potions) {
            for (StorePotion potion : ___potions)
                ShopGrid.addItem(new CustomShopItem(potion));
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
                ShopGrid.Row gridRow = StoreRelicPatches.gridRow.get(__instance);
                __instance.relic.currentY = gridRow.getY(row.get(__instance), rugY);
                __instance.relic.currentX = gridRow.getX(col.get(__instance));
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
                        }
                    }
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
        public static class SetPotionYBasedOnRow {

            @SpireInsertPatch(locator = Locator.class)
            public static void Insert(StorePotion __instance, float rugY) {
                ShopGrid.Row gridRow = StorePotionPatches.gridRow.get(__instance);
                __instance.potion.posY = gridRow.getY(row.get(__instance), rugY);
                __instance.potion.posX = gridRow.getX(col.get(__instance));
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
                    ShopGrid.Row gridRow = StorePotionPatches.gridRow.get(__instance);
                    for (CustomShopItem item : gridRow.items) {
                        if (item.storePotion == __instance) {
                            item.storePotion.potion = null;
                            item.storePotion = null;
                            item.isPurchased = true;
                        }
                    }
                }
            }
        }
    }
}
