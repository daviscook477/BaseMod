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
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;

import basemod.abstracts.CustomShopItem;

public class ShopGrid {

    public static final String DEFAULT_PAGE_ID = "basemod:";

    private static int pageIdCounter = 0;

    public static int defaultPageRows = 2;

    public static int defaultPageCols = 3;

    public static float rightEdge = Settings.WIDTH * 0.74F;

    public static float leftEdge = Settings.WIDTH * 0.44F;

    public static float topEdge = Settings.HEIGHT * 0.48F;
    
    public static float bottomEdge = Settings.HEIGHT * 0.075F;

    // this list holds pages specifically added by modders
    public static LinkedList<Page> customPages = new LinkedList<>();

    // this list has the pages for the remaining items that were added and the initial shop items
    public static LinkedList<Page> pages = new LinkedList<>();

    // used for when the shop grid is empty, will always have id of basemod:0 and cannot be removed
    public static final Page EMPTY_SHOP_PAGE = new Page();

    private static Page currentPage;

    public static NavButton leftArrow;

    public static NavButton rightArrow;

    private static float pageY;

    public static void initialize() {
        pages.clear();
        pageIdCounter = 1;
        currentPage = addDefaultPage();
        rightArrow = new NavButton(true);
        leftArrow = new NavButton(false);
    }

    public static Page getCurrentPage() {
        if (currentPage == null || (pages.isEmpty() && customPages.isEmpty()))
            return EMPTY_SHOP_PAGE;
        return currentPage;
    }

    public static void setCurrentPage(Page page) {
        if (page == null)
            currentPage = EMPTY_SHOP_PAGE;
        else
            currentPage = page;
    }

    public static Page addEmptyPage() {
        Page page = new Page();
        pages.addLast(page);
        return page;
    }

    public static Page addDefaultPage() {
        int[] rowSizes = new int[defaultPageRows];
            for (int i = 0; i < rowSizes.length; i++)
                rowSizes[i] = defaultPageCols;
        Page page = new Page(rowSizes);
        pages.addLast(page);
        return page;
    }

    public static Page addPage(int ... rowSizes) {
        Page page = new Page(rowSizes);
        pages.addLast(page);
        return page;
    }

    public static Page addCustomPage(String modId, int ... rowSizes) {
        Page page = new Page(modId, rowSizes);
        customPages.addLast(page);
        return page;
    }

    public static boolean removePage(Page page) {
        if (page == EMPTY_SHOP_PAGE)
            return false;
        if (page == currentPage) {
            currentPage = page.getNextPage();
            if (currentPage == page)
                currentPage = EMPTY_SHOP_PAGE;
        }
        if (pages.contains(page))
            return pages.remove(page);
        else if (customPages.contains(page))
            return customPages.remove(page);
        return false;
    }

    public static boolean removePage(int idx) {
        Page page = pages.get(idx);
        if (page != null)
            return removePage(page);
        return false;
    }

    public static boolean removePage(String pageId) {
        for (Page page : pages)
            if (page.id.equals(pageId))
                return removePage(page);
        for (Page page : customPages)
            if (page.id.equals(pageId))
                return removePage(page);
        return false;
    }

    public static boolean tryAddItem(CustomShopItem item) {
        for (Page page : pages)
            if (page.tryAddItem(item))
                return true;

        Page page = addDefaultPage();
        return page.tryAddItem(item);
    }

    public static boolean tryAddItemToCustomPage(String id, CustomShopItem item) {
        for (Page customPage : customPages)
            if (customPage.id.equals(id))
                return customPage.tryAddItem(item);
        return false;
    }

    public static float gridWidth() {
        return rightEdge - leftEdge;
    }

    public static float gridHeight() {
        return topEdge - bottomEdge;
    }

    public static void removeEmptyPages() {
        Page previousPage = null;
        if (currentPage != null)
            previousPage = currentPage.getPreviousPage();
        pages.removeIf((page) -> page.isEmpty());
        customPages.removeIf((page) -> page.isEmpty());
        if (!pages.contains(currentPage) && !customPages.contains(currentPage))
            currentPage = previousPage;
        else if (currentPage == previousPage && pages.size() == 0 && customPages.size() == 0)
            currentPage = EMPTY_SHOP_PAGE;
    }

    public static void hide() {
        if (currentPage != null)
            currentPage.hide();
        leftArrow.hide();
        rightArrow.hide();
    }

    public static void update(float rugY) {
        if (currentPage != null)
            currentPage.update(rugY);
    }

    public static void render(SpriteBatch sb, float rugY) {
        if (Settings.isDebug || Settings.isInfo) {
            sb.setColor(Color.RED);
            sb.draw(ImageMaster.DEBUG_HITBOX_IMG, leftEdge, rugY + bottomEdge, gridWidth(), gridHeight());
        }
        if (currentPage != null)
            currentPage.render(sb);
    }

