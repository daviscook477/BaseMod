package basemod.patches.com.megacrit.cardcrawl.cards.CardGroup;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javafx.util.Pair;

import java.util.function.Predicate;

@SpirePatch(
		cls="com.megacrit.cardcrawl.cards.CardGroup",
		method="getGroupWithoutBottledCards"
)
public class WithoutBottledCardsPatch
{
	public static CardGroup Postfix(CardGroup __result, CardGroup group)
	{
		for (Pair<Predicate<AbstractCard>, AbstractRelic> info : BaseMod.getBottledRelicList()) {
			__result.group.removeIf(info.getKey());
		}

		return __result;
	}
}
