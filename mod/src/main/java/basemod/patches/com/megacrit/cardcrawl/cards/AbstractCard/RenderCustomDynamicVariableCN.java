package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import basemod.abstracts.DynamicVariable;
import basemod.helpers.dynamicvariables.BlockVariable;
import basemod.helpers.dynamicvariables.DamageVariable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.regex.Pattern;

@SpirePatch(
		clz=AbstractCard.class,
		method="renderDescriptionCN"
)
public class RenderCustomDynamicVariableCN
{
	@SpireInsertPatch(
			locator=Locator.class,
			localvars={"tmp"}
	)
	public static void Insert(AbstractCard __instance, SpriteBatch sb, @ByRef String[] tmp)
	{
		if (tmp[0].startsWith("$")) {
			String key = tmp[0];

			Pattern pattern = Pattern.compile("\\$(.+)\\$\\$");
			java.util.regex.Matcher matcher = pattern.matcher(key);
			if (matcher.find()) {
				key = matcher.group(1);
			}

			DynamicVariable dv = BaseMod.cardDynamicVariableMap.get(key);
			if (dv != null) {
				if (dv.isModified(__instance)) {
					if (dv.value(__instance) >= dv.baseValue(__instance)) {
						tmp[0] = "[#" + dv.getIncreasedValueColor().toString() + "]" + Integer.toString(dv.value(__instance)) + "[]";
					} else {
						tmp[0] = "[#" + dv.getDecreasedValueColor().toString() + "]" + Integer.toString(dv.value(__instance)) + "[]";
					}
				} else {
					//cardmods affect base variables
					int num;
					if (dv instanceof BlockVariable && CardModifierPatches.CardModifierFields.cardModHasBaseBlock.get(__instance)) {
						num = CardModifierPatches.CardModifierFields.cardModBaseBlock.get(__instance);
						if (num >= __instance.baseBlock) {
							tmp[0] = "[#" + RenderCustomDynamicVariable.Inner.DARK_GREEN.toString() + "]" + Integer.toString(num) + "[]";
						} else {
							tmp[0] = "[#" + RenderCustomDynamicVariable.Inner.DARK_RED.toString() + "]" + Integer.toString(num) + "[]";
						}
					} else if (dv instanceof DamageVariable && CardModifierPatches.CardModifierFields.cardModHasBaseDamage.get(__instance)) {
						num = CardModifierPatches.CardModifierFields.cardModBaseDamage.get(__instance);
						if (num >= __instance.baseDamage) {
							tmp[0] = "[#" + RenderCustomDynamicVariable.Inner.DARK_GREEN.toString() + "]" + Integer.toString(num) + "[]";
						} else {
							tmp[0] = "[#" + RenderCustomDynamicVariable.Inner.DARK_RED.toString() + "]" + Integer.toString(num) + "[]";
						}
					} else {
						tmp[0] = Integer.toString(dv.baseValue(__instance));
					}
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
