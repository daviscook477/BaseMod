package basemod.devcommands.shop;

import java.util.ArrayList;
import basemod.DevConsole;
import basemod.ShopGrid;
import basemod.devcommands.ConsoleCommand;

public class ShopAddRow extends ConsoleCommand {

    public ShopAddRow() {
        requiresPlayer = true;
        minExtraTokens = 1;
        maxExtraTokens = 1;
        simpleCheck = true;
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> opts = new ArrayList<String>();
        for (int i = 0; i < 10; i++)
            opts.add(Integer.toString(i));
        return opts;
    }

    @Override
    public void errorMsg() {
        Shop.cmdShopHelp();
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        if (ShopGrid.getCurrentPage() != ShopGrid.EMPTY_SHOP_PAGE) {
            try {
                int size = Integer.parseInt(tokens[3]);
                ShopGrid.Row row = ShopGrid.getCurrentPage().addRow(size);
                while (row.tryAddItem(ShopAdd.randomItem()));
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                Shop.cmdShopHelp();
            }
        } else {
            DevConsole.log("no page to add a row to");
        }
    }
}
