package basemod.devcommands.shop;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;

public class Shop extends ConsoleCommand {
    public Shop() {
        followup.put("add", ShopAdd.class);
        followup.put("remove", ShopRemove.class);
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        cmdShopHelp();
    }

    @Override
    public void errorMsg() {
        cmdShopHelp();
    }

    public static void cmdShopHelp() {
        DevConsole.couldNotParse();
        DevConsole.log("options are:");
        DevConsole.log("* add page [row size] [row size] ...");
        DevConsole.log("* add potion [id]");
        DevConsole.log("* add relic [id]");
        DevConsole.log("* add row [id] [id] ...");
        DevConsole.log("* remove item [row] [col]");
        DevConsole.log("* remove page [id]");
    }
}
