package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.DynamicVariable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.Strike_Red;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.util.Strings;

import java.util.function.Function;
import java.util.regex.Pattern;

@SpirePatch(
		clz=AbstractCard.class,
		method="renderDescriptionCN"
)
public class RenderCustomDynamicVariableCN
{
	public static final String VARIABLE_REGEX = "!(.+?)!";
	
	@SpireInsertPatch(
			locator=Locator.class,
			localvars={"tmp"}
	)
	public static void Insert(AbstractCard __instance, SpriteBatch sb, @ByRef String[] tmp)
	{

		// Well-formatted variables in CN desc should be like "造成 !modid:var! 伤害。" 
		// in which two whitespaces separate the variable from the other texts. 
		// And these well-formatted ones should be well tokenized into individual tokens in CustomDynamicVariableTokenizeCN. 
		// And "mal-formatted" variables are like "造成!modid:var!伤害。". 
		// It can be noted that these mal-formatted variables are not separated from the other texts.
		// One problem with these mal-formatted variables is they can really come in piles in a sentence (or a token), 
		// such as "造成!modid:var1!伤害，然后获得!modid:var2!点格挡并抽!M!张牌。"
		// which means regex match may come up with a lot of groups, and we need to replace these groups one by one
		
		String text = tmp[0];
		tmp[0] = MatchVariablesAndReplace(__instance, text);
	}

	private static class Locator extends SpireInsertLocator
	{
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
		{
			Matcher finalMatcher = new Matcher.MethodCallMatcher(String.class, "length");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}
	
	public static String MatchVariablesAndReplace(AbstractCard card, String text) {
		// I made a poll in a major Chinese modding community which showed that
		// around 64% of Chinese modders preferred a strict match instead of a loose one.
		// That means for each token passed here, we need to check if it starts with "!", 
		// filtering out all the tokens that start with other texts
		
		// This simple bool is kept here for some time when it might need to switch to other case
		boolean strictSideWon = true;
		if (strictSideWon && !text.startsWith("!"))
			return text;
		Pattern pattern = Pattern.compile(VARIABLE_REGEX);
        return stepReplace(pattern, text, m -> {
            String varKey = m.group(1);
            DynamicVariable dv = BaseMod.cardDynamicVariableMap.get(varKey);
            if (dv != null) {
                return GetValueOfVariable(card, dv);
            } else {
                return m.group(0);
            }
        });
	}
	
	public static String GetValueOfVariable(AbstractCard card, DynamicVariable dv) {
		String result = null;
		if (dv != null) {
			if (dv.isModified(card)) {
				if (dv.value(card) >= dv.modifiedBaseValue(card)) {
					result = "[#" + dv.getIncreasedValueColor() + "]" + dv.value(card) + "[]";
				} else {
					result = "[#" + dv.getDecreasedValueColor() + "]" + dv.value(card) + "[]";
				}
			} else {
				Color textColor = ReflectionHacks.getPrivate(card, AbstractCard.class, "textColor");
				Color dvColor = dv.getNormalColor();
				float oldAlpha = dvColor.a;
				if (textColor != null) {
					dvColor.a = textColor.a;
				}
				result = "[#" + dvColor + "]" + dv.modifiedBaseValue(card) + "[]";
				dvColor.a = oldAlpha;
			}
		}
		return result;
	}

	private static String stepReplace(Pattern pattern, String text, Function<java.util.regex.Matcher, String> mapper) {
		java.util.regex.Matcher matcher = pattern.matcher(text);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String replacement = mapper.apply(matcher);
			if (replacement != null) {
				matcher.appendReplacement(sb, replacement);
			}
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
}
