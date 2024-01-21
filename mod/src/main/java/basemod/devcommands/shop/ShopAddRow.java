package basemod.devcommands.shop;

import java.util.ArrayList;
import java.util.Comparator;

import basemod.DevConsole;
import basemod.ShopGrid;
import basemod.abstracts.CustomShopItem;
import basemod.devcommands.ConsoleCommand;

public class ShopAddRow extends ConsoleCommand {

    public ShopAddRow() {
        requiresPlayer = true;
        minExtraTokens = 0;
        minExtraTokens = 9;
        simpleCheck = true;
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> opts = new ArrayList<String>();
        opts.addAll(ShopAdd.allPotions());
        opts.addAll(ConsoleCommand.getRelicOptions());
        opts.sort(Comparator.comparing(String::toString));
        return opts;
    }

    @Override
    public void errorMsg() {
        Shop.cmdShopHelp();
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        if (tokens.length == 3) {
            if (ShopGrid.getCurrentPage() != ShopGrid.EMPTY_SHOP_PAGE) {
                ShopGrid.Row row = ShopGrid.getCurrentPage().addRow();
                while (row.tryAddItem(ShopAdd.randomItem()));
            } else {
                DevConsole.log("no page to add a row to");
            }
        } else {
            if (ShopGrid.getCurrentPage() != ShopGrid.EMPTY_SHOP_PAGE) {
                ArrayList<CustomShopItem> items = new ArrayList<CustomShopItem>();
                for (int i = 3; i < tokens.length; i++)
                    items.add(ShopAdd.itemFromId(tokens[i]));
                ShopGrid.Row row = ShopGrid.getCurrentPage().addRow(items.size());
                for (CustomShopItem item : items)
                    row.tryAddItem(item);
            } else {
                DevConsole.log("no page to add a row to");
            }
        }
    }
}
