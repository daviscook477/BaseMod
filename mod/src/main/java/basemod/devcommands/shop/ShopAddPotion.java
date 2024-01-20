package basemod.devcommands.shop;

import java.util.ArrayList;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import basemod.DevConsole;
import basemod.ShopGrid;
import basemod.abstracts.CustomShopItem;
import basemod.devcommands.ConsoleCommand;

public class ShopAddPotion extends ConsoleCommand {
    
    public ShopAddPotion() {
        requiresPlayer = true;
        minExtraTokens = 0;
        minExtraTokens = 1;
        simpleCheck = true;
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        return ShopAdd.allPotions();
    }

    @Override
    public void errorMsg() {
        Shop.cmdShopHelp();
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        AbstractPotion potion;
        if (tokens.length < 4)
            potion = AbstractDungeon.returnRandomPotion();
        else
            potion = PotionHelper.getPotion(tokens[3]);
        if (!ShopGrid.tryAddItem(new CustomShopItem(potion)))
            DevConsole.log("could not add " + potion.ID + " to shop grid");
    }

}
