package basemod.devcommands.shop;

import java.util.ArrayList;
import java.util.Iterator;

import basemod.DevConsole;
import basemod.ShopGrid;
import basemod.abstracts.CustomShopItem;
import basemod.devcommands.ConsoleCommand;

public class ShopRemoveItem extends ConsoleCommand {
    
    public ShopRemoveItem() {
        requiresPlayer = true;
        minExtraTokens = 1;
        maxExtraTokens = 2;
        simpleCheck = true;
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> opts = new ArrayList<>();
        if (tokens.length <= 4) {
            if (ShopGrid.getCurrentPage() != ShopGrid.EMPTY_SHOP_PAGE) {
                for (int i = 0; i < ShopGrid.getCurrentPage().rows.size(); i++)
                    opts.add(String.valueOf(i));
            }
        }
        else {
            try {
                int row = Integer.parseInt(tokens[3]);
                for (int i = 0; i < ShopGrid.getCurrentPage().rows.get(row).maxColumns; i++)
                    opts.add(String.valueOf(i));
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
        return opts;
    }

    @Override
    public void errorMsg() {
        Shop.cmdShopHelp();
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        try {
            int row = Integer.parseInt(tokens[3]);
            if (tokens.length == 4) {
                Iterator<CustomShopItem> it = ShopGrid.getCurrentPage().rows.get(row).items.iterator();
                while (it.hasNext()) {
                    it.next();
                    it.remove();
                }
            } else {
                int col = Integer.parseInt(tokens[4]);
                ShopGrid.getCurrentPage().rows.get(row).items.remove(col);
            }
            ShopGrid.removeEmptyPages();
        } catch(NumberFormatException nfe) {
            nfe.printStackTrace();
            Shop.cmdShopHelp();
        } catch (IndexOutOfBoundsException iobe) {
            iobe.printStackTrace();
            DevConsole.log("row could not be found");
        }
    }

}
