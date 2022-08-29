package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import basemod.abstracts.DynamicVariable;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DescriptionLine;
import com.megacrit.cardcrawl.core.Settings;

import java.util.function.BiConsumer;
import java.util.regex.Pattern;

@SpirePatch(
		clz=AbstractCard.class,
		method="displayUpgrades"
)
public class SmithPreview
{
	public static void Postfix(AbstractCard __instance)
	{
		ForEachDynamicVariable(__instance, (card, dv) -> {
			if (dv.upgraded(card)) {
				dv.setIsModified(card, true);
			}
		});
	}

	public static void ForEachDynamicVariable(AbstractCard card, BiConsumer<AbstractCard, DynamicVariable> callback)
	{
		Pattern pattern;
		if (Settings.lineBreakViaCharacter) {
			pattern = Pattern.compile("\\$(.+)\\$\\$");
		} else {
			pattern = Pattern.compile("!(.+)!.*");
		}

		for (DescriptionLine line : card.description) {
			String[] tokenized;
			if (Settings.lineBreakViaCharacter) {
				tokenized = line.getCachedTokenizedTextCN();
			} else {
				tokenized = line.getCachedTokenizedText();
			}
			for (String word : tokenized) {
				java.util.regex.Matcher matcher = pattern.matcher(word);
				if (matcher.find()) {
					word = matcher.group(1);

					DynamicVariable dv = BaseMod.cardDynamicVariableMap.get(word);
					if (dv != null) {
						callback.accept(card, dv);
					}
				}
			}
		}
	}
}
