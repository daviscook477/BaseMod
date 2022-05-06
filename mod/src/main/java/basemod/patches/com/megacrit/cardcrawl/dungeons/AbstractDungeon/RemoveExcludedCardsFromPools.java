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
                NoCompendium annotation = card.getClass().getAnnotation(NoCompendium.class);
                if (annotation != null && annotation.noPools()) {
                    remove.add(card);
                }
            }
            group.group.removeAll(remove);
        }
    }
}
