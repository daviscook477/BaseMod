package basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import java.util.ArrayList;
import java.util.Map;

import basemod.abstracts.AbstractCardModifier;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.CardModifierPatches;
import basemod.BaseMod;
import basemod.abstracts.CustomSavableRaw;
import basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer.SeenEvents;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

@SpirePatch(clz=SaveFile.class, method=SpirePatch.CONSTRUCTOR, paramtypez={SaveFile.SaveType.class})
public class ConstructSaveFilePatch
{
    public static void Prefix(SaveFile __instance, SaveFile.SaveType saveType) {
        // Card saves
        ModSaves.ArrayListOfJsonElement modCardSaves = new ModSaves.ArrayListOfJsonElement();
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (card instanceof CustomSavableRaw) {
                modCardSaves.add(((CustomSavableRaw)card).onSaveRaw());
            } else {
                modCardSaves.add(null);
            }
        }
        ModSaves.modCardSaves.set(__instance, modCardSaves);

        //Master deck AbstractCardModifiers
        ModSaves.ArrayListOfJsonElement cardModifierSaves = new ModSaves.ArrayListOfJsonElement();
        GsonBuilder builder = new GsonBuilder();
        if (CardModifierPatches.modifierAdapter == null) {
            CardModifierPatches.initializeAdapterFactory();
        }
        builder.registerTypeAdapterFactory(CardModifierPatches.modifierAdapter);
        Gson gson = builder.create();
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            ArrayList<AbstractCardModifier> cardModifierList = CardModifierPatches.CardModifierFields.cardModifiers.get(card);
            ArrayList<AbstractCardModifier> saveIgnores = new ArrayList<>();
            for (AbstractCardModifier mod : cardModifierList) {
                if (mod.getClass().isAnnotationPresent(AbstractCardModifier.SaveIgnore.class)) {
                    saveIgnores.add(mod);
                }
            }
            if (!saveIgnores.isEmpty()) {
                BaseMod.logger.warn("attempted to save un-savable card modifiers. Modifiers marked @SaveIgnore will not be saved on master deck.");
                BaseMod.logger.info("affected card: " + card.cardID);
                for (AbstractCardModifier mod : saveIgnores) {
                    BaseMod.logger.info("saveIgnore mod: " + mod.getClass().getName());
                }
                cardModifierList.removeAll(saveIgnores);
            }
            if (!cardModifierList.isEmpty()) {
                cardModifierSaves.add(gson.toJsonTree(cardModifierList, new TypeToken<ArrayList<AbstractCardModifier>>(){}.getType()));
            } else {
                cardModifierSaves.add(null);
            }
        }
        ModSaves.cardModifierSaves.set(__instance, cardModifierSaves);

        // Relic saves
        ModSaves.ArrayListOfJsonElement modRelicSaves = new ModSaves.ArrayListOfJsonElement();
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof CustomSavableRaw) {
                modRelicSaves.add(((CustomSavableRaw)relic).onSaveRaw());
            } else {
                modRelicSaves.add(null);
            }
        }
        ModSaves.modRelicSaves.set(__instance, modRelicSaves);

        // Potion saves
        ModSaves.ArrayListOfJsonElement modPotionSaves = new ModSaves.ArrayListOfJsonElement();
        for (AbstractPotion potion : AbstractDungeon.player.potions) {
            if (potion instanceof CustomSavableRaw) {
                modPotionSaves.add(((CustomSavableRaw)potion).onSaveRaw());
            } else {
                modPotionSaves.add(null);
            }
        }
        ModSaves.modPotionSaves.set(__instance, modPotionSaves);

        // Mod saves
        ModSaves.HashMapOfJsonElement modSaves = new ModSaves.HashMapOfJsonElement();
        for (Map.Entry<String, CustomSavableRaw> field : BaseMod.getSaveFields().entrySet()) {
            modSaves.put(field.getKey(), field.getValue().onSaveRaw());
        }
        ModSaves.modSaves.set(__instance, modSaves);

        // Event saves
        ModSaves.ArrayListOfString eventSaves = new ModSaves.ArrayListOfString();
        eventSaves.addAll(SeenEvents.seenEvents.get(AbstractDungeon.player));
        ModSaves.eventSaves.set(__instance, eventSaves);
    }
}