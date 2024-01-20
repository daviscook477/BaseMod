package basemod.devcommands.shop;

import java.util.ArrayList;
import java.util.List;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import basemod.abstracts.CustomShopItem;
import basemod.devcommands.ConsoleCommand;

public class ShopAdd extends ConsoleCommand {

    public ShopAdd() {
        followup.put("page", ShopAddPage.class);
        followup.put("potion", ShopAddPotion.class);
        followup.put("relic", ShopAddRelic.class);
        followup.put("row", ShopAddRow.class);
    }

    public static ArrayList<String> allPotions() {
        ArrayList<String> results = new ArrayList<>();
        List<String> allPotions = PotionHelper.getPotions(AbstractPlayer.PlayerClass.IRONCLAD, true);
        for (String key : allPotions) {
            results.add(key.replace(' ', '_'));
        }
        return results;
    }

    public static CustomShopItem randomItem() {
        if (AbstractDungeon.miscRng.randomBoolean())
            return new CustomShopItem(AbstractDungeon.returnRandomRelic(AbstractDungeon.returnRandomRelicTier()));
        else
            return new CustomShopItem(AbstractDungeon.returnRandomPotion());
    }

    public static CustomShopItem itemFromId(String id) {
        Object obj = RelicLibrary.getRelic(id);
        CustomShopItem item = null;
        if (obj != null)
            item = new CustomShopItem((AbstractRelic)obj);
        else if ((obj = PotionHelper.getPotion(id)) != null)
            item = new CustomShopItem((AbstractPotion)obj);
        return item;
    }

    @Override
    public void errorMsg() {
        Shop.cmdShopHelp();
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        Shop.cmdShopHelp();
    }
}
