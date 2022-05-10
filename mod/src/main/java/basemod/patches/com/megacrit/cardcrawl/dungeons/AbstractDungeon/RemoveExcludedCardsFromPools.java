package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.NoCompendium;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.Arrays;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = "initializeCardPools"
)
public class RemoveExcludedCardsFromPools {

    public static void Postfix(AbstractDungeon __instance) {
        ArrayList<CardGroup> groups = new ArrayList<>(Arrays.asList(AbstractDungeon.srcColorlessCardPool, AbstractDungeon.srcCurseCardPool, AbstractDungeon.srcRareCardPool, AbstractDungeon.srcUncommonCardPool, AbstractDungeon.srcCommonCardPool, AbstractDungeon.colorlessCardPool, AbstractDungeon.curseCardPool, AbstractDungeon.rareCardPool, AbstractDungeon.uncommonCardPool, AbstractDungeon.commonCardPool));
        for (CardGroup group : groups) {
            ArrayList<AbstractCard> remove = new ArrayList<>();
            for (AbstractCard card : group.group) {
                if (card.getClass().isAnnotationPresent(NoPools.class)) {
                    remove.add(card);
                }
            }
            group.group.removeAll(remove);
        }
    }
}
