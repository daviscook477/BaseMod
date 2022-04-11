package basemod.patches.com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

import basemod.BaseMod;
import basemod.CustomCharacterSelectScreen;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        clz = MainMenuScreen.class,
        method = "updateCharSelectController"
)
public class CharacterSelectControllerSupport {

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static SpireReturn Insert(MainMenuScreen __instance) {
        if(!BaseMod.getModdedCharacters().isEmpty() && __instance.charSelectScreen instanceof CustomCharacterSelectScreen) {
            if(((CustomCharacterSelectScreen)__instance.charSelectScreen).updateCharSelectController()) {
               return SpireReturn.Return();
            }
        }
        return SpireReturn.Continue();
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher matcher = new Matcher.FieldAccessMatcher(MainMenuScreen.class, "charSelectScreen");
            int[] found = LineFinder.findInOrder(ctBehavior, new ArrayList<>(), matcher);
            found[0] -= 1;
            return found;
        }
    }

}
