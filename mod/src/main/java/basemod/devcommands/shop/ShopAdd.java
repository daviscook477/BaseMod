package basemod.devcommands.shop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import basemod.DevConsole;
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

    private static ArrayList<String> allPotions() {
        ArrayList<String> results = new ArrayList<>();
        List<String> allPotions = PotionHelper.getPotions(AbstractPlayer.PlayerClass.IRONCLAD, true);
        for (String key : allPotions) {
            results.add(key.replace(' ', '_'));
        }
        return results;
    }

    private static CustomShopItem randomItem() {
        if (AbstractDungeon.miscRng.randomBoolean())
            return new CustomShopItem(AbstractDungeon.returnRandomRelic(AbstractDungeon.returnRandomRelicTier()));
        else
            return new CustomShopItem(AbstractDungeon.returnRandomPotion());
    }

    private static CustomShopItem itemFromId(String id) {
        Object obj = RelicLibrary.getRelic(id);
        CustomShopItem item = null;
        if (obj != null)
            item = new CustomShopItem((AbstractRelic)obj);
        else if ((obj = PotionHelper.getPotion(id)) != null)
            item = new CustomShopItem((AbstractPotion)obj);
        return item;
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        if (tokens.length == 2) {
            ArrayList<String> choices = new ArrayList<>();
            choices.add("page");
            choices.add("potion");
            choices.add("relic");
            choices.add("row");
            return choices;
        }

        ArrayList<String> results = new ArrayList<>();
        switch(tokens[2]) {
            case "page":
                results.add("3 3 3 ...");
                return results;
            case "potion":
                if (tokens.length > 3)
                    return results;
                return allPotions();
            case "relic":
                if (tokens.length > 3)
                    return results;
                return ConsoleCommand.getRelicOptions();
            case "row":
                results.addAll(allPotions());
                results.addAll(ConsoleCommand.getRelicOptions());
                results.sort(Comparator.comparing(String::toString));
                return results;
            default:
                return new ArrayList<>();
        }
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        switch(tokens[2]) {
            case "page":
                if (tokens.length == 3) {
                    ShopGrid.Page page = ShopGrid.addDefaultPage();
                    while (page.tryAddItem(randomItem()));
                    ShopGrid.currentPage = page;
                } else {
                    ShopGrid.Page page = ShopGrid.addEmptyPage();
                    for (int i = 3; i < tokens.length; i++)
                        page.addRow(Integer.parseInt(tokens[i]));
                    while (page.tryAddItem(randomItem()));
                    ShopGrid.currentPage = page;
                }
            case "row":
                if (tokens.length == 3) {
                    ShopGrid.Row row = ShopGrid.currentPage.addRow();
                    while (row.tryAddItem(randomItem()));
                } else {
                    ArrayList<CustomShopItem> items = new ArrayList<CustomShopItem>();
                    for (int i = 3; i < tokens.length; i++)
                        items.add(itemFromId(tokens[i]));
                    ShopGrid.Row row = ShopGrid.currentPage.addRow(items.size());
                    for (CustomShopItem item : items)
                        row.tryAddItem(item);
                }
                break;
            case "relic":
                if (tokens.length > 4) {
                    errorMsg();
                    return;
                }
                AbstractRelic relic;
                if (tokens.length < 4)
                    relic = AbstractDungeon.returnRandomRelic(AbstractDungeon.returnRandomRelicTier());
                else
                    relic = RelicLibrary.getRelic(tokens[3]);
                if (!ShopGrid.tryAddItem(new CustomShopItem(relic)))
                    DevConsole.log("could not add " + relic.relicId + " to shop grid");
                break;
            case "potion":
                if (tokens.length > 4) {
                    errorMsg();
                    return;
                }
                AbstractPotion potion;
                if (tokens.length < 4)
                    potion = AbstractDungeon.returnRandomPotion();
                else
                    potion = PotionHelper.getPotion(tokens[3]);
                if (!ShopGrid.tryAddItem(new CustomShopItem(potion)))
                    DevConsole.log("could not add " + potion.ID + " to shop grid");
                break;
            default:
                errorMsg();
        }
    }
}
