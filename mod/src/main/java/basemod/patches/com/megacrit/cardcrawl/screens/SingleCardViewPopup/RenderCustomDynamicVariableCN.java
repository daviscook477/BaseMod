package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import basemod.BaseMod;
import basemod.abstracts.DynamicVariable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.regex.Pattern;

@SpirePatch(
		clz=SingleCardViewPopup.class,
		method="renderDescriptionCN"
)
public class RenderCustomDynamicVariableCN
{
	@SpireInsertPatch(
			locator=Locator.class,
			localvars={"card", "tmp"}
	)
	public static void Insert(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard card, @ByRef String[] tmp)
	{
		if (tmp[0].startsWith("$") || tmp[0].equals("D")) {
			String key = tmp[0];

			Pattern pattern = Pattern.compile("\\$(.+)\\$\\$");
			java.util.regex.Matcher matcher = pattern.matcher(key);
			if (matcher.find()) {
				key = matcher.group(1);
			}

			DynamicVariable dv = BaseMod.cardDynamicVariableMap.get(key);
			if (dv != null) {
				if (dv.isModified(card)) {
					if (dv.value(card) >= dv.modifiedBaseValue(card)) {
						tmp[0] = "[#" + dv.getIncreasedValueColor().toString() + "]" + Integer.toString(dv.value(card)) + "[]";
					} else {
						tmp[0] = "[#" + dv.getDecreasedValueColor().toString() + "]" + Integer.toString(dv.value(card)) + "[]";
					}
				} else {
					//cardmods affect base variables
					tmp[0] = Integer.toString(dv.modifiedBaseValue(card));
				}
			}
		}
	}

	private static class Locator extends SpireInsertLocator
	{
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
		{
			Matcher finalMatcher = new Matcher.MethodCallMatcher(String.class, "length");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}
}
