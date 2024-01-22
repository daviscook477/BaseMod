package basemod.devcommands.shop;

import java.util.ArrayList;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import basemod.DevConsole;
import basemod.ShopGrid;
import basemod.abstracts.CustomShopItem;
import basemod.devcommands.ConsoleCommand;

public class ShopAddRelic extends ConsoleCommand {

    public ShopAddRelic() {
        requiresPlayer = true;
        minExtraTokens = 0;
        maxExtraTokens = 1;
        simpleCheck = true;
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        return ConsoleCommand.getRelicOptions();
    }

    @Override
    public void errorMsg() {
        Shop.cmdShopHelp();
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        AbstractRelic relic;
        if (tokens.length == 3)
            relic = AbstractDungeon.returnRandomRelic(AbstractDungeon.returnRandomRelicTier()).makeCopy();
        else
            relic = RelicLibrary.getRelic(tokens[3]).makeCopy();
        if (!ShopGrid.tryAddItem(new CustomShopItem(relic)))
            DevConsole.log("could not add " + relic.relicId + " to shop grid");
    }
    
}
