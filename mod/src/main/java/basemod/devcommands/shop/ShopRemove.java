package basemod.devcommands.shop;

import java.util.ArrayList;
import java.util.Iterator;

import basemod.ShopGrid;
import basemod.abstracts.CustomShopItem;
import basemod.devcommands.ConsoleCommand;

public class ShopRemove extends ConsoleCommand {

    public ShopRemove() {
        requiresPlayer = true;
        minExtraTokens = 2;
        maxExtraTokens = 2;
        simpleCheck = true;
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        if (tokens.length == 2) {
            ArrayList<String> choices = new ArrayList<>();
            choices.add("item");
            choices.add("page");
        }

        ArrayList<String> results = new ArrayList<>();
        switch(tokens[2]) {
            case "item":
                if (tokens.length < 5)
                    results.add("col");
                if (tokens.length < 4)
                    results.add("row");
                return results;
        }
        return results;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        switch(tokens[2]) {
            case "item":
                int row = Integer.parseInt(tokens[3]);
                if (tokens.length == 4) {
                    Iterator<CustomShopItem> it = ShopGrid.currentPage.rows.get(row).items.iterator();
                    while (it.hasNext()) {
                        it.remove();
                    }
                } else if (tokens.length == 5) {
                    int col = Integer.parseInt(tokens[5]);
                    ShopGrid.currentPage.rows.get(row).items.remove(col);
                } else {
                    errorMsg();
                }
                break;
            case "page":
                ShopGrid.removePage(ShopGrid.currentPage);
                break;
        }
    }
}
