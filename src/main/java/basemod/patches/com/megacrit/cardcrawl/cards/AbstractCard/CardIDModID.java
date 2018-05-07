package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;

@SpirePatch(
        cls="com.megacrit.cardcrawl.cards.AbstractCard",
        method="ctor",
        paramtypes={
                "java.lang.String",
                "java.lang.String",
                "java.lang.String",
                "java.lang.String",
                "int",
                "java.lang.String",
                "com.megacrit.cardcrawl.cards.AbstractCard$CardType",
                "com.megacrit.cardcrawl.cards.AbstractCard$CardColor",
                "com.megacrit.cardcrawl.cards.AbstractCard$CardRarity",
                "com.megacrit.cardcrawl.cards.AbstractCard$CardTarget",
                "com.megacrit.cardcrawl.cards.DamageInfo$DamageType"
        }
)
public class CardIDModID
{
    public static void Prefix(AbstractCard __instance, @ByRef String[] id, String name, String jokeUrl, String imgUrl, int cost, String rawDescription,
                              AbstractCard.CardType type, AbstractCard.CardColor color, AbstractCard.CardRarity rarity, AbstractCard.CardTarget target,
                              DamageInfo.DamageType dType)
    {
        String modName = BaseMod.findCallingModName();
        if (modName != null && !id[0].startsWith(modName + ":")) {
            id[0] = modName + ":" + id[0];
        }
    }
}
