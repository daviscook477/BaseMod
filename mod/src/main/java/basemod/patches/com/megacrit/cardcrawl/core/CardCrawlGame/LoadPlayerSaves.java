package basemod.patches.com.megacrit.cardcrawl.core.CardCrawlGame;

import java.util.ArrayList;
import java.util.Map;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.CardModifierPatches;
import basemod.BaseMod;
import basemod.abstracts.CustomSavableRaw;
import basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer.SeenEvents;
import basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveFile.ModSaves;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.Map;

@SpirePatch(clz=CardCrawlGame.class, method="loadPlayerSave")
public class LoadPlayerSaves
{
    public static void Postfix(CardCrawlGame __instance, AbstractPlayer p)
    {
        // Cards
        ModSaves.ArrayListOfJsonElement modCardSaves = ModSaves.modCardSaves.get(CardCrawlGame.saveFile);
        int i = 0;
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (card instanceof CustomSavableRaw) {
                ((CustomSavableRaw)card).onLoadRaw(modCardSaves == null || i >= modCardSaves.size() ? null : modCardSaves.get(i));
            }
            i++;
        }

        //Master deck AbstractCardModifiers
        GsonBuilder builder = new GsonBuilder();
        if (CardModifierPatches.modifierAdapter == null) {
            CardModifierPatches.initializeAdapterFactory();
        }
        builder.registerTypeAdapterFactory(CardModifierPatches.modifierAdapter);
        Gson gson = builder.create();
        ModSaves.ArrayListOfJsonElement cardModifierSaves = ModSaves.cardModifierSaves.get(CardCrawlGame.saveFile);
        i = 0;
        if (cardModifierSaves != null) {
            for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                ArrayList<AbstractCardModifier> cardModifiers = new ArrayList<>();

                JsonElement loaded = i >= cardModifierSaves.size() ? null : cardModifierSaves.get(i);
                if (loaded != null && loaded.isJsonArray()) {
                    JsonArray array = loaded.getAsJsonArray();

                    for (JsonElement element : array) {
                        AbstractCardModifier cardModifier = null;
                        try {
                            cardModifier = gson.fromJson(element, new TypeToken<AbstractCardModifier>() {
                            }.getType());
                        } catch (Exception e) {
                            System.out.println("Unable to load cardmod: " + element);
                            cardModifiers.add(getErrorMod());
                        }
                        if (cardModifier != null) {
                            cardModifiers.add(cardModifier);
                        }
                    }
                }
                CardModifierManager.removeAllModifiers(card, true);
                for (AbstractCardModifier mod : cardModifiers) {
                    CardModifierManager.addModifier(card, mod.makeCopy());
                }
                i++;
            }
        }

        // Relics
        ModSaves.ArrayListOfJsonElement modRelicSaves = ModSaves.modRelicSaves.get(CardCrawlGame.saveFile);
        i = 0;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof CustomSavableRaw) {
                ((CustomSavableRaw)relic).onLoadRaw(modRelicSaves == null || i >= modRelicSaves.size() ? null : modRelicSaves.get(i));
            }
            i++;
        }

        // Potions
        ModSaves.ArrayListOfJsonElement modPotionSaves = ModSaves.modPotionSaves.get(CardCrawlGame.saveFile);
        i = 0;
        for (AbstractPotion potion : AbstractDungeon.player.potions) {
            if (potion instanceof CustomSavableRaw) {
                ((CustomSavableRaw)potion).onLoadRaw(modPotionSaves == null || i >= modPotionSaves.size() ? null : modPotionSaves.get(i));
            }
            i++;
        }

        // Seen Events
        ModSaves.ArrayListOfString seenEventSaves = ModSaves.eventSaves.get(CardCrawlGame.saveFile);
        if (seenEventSaves != null)
            SeenEvents.seenEvents.get(AbstractDungeon.player).addAll(seenEventSaves);

        // Custom save fields
        ModSaves.HashMapOfJsonElement modSaves = ModSaves.modSaves.get(CardCrawlGame.saveFile);
        for (Map.Entry<String, CustomSavableRaw> field : BaseMod.getSaveFields().entrySet()) {
            field.getValue().onLoadRaw(modSaves == null ? null : modSaves.get(field.getKey()));
        }
    }

    public static AbstractCardModifier getErrorMod() {
        return new MadnessMod();
    }

    @AbstractCardModifier.SaveIgnore
    private static class MadnessMod extends AbstractCardModifier {

        private MadnessMod() {}

        @Override
        public String modifyName(String cardName, AbstractCard card) {
            return "Madness " + cardName;
        }

        @Override
        public String modifyDescription(String rawDescription, AbstractCard card) {
            return rawDescription + " NL " + "This card was saved with a modifier that could not be loaded.";
        }

        @Override
        public AbstractCardModifier makeCopy() {
            return new MadnessMod();
        }
    }
}