    public static boolean isEmpty() {
        if (getCurrentPage() == EMPTY_SHOP_PAGE)
            return true;
        for (Page page : pages)
            if (!page.isEmpty())
                return false;
        for (Page page : customPages)
            if (!page.isEmpty())
                return false;
        return true;
    }

    public static class Page {

        public String id;

        public ArrayList<Row> rows = new ArrayList<Row>();

        public Page(int ... rowSizes) {
            for (int i = 0; i < rowSizes.length; i++) {
                rows.add(new Row(this, i, rowSizes[i]));
            }
            this.id = DEFAULT_PAGE_ID + pageIdCounter++;
        }

        public Page(String id, int ... rowSizes) {
            for (int i = 0; i < rowSizes.length; i++) {
                rows.add(new Row(this, i, rowSizes[i]));
            }
            this.id = id;
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
            pageY = rugY + 510.0F * Settings.yScale;
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
                    1130F * Settings.xScale,
                    pageY,
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

        public Row addRow() {
            return addRow(defaultPageCols);
        }

        public Row addRow(int size) {
            Row row = new Row(this, rows.size(), size);
            rows.add(row);
            return row;
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

        public boolean contains(CustomShopItem item) {
            for (Row row : rows)
                if (row.items.contains(item))
                    return true;
            return false;
        }

        public boolean contains (StoreRelic relic) {
            for (Row row : rows)
                for (CustomShopItem item : row.items)
                    if (relic == item.storeRelic)
                        return true;
            return false;
        }

        public boolean contains(StorePotion potion) {
            for (Row row : rows)
                for (CustomShopItem item : row.items)
                    if (potion == item.storePotion)
                        return true;
            return false;
        }

        public CustomShopItem getItem(StorePotion potion) {
            for (Row row : rows)
                for (CustomShopItem item : row.items)
                    if (potion == item.storePotion)
                        return item;
            return null;
        }

        public CustomShopItem getItem(StoreRelic relic) {
            for (Row row : rows)
                for (CustomShopItem item : row.items)
                    if (relic == item.storeRelic)
                        return item;
            return null;
        }
    }

    public static class Row {

        public Page owner;

        public ArrayList<CustomShopItem> items;

        public int rowNumber;

        public int maxColumns;

        public Row(Page owner, int rowNumber, int maxColumns) {
            this.owner = owner;
            this.items = new ArrayList<>();
            this.maxColumns = maxColumns;
            this.rowNumber = rowNumber;
        }

        public float getX(int col) {
            return leftEdge + (col + 1F) / (maxColumns + 1F) * gridWidth();
        }

        public float getY(int row, float rugY) {
            return rugY + (topEdge - ((row + 1F) / (owner.rows.size() + 1F) * gridHeight()));
        }

        @SuppressWarnings("unchecked")
        public boolean tryAddItem(CustomShopItem item) {
            if (items.size() < maxColumns) {
                item.row = rowNumber;
                item.col = items.size();
                item.gridRow = this;
                items.add(item);
                if (item.storeRelic != null) {
                    if (item.applyDiscounts) {
                        item.applyDiscounts(item.storeRelic.price);
                        item.storeRelic.price = item.price;
                    }
                    ArrayList<StoreRelic> relics = (ArrayList<StoreRelic>)ReflectionHacks.getPrivate(AbstractDungeon.shopScreen, ShopScreen.class, "relics");
                    if (!relics.contains(item.storeRelic))
                        relics.add(item.storeRelic);
                } else if (item.storePotion != null) {
                    if (item.applyDiscounts) {
                        item.applyDiscounts(item.storePotion.price);
                        item.storePotion.price = item.price;
                    }
                    ArrayList<StorePotion> potions = (ArrayList<StorePotion>)ReflectionHacks.getPrivate(AbstractDungeon.shopScreen, ShopScreen.class, "potions");
                    if (!potions.contains(item.storePotion))
                        potions.add(item.storePotion);
                }
                return true;
            }
            return false;
        }

        public void update(float rugY) {
            for (CustomShopItem item : items) {
                if (item.storePotion == null && item.storeRelic == null) {
                    item.update(rugY);
                }
            }
        }

        public void render(SpriteBatch sb) {
            if (AbstractDungeon.shopScreen != null)
                for (CustomShopItem item : items)
                    if (item.storePotion == null && item.storeRelic == null)
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
            this.x = (forward ? 1205.0F : 1055.0F) * Settings.xScale;
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
            int curIdx = pages.indexOf(currentPage);
            if (((!forward && curIdx > 0) || (forward && curIdx < pages.size() - 1))) {
                sb.setColor(Color.WHITE);
                TextureRegion region = new TextureRegion(texture);
                if (forward)
                    region.flip(true, false);
                sb.draw(region, x - 96.0F * Settings.scale, y - 86.0F * Settings.scale, 128.0F, 128.0F, 128.0F, 128.0F, Settings.scale / 2, Settings.scale / 2, 0.0F);
                hb.render(sb);
            }
        }

        public void hide() {
            this.hb.move(this.hb.x, Settings.HEIGHT + 200.0F * Settings.scale);
        }
    }
}
