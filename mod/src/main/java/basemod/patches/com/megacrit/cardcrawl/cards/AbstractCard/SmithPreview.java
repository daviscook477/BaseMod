package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.DynamicVariable;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DescriptionLine;
import com.megacrit.cardcrawl.core.Settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpirePatch(
		clz=AbstractCard.class,
		method="displayUpgrades"
)
public class SmithPreview
{
	public static void Postfix(AbstractCard __instance)
	{
		Pattern pattern;
		if (Settings.lineBreakViaCharacter) {
			pattern = Pattern.compile("!(.+)!!");
		} else {
			pattern = Pattern.compile("!(.+)!.*");
		}

		for (DescriptionLine line : __instance.description) {
			for (String word : ((String) ReflectionHacks.getPrivate(line, DescriptionLine.class, "text")).split(" ")) {
				Matcher matcher = pattern.matcher(word);
				if (matcher.find()) {
					word = matcher.group(1);

					DynamicVariable dv = BaseMod.cardDynamicVariableMap.get(word);
					if (dv != null) {
						if (dv.upgraded(__instance)) {
							dv.setIsModified(__instance, true);
						}
					}
				}
			}
		}
	}
}
