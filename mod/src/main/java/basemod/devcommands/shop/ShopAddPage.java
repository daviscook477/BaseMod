package basemod.devcommands.shop;

import java.util.ArrayList;

import basemod.ShopGrid;
import basemod.devcommands.ConsoleCommand;

public class ShopAddPage extends ConsoleCommand {

    public ShopAddPage() {
        requiresPlayer = true;
        minExtraTokens = 0;
        maxExtraTokens = 9;
        simpleCheck = true;
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> opts = new ArrayList<String>();
        opts.add("3");
        return opts;
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        if (tokens.length == 3) {
            ShopGrid.Page page = ShopGrid.addDefaultPage();
            while (page.tryAddItem(ShopAdd.randomItem()));
            ShopGrid.currentPage = page;
        } else {
            ShopGrid.Page page = ShopGrid.addEmptyPage();
            try {
                for (int i = 3; i < tokens.length; i++)
                page.addRow(Integer.parseInt(tokens[i]));
                while (page.tryAddItem(ShopAdd.randomItem()));
                ShopGrid.currentPage = page;
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                Shop.cmdShopHelp();
            }
        }
    }
    
}
