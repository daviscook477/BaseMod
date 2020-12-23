package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import basemod.interfaces.AlternateCardCostModifier;
import basemod.interfaces.XCostModifier;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.AlternateCardCosts;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.CardModifierPatches;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.List;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "useCard"
)
public class ModifyXCostPatch
{

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void Insert(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse) {
        if (c.cost == -1) {
            int effect = c.energyOnUse;
            // Alternate Costs to work with X costs logic
            if (!c.freeToPlayOnce) {
                ArrayList<AlternateCardCostModifier> list = AlternateCardCosts.modifiers(c);
                boolean replaceEnergy = false;
                int nonSplittable = 0;
                //start finding the highest non-splittable resource. Check for existence of any effects that replace energy.
                for (AlternateCardCostModifier mod : list) {
                    if (mod.costEffectActive(c) && !mod.canSplitCost(c) && mod.disableEnergyForX(c)) {
                        int tmp = compareLimit(mod.getAlternateResource(c), mod.setXCostLimit(c));
                        replaceEnergy = true;
                        if (tmp > nonSplittable) {
                            nonSplittable = tmp;
                        }
                    }
                }
                //find the combined total of all splittable resources that disable energy. If any exist, set effect to their total and disable energy spending.
                int splittableEnergyReplace = 0;
                for (AlternateCardCostModifier mod : list) {
                    if (mod.costEffectActive(c) && mod.canSplitCost(c) && mod.disableEnergyForX(c)) {
                        int tmp = compareLimit(mod.getAlternateResource(c), mod.setXCostLimit(c));
                        replaceEnergy = true;
                        if (tmp > -1) {
                            splittableEnergyReplace += tmp;
                        }
                    }
                }
                if (replaceEnergy) {
                    c.freeToPlayOnce = true;
                    effect = splittableEnergyReplace;
                }
                //finish finding the highest non-splittable resource.
                for (AlternateCardCostModifier mod : list) {
                    if (mod.costEffectActive(c) && !mod.canSplitCost(c) && !mod.disableEnergyForX(c)) {
                        int tmp = compareLimit(mod.getAlternateResource(c), mod.setXCostLimit(c));
                        if (tmp > nonSplittable) {
                            nonSplittable = tmp;
                        }
                    }
                }
                //finish finding the total of all splittable resources + energy (or energy replacements)
                int splittable = effect;
                for (AlternateCardCostModifier mod : list) {
                    if (mod.costEffectActive(c) && mod.canSplitCost(c) && !mod.disableEnergyForX(c)) {
                        int tmp = compareLimit(mod.getAlternateResource(c), mod.setXCostLimit(c));
                        if (tmp > -1) {
                            splittable += tmp;
                        }
                    }
                }
                //if any non splittable cost is greater than total splittable costs, find the first resource with that value and spend it. Disable energy spending.
                if (nonSplittable >= splittable) {
                    effect = nonSplittable;
                    for (AlternateCardCostModifier mod : list) {
                        if (!mod.canSplitCost(c) && mod.costEffectActive(c)) {
                            int resource = compareLimit(mod.getAlternateResource(c), mod.setXCostLimit(c));
                            if (resource == nonSplittable) {
                                mod.spendAlternateCost(c, nonSplittable);
                                break;
                            }
                        }
                    }
                    c.freeToPlayOnce = true;
                //otherwise, spend splittable resources until `splittable` is depleted.
                } else {
                    effect = splittable;
                    if (!replaceEnergy) {
                        splittable -= c.energyOnUse;
                    }
                    for (AlternateCardCostModifier mod : list) {
                        if (mod.canSplitCost(c) && mod.costEffectActive(c)) {
                            if (splittable > 0) {
                                int resource = compareLimit(mod.getAlternateResource(c), mod.setXCostLimit(c));
                                splittable -= resource;
                                mod.spendAlternateCost(c, resource);
                            }
                        }
                    }
                }
            }
            //now that we're finished pretending to manipulate energy for X costs, we can pretend to have chemical-x effects
            ArrayList<List> lists = new ArrayList<>();
            lists.add(AbstractDungeon.player.hand.group);
            lists.add(AbstractDungeon.player.drawPile.group);
            lists.add(AbstractDungeon.player.discardPile.group);
            lists.add(AbstractDungeon.player.powers);
            lists.add(AbstractDungeon.player.relics);
            lists.add(CardModifierPatches.CardModifierFields.cardModifiers.get(c));
            for (List list : lists) {
                for (Object item : list) {
                    if (item instanceof XCostModifier) {
                        XCostModifier mod = (XCostModifier)item;
                        if (mod.xCostModifierActive(c)) {
                            effect += mod.modifyX(c);
                        }
                    }
                }
            }
            c.energyOnUse = effect;
        }
    }

    private static int compareLimit(int resource, int limit) {
        if (limit != -1 && limit < resource) {
            return limit;
        }
        return resource;
    }

    public static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception
        {
            Matcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "use");
            return LineFinder.findInOrder(ctBehavior, matcher);
        }
    }
}
