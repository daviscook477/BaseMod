package basemod.patches.com.megacrit.cardcrawl.helpers.CardLibrary;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class GetCardModID
{
    @SpirePatch(
            cls="com.megacrit.cardcrawl.helpers.CardLibrary",
            method="getCard",
            paramtypes={"java.lang.String"}
    )
    public static class GetCardPatch1
    {
        public static void Prefix(@ByRef String[] key)
        {
            key[0] = BaseMod.convertToModID(key[0]);
        }
    }

    @SpirePatch(
            cls="com.megacrit.cardcrawl.helpers.CardLibrary",
            method="getCard",
            paramtypes={"com.megacrit.cardcrawl.characters.AbstractPlayer$PlayerClass", "java.lang.String"}
    )
    public static class GetCardPatch2
    {
        public static void Prefix(AbstractPlayer.PlayerClass plyrClass, @ByRef String[] key)
        {
            key[0] = BaseMod.convertToModID(key[0]);
        }
    }
}
