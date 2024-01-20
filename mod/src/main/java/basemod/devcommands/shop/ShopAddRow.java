package basemod.devcommands.shop;

import java.util.ArrayList;
import java.util.Comparator;

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
            ShopGrid.Row row = ShopGrid.currentPage.addRow();
            while (row.tryAddItem(ShopAdd.randomItem()));
        } else {
            ArrayList<CustomShopItem> items = new ArrayList<CustomShopItem>();
            for (int i = 3; i < tokens.length; i++)
                items.add(ShopAdd.itemFromId(tokens[i]));
            ShopGrid.Row row = ShopGrid.currentPage.addRow(items.size());
            for (CustomShopItem item : items)
                row.tryAddItem(item);
        }
    }
}
