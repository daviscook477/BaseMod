package basemod.patches.com.megacrit.cardcrawl.screens.charSelect.CharacterOption;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class CorrectRemainingUnlocks {
    @SpirePatch(
            clz = CharacterOption.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = { String.class, AbstractPlayer.class, Texture.class, Texture.class }
    )
    public static class TextureConstructor
    {
        @SpirePostfixPatch
        public static void correctAmount(CharacterOption __instance, String s, AbstractPlayer p, Texture button, Texture portrait)
        {
            if (CardCrawlGame.characterManager != null && !BaseMod.isBaseGameCharacter(p)) {
                ReflectionHacks.setPrivate(__instance, CharacterOption.class, "unlocksRemaining", BaseMod.getMaxUnlockLevel(p) - UnlockTracker.getUnlockLevel(p.chosenClass));
            }
        }
    }

    @SpirePatch(
            clz = CharacterOption.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = { String.class, AbstractPlayer.class, String.class, String.class }
    )
    public static class StringConstructor
    {
        @SpirePostfixPatch
        public static void correctAmount(CharacterOption __instance, String s, AbstractPlayer p, String button, String portrait)
        {
            if (CardCrawlGame.characterManager != null && !BaseMod.isBaseGameCharacter(p)) {
                ReflectionHacks.setPrivate(__instance, CharacterOption.class, "unlocksRemaining", BaseMod.getMaxUnlockLevel(p) - UnlockTracker.getUnlockLevel(p.chosenClass));
            }
        }
    }
}
