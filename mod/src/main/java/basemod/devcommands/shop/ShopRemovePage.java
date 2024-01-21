package basemod.devcommands.shop;

import basemod.DevConsole;
import basemod.ShopGrid;
import basemod.devcommands.ConsoleCommand;

public class ShopRemovePage extends ConsoleCommand {

    public ShopRemovePage() {
        requiresPlayer = true;
        minExtraTokens = 0;
        maxExtraTokens = 0;
        simpleCheck = true;
    }

    @Override
    public void errorMsg() {
        Shop.cmdShopHelp();
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        if (!ShopGrid.removePage(ShopGrid.currentPage))
            DevConsole.log("could not remove page");
    }
}
