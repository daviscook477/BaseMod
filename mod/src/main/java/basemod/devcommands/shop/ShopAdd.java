package basemod.devcommands.shop;

import java.util.ArrayList;
import java.util.List;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;

import basemod.ShopGrid;
import basemod.abstracts.CustomShopItem;
import basemod.devcommands.ConsoleCommand;

public class ShopAdd extends ConsoleCommand {
    public ShopAdd() {
        requiresPlayer = true;
        minExtraTokens = 2;
        maxExtraTokens = 2;
        simpleCheck = true;
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        String item = tokens[1];
        if (item.equals("relic"))
            return ConsoleCommand.getRelicOptions();

        ArrayList<String> result = new ArrayList<>();
        List<String> allPotions = PotionHelper.getPotions(AbstractPlayer.PlayerClass.IRONCLAD, true);
        for (String key : allPotions) {
            result.add(key.replace(' ', '_'));
        }
        return result;
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        String item = tokens[1];
        String id = tokens[2];

        if (item.equals("relic"))
            ShopGrid.tryAddItem(new CustomShopItem(RelicLibrary.getRelic(id)));
        else if (item.equals("potion"))
            ShopGrid.tryAddItem(new CustomShopItem(PotionHelper.getPotion(id)));
    }
}
