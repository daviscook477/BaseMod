package basemod.devcommands.shop;

import basemod.devcommands.ConsoleCommand;

public class ShopRemove extends ConsoleCommand {

    public ShopRemove() {
        followup.put("item", ShopRemoveItem.class);
        followup.put("page", ShopRemovePage.class);
    }

    @Override
    public void errorMsg() {
        Shop.cmdShopHelp();
    }

    @Override
    public void execute(String[] tokens, int depth) {
        Shop.cmdShopHelp();
    }
}
