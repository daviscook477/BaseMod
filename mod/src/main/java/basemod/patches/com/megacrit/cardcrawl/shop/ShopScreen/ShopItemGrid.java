package basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomShopItem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;
import java.util.ArrayList;
import java.util.LinkedList;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class ShopItemGrid {
    public static LinkedList<ShopItemPage> pages = new LinkedList<>();
    public static ShopItemPage currentPage;
    public static NavButton leftArrow;
    public static NavButton rightArrow;
    private static float pageIdxY;

    @SpirePatch2(
        clz = ShopScreen.class,
        method = "init"
    )
    public static class InitPage {
        @SpirePostfixPatch
        public static void Postfix() {
            BaseMod.publishPostShopInitialize();
        }

        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert(ArrayList<StoreRelic> ___relics, ArrayList<StorePotion> ___potions) {
            ShopItemPage page = new ShopItemPage();
            page.row1 = ShopItemRow.makeDefaultRelicRow(___relics, 0);
            page.row2 = ShopItemRow.makeDefaultPotionRow(___potions, 1);
            pages.clear();
            pages.addLast(page);
            currentPage = page;
            rightArrow = new NavButton(true);
            leftArrow = new NavButton(false);
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctb) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(ShopScreen.class, "purgeAvailable");
                return LineFinder.findInOrder(ctb, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    @SpirePatch2(
        clz = ShopScreen.class,
        method = "open"
    )
    public static class HideOnOpen {
        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert() {
            currentPage.hide();
            leftArrow.hide();
            rightArrow.hide();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctb) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(ShopScreen.class, "rugY");
                return LineFinder.findInOrder(ctb, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    @SpirePatch2(
        clz = ShopScreen.class,
        method = "update"
    )
    public static class UpdateCurrentPage {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("updateRelics") || m.getMethodName().equals("updatePotions"))
                        m.replace("{}");
                }
            };
        }

        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert(float ___rugY) {
            currentPage.update(___rugY);
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctb) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ShopScreen.class, "updateRug");
                return LineFinder.findInOrder(ctb, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    @SpirePatch2(
        clz = ShopScreen.class,
        method = "render"
    )
    public static class RenderCurrentPage {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("renderRelics") || m.getMethodName().equals("renderPotions"))
                        m.replace("{}");
                }
            };
        }

        @SpireInsertPatch(
            locator = Locator.class
        )
        public static void Insert(SpriteBatch sb) {
            currentPage.render(sb);
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctb) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ShopScreen.class, "renderPurge");
                return LineFinder.findInOrder(ctb, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    public static void addItem(CustomShopItem item) {
        if (currentPage != null && currentPage.tryAddItem(item))
            return;
        ShopItemPage newPage = new ShopItemPage();
        newPage.tryAddItem(item);
        pages.addLast(newPage);
        if (currentPage == null)
            currentPage = newPage;
    }

    public static void removeEmptyPages() {
        ShopItemPage page = currentPage;
        if (page.isEmpty()) {
            int newIdx = pages.indexOf(page) - 1;
            pages.remove(page);
            currentPage = (newIdx == -1 ? null : pages.get(newIdx));
        }
    }

    public static class ShopItemPage {
        public ShopItemRow row1;
        public ShopItemRow row2;

        public ShopItemPage() {
            this.row1 = new ShopItemRow(0);
            this.row2 = new ShopItemRow(1);
        }

        public void hide() {
            row1.hide();
            row2.hide();
        }

        public void update(float rugY) {
            row1.update(rugY);
            row2.update(rugY);
            leftArrow.update(rugY);
            rightArrow.update(rugY);
            pageIdxY = rugY + 500.0F * Settings.yScale;
        }

        public void render(SpriteBatch sb) {
            row1.render(sb);
            row2.render(sb);
            leftArrow.render(sb);
            rightArrow.render(sb);
            if (pages.size() > 1)
                FontHelper.renderFontCentered(
                    sb,
                    FontHelper.buttonLabelFont,
                    (pages.indexOf(currentPage) + 1) + "/" + pages.size(),
                    1150.0F * Settings.xScale,
                    pageIdxY,
                    Color.WHITE
                );
        }

        public boolean tryAddItem(CustomShopItem item) {
            return row1.tryAddItem(item) || row2.tryAddItem(item);
        }

        public boolean isEmpty() {
            return row1.isEmpty() && row2.isEmpty();
        }
    }

    public static class ShopItemRow {
        public static final int MAX_ITEMS_PER_ROW = 3;

        public ArrayList<CustomShopItem> items;
        public int row;

        private boolean isDefaultRelics = false;
        private boolean isDefaultPotions = false;

        public ShopItemRow(int row) {
            this.items = new ArrayList<>();
            this.row = row;
        }

        public static ShopItemRow makeDefaultRelicRow(ArrayList<StoreRelic> relics, int row) {
            ShopItemRow itemRow = new ShopItemRow(row);
            itemRow.items = new ArrayList<>();
            itemRow.row = row;
            itemRow.isDefaultRelics = true;
            for (StoreRelic relic : relics) {
                itemRow.items.add(new CustomShopItem(relic));
            }
            return itemRow;
        }

        public static ShopItemRow makeDefaultPotionRow(ArrayList<StorePotion> potions, int row) {
            ShopItemRow itemRow = new ShopItemRow(row);
            itemRow.items = new ArrayList<>();
            itemRow.row = row;
            itemRow.isDefaultPotions = true;
            for (StorePotion potion : potions) {
                itemRow.items.add(new CustomShopItem(potion));
            }
            return itemRow;
        }

        public boolean tryAddItem(CustomShopItem item) {
            if (items.size() < MAX_ITEMS_PER_ROW) {
                item.row = this.row;
                item.slot = items.size();
                items.add(item);
                return true;
            }
            return false;
        }

        public void update(float rugY) {
            if (isDefaultRelics) {
                ReflectionHacks.privateMethod(ShopScreen.class, "updateRelics").invoke(AbstractDungeon.shopScreen);
                return;
            }
            if (isDefaultPotions) {
                ReflectionHacks.privateMethod(ShopScreen.class, "updatePotions").invoke(AbstractDungeon.shopScreen);
                return;
            }
            for (CustomShopItem item : items) {
                item.update(rugY);
            }
        }

        public void render(SpriteBatch sb) {
            if (isDefaultRelics && AbstractDungeon.shopScreen != null) {
                ReflectionHacks.privateMethod(ShopScreen.class, "renderRelics", SpriteBatch.class).invoke(AbstractDungeon.shopScreen, sb);
                return;
            }
            if (isDefaultPotions && AbstractDungeon.shopScreen != null) {
                ReflectionHacks.privateMethod(ShopScreen.class, "renderPotions", SpriteBatch.class).invoke(AbstractDungeon.shopScreen, sb);
                return;
            }
            if (AbstractDungeon.shopScreen != null)
                for (CustomShopItem item : items) {
                    item.render(sb);
                }
        }

        public void hide() {
            for (CustomShopItem item : items) {
                item.hide();
            }
        }

        public boolean isEmpty() {
            BaseMod.logger.info("Checking if row is empty: " + items.size());
            BaseMod.logger.info("Checking if row is empty: " + items.stream().filter(item -> !item.isPurchased).count());
            return (items.stream().filter(item -> !item.isPurchased).count() == 0);
        }
    }

    public static class NavButton {
        public static Texture texture = ImageMaster.POPUP_ARROW;
        public Hitbox hb;

        private float x, y;

        public boolean forward = true;

        public NavButton(boolean forward) {
            this.forward = forward;
            this.hb = new Hitbox(64.0F * Settings.scale, 64.0F * Settings.scale);
        }

        public void update(float rugY) {
            this.x = (forward ? 1225.0F : 1075.0F) * Settings.xScale;
            this.y = rugY + 500.0F * Settings.yScale;
            this.hb.move(x, y);
            this.hb.update();
            if (this.hb.hovered && InputHelper.justClickedLeft)
                hb.clickStarted = true;
            if (this.hb.clicked || (this.hb.hovered && CInputActionSet.select.isJustPressed())) {
                int curIdx = pages.indexOf(currentPage);
                if (forward && curIdx < pages.size() - 1)
                    currentPage = pages.get(curIdx + 1);
                else if (!forward && curIdx > 0)
                    currentPage = pages.get(curIdx - 1);
                this.hb.clicked = false;
            }
        }

        public void render(SpriteBatch sb) {
            sb.setColor(Color.WHITE);
            int curIdx = pages.indexOf(currentPage);
            if (!forward && curIdx > 0) {
                TextureRegion region = new TextureRegion(texture);
                sb.draw(region, x - 64.0F * Settings.scale, y - 64.0F * Settings.scale, 128.0F, 128.0F, 128.0F, 128.0F, Settings.scale / 2, Settings.scale / 2, 0.0F);
                hb.render(sb);
            }
            if (forward && curIdx < pages.size() - 1) {
                TextureRegion flippedRegion = new TextureRegion(texture);
                flippedRegion.flip(true, false);
                sb.draw(flippedRegion, x - 64.0F * Settings.scale, y - 64.0F * Settings.scale, 128.0F, 128.0F, 128.0F, 128.0F, Settings.scale / 2, Settings.scale / 2, 0.0F);
                hb.render(sb);
            }
        }

        public void hide() {
            this.hb.move(this.hb.x, Settings.HEIGHT + 200.0F * Settings.scale);
        }
    }
}
