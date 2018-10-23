package basemod.patches.com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

import basemod.BaseMod;
import basemod.CustomCharacterSelectScreen;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        clz=MainMenuScreen.class,
        method=SpirePatch.CONSTRUCTOR,
        paramtypez={
                boolean.class
        }
)
public class SwapCharacterSelectScreen
{
    @SpireInsertPatch(
            locator=Locator.class
    )
    public static void Insert(MainMenuScreen __instance, boolean playBgm)
    {
        if (!BaseMod.getModdedCharacters().isEmpty()) {
            __instance.charSelectScreen = new CustomCharacterSelectScreen();
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(CharacterSelectScreen.class, "initialize");
            return LineFinder.findInOrder(ctBehavior, new ArrayList<>(), finalMatcher);
        }
    }
}
