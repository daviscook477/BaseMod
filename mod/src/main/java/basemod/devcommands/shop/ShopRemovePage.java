package basemod.devcommands.shop;

import java.util.ArrayList;
import java.util.Comparator;

import basemod.DevConsole;
import basemod.ShopGrid;
import basemod.devcommands.ConsoleCommand;

public class ShopRemovePage extends ConsoleCommand {

    public ShopRemovePage() {
        requiresPlayer = true;
        minExtraTokens = 0;
        maxExtraTokens = 1;
        simpleCheck = true;
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> opts = new ArrayList<>();
        for (ShopGrid.Page page : ShopGrid.pages)
            opts.add(page.id);
        for (ShopGrid.Page page : ShopGrid.customPages)
            opts.add(page.id);
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
            if (!ShopGrid.removePage(ShopGrid.getCurrentPage()))
                DevConsole.log("could not remove current page");
        } else {
            if (!ShopGrid.removePage(tokens[3]))
                DevConsole.log("could not remove page with id " + tokens[3]);
        }
        ShopGrid.removeEmptyPages();
    }
}
