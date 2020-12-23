package basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import java.util.ArrayList;
import java.util.HashMap;

@SpirePatch(clz=SaveFile.class, method=SpirePatch.CLASS)
public class ModSaves
{
    // Note: if we use a SpireField<ArrayList<T>>, then mod the spire discards the <T> part of the type
    // this causes gson to not know how to deserialize the elements
    public static class ArrayListOfJsonElement extends ArrayList<JsonElement> {}
    public static class ArrayListOfString extends ArrayList<String> {}
    public static class HashMapOfJsonElement extends HashMap<String,JsonElement> {}

    @SerializedName("basemod:mod_saves")
    public static SpireField<HashMapOfJsonElement> modSaves = new SpireField<>(() -> null);
    @SerializedName("basemod:mod_card_saves")
    public static SpireField<ArrayListOfJsonElement> modCardSaves = new SpireField<>(() -> null);
    @SerializedName("basemod:mod_relic_saves")
    public static SpireField<ArrayListOfJsonElement> modRelicSaves = new SpireField<>(() -> null);
    @SerializedName("basemod:mod_potion_saves")
    public static SpireField<ArrayListOfJsonElement> modPotionSaves = new SpireField<>(() -> null);
    @SerializedName("basemod:abstract_card_modifiers_save")
    public static SpireField<ArrayListOfJsonElement> cardModifierSaves = new SpireField<>(() -> null);
    @SerializedName("basemod:event_saves")
    public static SpireField<ArrayListOfString> eventSaves = new SpireField<>(() -> null);
}
