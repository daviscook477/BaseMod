package basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;

import basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveFile.ModSaves;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import javassist.CtBehavior;

import java.util.HashMap;

@SpirePatch(clz=SaveAndContinue.class, method="save", paramtypez={SaveFile.class})
public class Save
{
    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"params"}
    )
    public static void Insert(SaveFile save, HashMap<String, Object> params) {
        params.put("basemod:mod_saves", ModSaves.modSaves.get(save));
        params.put("basemod:mod_card_saves", ModSaves.modCardSaves.get(save));
        params.put("basemod:mod_relic_saves", ModSaves.modRelicSaves.get(save));
        params.put("basemod:mod_potion_saves", ModSaves.modPotionSaves.get(save));
        params.put("basemod:abstract_card_modifiers_save", ModSaves.cardModifierSaves.get(save));
        params.put("basemod:event_saves", ModSaves.eventSaves.get(save));
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher("com.google.gson.GsonBuilder", "create");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}