package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.abstracts.AbstractCardModifier;
import basemod.interfaces.AlternateCardCostModifier;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;

public class AlternateCardCosts {

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "useCard"
    )
    public static class CardModifierSpendResources
    {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(EnergyManager.class.getName()) && m.getMethodName().equals("use")) {
                        String manager = AlternateCardCosts.class.getName();
                        String energy = EnergyPanel.class.getName();
                        m.replace(
                                "if (" + manager + ".getPreEnergyResourceAmount(c) >= c.costForTurn) {" +
                                            manager + ".spendPreEnergyResource(c);" +
                                        "} else if (" + manager + ".getSplittableResourceAmount(c) >= c.costForTurn) {" +
                                            "int tmp = " + manager + ".spendPreEnergySplittableResource(c);" +
                                            "if (tmp > 0) {" +
                                                "if (tmp > " + energy + ".totalCount) {" +
                                                    "tmp -= " + energy + ".totalCount;" +
                                                    "this.energy.use(" + energy + ".totalCount);" +
                                                    manager + ".spendPostEnergySplittableResource(c, tmp);" +
                                                "} else {" +
                                                    "this.energy.use(tmp);" +
                                                "}" +
                                            "}" +
                                        "} else if (" + manager + ".getPostEnergyResourceAmount(c) >= c.costForTurn) {" +
                                            manager + ".spendPostEnergyResource(c);" +
                                        "} else {" +
                                            manager + ".spendPreEnergySplittableResource(c);" +
                                            "this.energy.use(" + energy + ".totalCount);" +
                                            manager + ".spendPostEnergySplittableResource(c, c.costForTurn);" +
                                        "}"
                        );
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "renderEnergy"
    )
    public static class GetCardModifierCostString
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"text", "costColor"}
        )
        public static void Insert(AbstractCard __instance, SpriteBatch sb, @ByRef String[] text, Color color) {
            text[0] = getCostString(__instance, text[0], color);
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "getEnergyFont");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }



    @SpirePatch(
            clz = AbstractCard.class,
            method = "hasEnoughEnergy"
    )
    public static class AlternateCostsHasEnoughAlternateResource
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<Boolean> Insert(AbstractCard __instance) {
            if (hasEnoughAlternateCost(__instance)) {
                return SpireReturn.Return(true);
            } else {
                return SpireReturn.Continue();
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "costForTurn");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    public static ArrayList<AlternateCardCostModifier> modifiers(AbstractCard c) {
        ArrayList<AlternateCardCostModifier> alternateCosts = new ArrayList<>();
        for (AbstractCardModifier mod : CardModifierPatches.CardModifierFields.cardModifiers.get(c)) {
            if (mod instanceof AlternateCardCostModifier) {
                alternateCosts.add((AlternateCardCostModifier)mod);
            }
        }
        if (AbstractDungeon.player != null) {
            for (AbstractPower power : AbstractDungeon.player.powers) {
                if (power instanceof AlternateCardCostModifier) {
                    alternateCosts.add((AlternateCardCostModifier) power);
                }
            }
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic instanceof AlternateCardCostModifier) {
                    alternateCosts.add((AlternateCardCostModifier) relic);
                }
            }
            for (AbstractCard card : AbstractDungeon.player.hand.group) {
                if (card instanceof AlternateCardCostModifier) {
                    alternateCosts.add((AlternateCardCostModifier) card);
                }
            }
            for (AbstractCard card : AbstractDungeon.player.drawPile.group) {
                if (card instanceof AlternateCardCostModifier) {
                    alternateCosts.add((AlternateCardCostModifier) card);
                }
            }
            for (AbstractCard card : AbstractDungeon.player.discardPile.group) {
                if (card instanceof AlternateCardCostModifier) {
                    alternateCosts.add((AlternateCardCostModifier) card);
                }
            }
            if (AbstractDungeon.player.cardInUse instanceof AlternateCardCostModifier) {
                alternateCosts.add((AlternateCardCostModifier) AbstractDungeon.player.cardInUse);
            }
        }
        return alternateCosts;
    }

    public static int getPreEnergyResourceAmount(AbstractCard card) {
        int tmp = 0;
        for (AlternateCardCostModifier mod : modifiers(card)) {
            if (mod.prioritizeAlternateCost(card) && !mod.canSplitCost(card) && mod.costEffectActive(card)) {
                tmp = Math.max(tmp, mod.getAlternateResource(card));
            }
        }
        return tmp;
    }

    public static int getPostEnergyResourceAmount(AbstractCard card) {
        int tmp = 0;
        for (AlternateCardCostModifier mod : modifiers(card)) {
            if (!mod.prioritizeAlternateCost(card) && !mod.canSplitCost(card) && mod.costEffectActive(card)) {
                tmp = Math.max(tmp, mod.getAlternateResource(card));
            }
        }
        return tmp;
    }

    public static int getSplittableResourceAmount(AbstractCard card) {
        int tmp = EnergyPanel.totalCount;
        for (AlternateCardCostModifier mod : modifiers(card)) {
            if (mod.canSplitCost(card) && mod.costEffectActive(card)) {
                int c = mod.getAlternateResource(card);
                if (c > -1) {
                    tmp += c;
                }
            }
        }
        return tmp;
    }

    public static void spendPreEnergyResource(AbstractCard card) {
        for (AlternateCardCostModifier mod : modifiers(card)) {
            if (mod.prioritizeAlternateCost(card) && mod.costEffectActive(card)) {
                int c = mod.getAlternateResource(card);
                if (c >= card.costForTurn) {
                    mod.spendAlternateCost(card, card.costForTurn);
                    return;
                }
            }
        }
    }

    public static void spendPostEnergyResource(AbstractCard card) {
        for (AlternateCardCostModifier mod : modifiers(card)) {
            if (!mod.prioritizeAlternateCost(card) && mod.costEffectActive(card)) {
                int c = mod.getAlternateResource(card);
                if (c >= card.costForTurn) {
                    mod.spendAlternateCost(card, card.costForTurn);
                    return;
                }
            }
        }
    }

    public static int spendPreEnergySplittableResource(AbstractCard card) {
        int remainingCost = card.costForTurn;
        for (AlternateCardCostModifier mod : modifiers(card)) {
            if (mod.prioritizeAlternateCost(card) && mod.canSplitCost(card) && mod.costEffectActive(card)) {
                remainingCost = mod.spendAlternateCost(card, remainingCost);
                if (remainingCost <= 0) {
                    break;
                }
            }
        }
        return remainingCost;
    }

    public static void spendPostEnergySplittableResource(AbstractCard card, int remainingCost) {
        for (AlternateCardCostModifier mod : modifiers(card)) {
            if (!mod.prioritizeAlternateCost(card) && mod.canSplitCost(card) && mod.costEffectActive(card)) {
                remainingCost = mod.spendAlternateCost(card, remainingCost);
                if (remainingCost <= 0) {
                    return;
                }
            }
        }
        System.out.println("CardModifierManager: WARNING: splittable resources spent for " + card + "without being sufficient!");
    }

    //the player is considered to have enough alternate cost when their energy + the total of alternate splittable resources >=
    // cost for turn, OR when any single non-splittable resource >= cost for turn.
    public static boolean hasEnoughAlternateCost(AbstractCard card) {
        ArrayList<AlternateCardCostModifier> splittableCosts = new ArrayList<>();
        ArrayList<AlternateCardCostModifier> nonSplittableCosts = new ArrayList<>();
        for (AlternateCardCostModifier mod : modifiers(card)) {
            if (mod.costEffectActive(card)) {
                if (mod.canSplitCost(card)) {
                    splittableCosts.add(mod);
                } else {
                    nonSplittableCosts.add(mod);
                }
            }
        }
        int amt = EnergyPanel.totalCount;
        for (AlternateCardCostModifier mod : splittableCosts) {
            int c = mod.getAlternateResource(card);
            if (c > -1) {
                amt += c;
            }
        }
        if (amt >= card.costForTurn) {
            return true;
        }
        for (AlternateCardCostModifier mod : nonSplittableCosts) {
            int c = mod.getAlternateResource(card);
            if (c > amt) {
                amt = c;
                if (amt >= card.costForTurn) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getCostString(AbstractCard card, String currentString, Color color) {
        for (AlternateCardCostModifier mod : modifiers(card)) {
            currentString = mod.replaceCostString(card, currentString, color);
        }
        return currentString;
    }
}
