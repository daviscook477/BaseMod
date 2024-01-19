package basemod.devcommands.shop;

import basemod.ShopGrid;
import basemod.devcommands.ConsoleCommand;

public class ShopRemove extends ConsoleCommand {
    public ShopRemove() {
        requiresPlayer = true;
        minExtraTokens = 2;
        maxExtraTokens = 2;
        simpleCheck = true;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        int row = Integer.parseInt(tokens[0]);
        int col = Integer.parseInt(tokens[1]);
        ShopGrid.currentPage.rows.get(row).items.remove(col);
    }
}
