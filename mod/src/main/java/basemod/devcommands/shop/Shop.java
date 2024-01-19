package basemod.devcommands.shop;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import basemod.devcommands.relic.Relic;

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
        Relic.cmdRelicHelp();
    }

    public static void cmdShopHelp() {
        DevConsole.couldNotParse();
        DevConsole.log("options are:");
        DevConsole.log("* remove [row] [col]");
        DevConsole.log("* add [relic/potion] [id]");
    }
}
