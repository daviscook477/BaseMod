package basemod;

import java.util.ArrayList;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import basemod.abstracts.CustomShopItem;
import basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen.ShopGridPatch.StorePotionPatches;
import basemod.patches.com.megacrit.cardcrawl.shop.ShopScreen.ShopGridPatch.StoreRelicPatches;

public class ShopGrid {

    public static int defaultPageRows = 3;

    public static int defaultPageCols = 3;

    public static float rightEdge = Settings.WIDTH * 0.74F;

    public static float leftEdge = Settings.WIDTH * 0.44F;

    public static float topEdge = Settings.HEIGHT * 0.44F;
    
    public static float bottomEdge = Settings.HEIGHT * 0.075F;

    // this list holds pages specifically added by modders
    public static LinkedList<Page> customPages = new LinkedList<>();

    // this list has the pages for the remaining items that were added and the initial shop items
    public static LinkedList<Page> pages = new LinkedList<>();

    public static Page currentPage;

    public static NavButton leftArrow;

    public static NavButton rightArrow;

    private static float pageIdxY; // the top of the page?

    public static void initialize() {
        pages.add(currentPage = makeDefaultPage());
        rightArrow = new NavButton(true);
        leftArrow = new NavButton(false);
    }

    public static Page makeDefaultPage() {
        int[] rowSizes = new int[defaultPageRows];
            for (int i = 0; i < rowSizes.length; i++)
                rowSizes[i] = defaultPageCols;
        return new Page(rowSizes);
    }

    public static void addItem(CustomShopItem item) {
        for (Page page : pages)
            if (page.tryAddItem(item))
                return;

        Page page = makeDefaultPage();
        page.tryAddItem(item);
        pages.add(page);
    }

    public static float gridWidth() {
        return leftEdge - rightEdge;
    }

    public static float gridHeight() {
        return topEdge - bottomEdge;
    }

    public static void removeEmptyPages() {
        Page previousPage = currentPage.getPreviousPage();
        pages.removeIf((page) -> page.isEmpty());
        customPages.removeIf((page) -> page.isEmpty());
        if (!pages.contains(currentPage) && !customPages.contains(currentPage))
            currentPage = previousPage;
    }

    public static void hide() {
        currentPage.hide();
        leftArrow.hide();
        rightArrow.hide();
    }

    // public static void show() {
    //     currentPage.show();
    //     leftArrow.show();
    //     rightArrow.show();
    // }

    public static class Page {
        public ArrayList<Row> rows = new ArrayList<Row>();

        public Page(int ... rowSizes) {
            for (int i = 0; i < rowSizes.length; i++) {
                int size = rowSizes[i];
                Row row = new Row(0, size);
                row.owner = this;
                rows.add(new Row(0, size));
            }
        }

        public void hide() {
            for (Row row : rows)
                row.hide();
        }

        public void update(float rugY) {
            for (Row row : rows)
                row.update(rugY);
            leftArrow.update(rugY);
            rightArrow.update(rugY);
            pageIdxY = rugY + 500.0F * Settings.yScale; // <-- is this wrong?
        }

        public void render(SpriteBatch sb) {
            for (Row row : rows)
                if (!row.isEmpty())
                    row.render(sb);
            leftArrow.render(sb);
            rightArrow.render(sb);
            if (pages.size() > 1) {
                FontHelper.renderFontCentered(
                    sb,
                    FontHelper.buttonLabelFont,
                    (pages.indexOf(currentPage) + 1) + "/" + pages.size(),
                    1150.0F * Settings.xScale,
                    pageIdxY,
                    Color.WHITE
                );
            }
        }

        public boolean tryAddItem(CustomShopItem item) {
            for (Row row : rows) {
                if (!row.isFull()) {
                    row.tryAddItem(item);
                    return true;
                }
            }
            return false;
        }

        public boolean isFull() {
            for (Row row : rows)
                if (!row.isFull())
                    return false;
            return true;
        }

        public boolean isEmpty() {
            for (Row row : rows)
                if (!row.isEmpty())
                    return false;
            return true;
        }

        public Page getPreviousPage() {
            if (pages.contains(this)) {
                if (pages.getFirst() == this) {
                    if (customPages.isEmpty()) {
                        return pages.getLast();
                    }
                    return customPages.getLast();
                }
                return pages.get(pages.indexOf(this) - 1);
            } else if (customPages.contains(this)) {
                if (customPages.getFirst() == this) {
                    if (pages.isEmpty()) {
                        return customPages.getLast();
                    }
                    return pages.getLast();
                }
                return customPages.get(pages.indexOf(this) - 1);
            }
            return currentPage;
        }

        public Page getNextPage() {
            if (pages.contains(this)) {
                if (pages.getLast() == this) {
                    if (customPages.isEmpty()) {
                        return pages.getFirst();
                    }
                    return customPages.getFirst();
                }
                return pages.get(pages.indexOf(this) + 1);
            } else if (customPages.contains(this)) {
                if (customPages.getLast() == this) {
                    if (pages.isEmpty()) {
                        return customPages.getFirst();
                    }
                    return pages.getFirst();
                }
                return customPages.get(pages.indexOf(this) + 1);
            }
            return currentPage;
        }
    }

    public static class Row {

        public Page owner;

        public ArrayList<CustomShopItem> items;

        public int rowNumber;

        public int maxColumns;

        public Row(int rowNumber, int maxColumns) {
            this.items = new ArrayList<>();
            this.maxColumns = maxColumns;
            this.rowNumber = rowNumber;
        }

        public float getX(int col) {
            return leftEdge + (col + 1) / (maxColumns + 1) * gridWidth();
        }

        public float getY(int row, float rugY) {
            return rugY + bottomEdge + (row + 1) / owner.rows.size() * gridHeight();
        }

        public boolean tryAddItem(CustomShopItem item) {
            if (items.size() < maxColumns) {
                item.row = rowNumber;
                item.col = items.size();
                items.add(item);
                return true;
            }
            return false;
        }

        public void update(float rugY) {
            for (CustomShopItem item : items) {
                if (item.storePotion == null && item.storeRelic == null) {
                    item.update(rugY);
                } else if (!item.isPurchased) {
                    if (item.storePotion != null) {
                        StorePotionPatches.row.set(item.storePotion, rowNumber);
                        StorePotionPatches.col.set(item.storePotion, items.indexOf(item));
                    }
                    else if (item.storeRelic != null) {
                        StoreRelicPatches.row.set(item.storeRelic, rowNumber);
                        StoreRelicPatches.col.set(item.storeRelic, items.indexOf(item));
                    }
                }
            }
        }

        public void render(SpriteBatch sb) {
            if (AbstractDungeon.shopScreen != null)
                for (CustomShopItem item : items)
                    if (item.storePotion != null && item.storeRelic != null)
                        item.render(sb);
        }

        public void hide() {
            for (CustomShopItem item : items) {
                item.hide();
            }
        }

        public boolean isEmpty() {
            return items.isEmpty() || (items.stream().filter((item) -> !item.isPurchased).count() == 0);
        }

        public boolean isFull() {
            return items.size() == maxColumns;
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
