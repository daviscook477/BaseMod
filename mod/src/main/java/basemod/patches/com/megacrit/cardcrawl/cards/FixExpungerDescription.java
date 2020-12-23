package basemod.patches.com.megacrit.cardcrawl.cards;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.tempCards.Expunger;

@SpirePatch(
		clz = Expunger.class,
		method = "makeStatEquivalentCopy"
)
public class FixExpungerDescription
{
	public static AbstractCard Postfix(AbstractCard __result, Expunger __instance)
	{
		__result.rawDescription = __instance.rawDescription;
		return __result;
	}
}
