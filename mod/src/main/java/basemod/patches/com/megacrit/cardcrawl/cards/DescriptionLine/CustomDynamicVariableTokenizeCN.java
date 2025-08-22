package basemod.patches.com.megacrit.cardcrawl.cards.DescriptionLine;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.DescriptionLine;

/*
Instead of replacing "!" with "$" in CN language to prevent the bad logic of the source code,
it is better to make it more close to good logic. So I commented the insert patch to stop it from doing the replacement.
The key reason why CN modders can't use normal letters DBM in their card desc is that in initializeDescriptionCN,
the source code in one hand removes the format symbol "!" that wraps the damage variable !D! so it becomes a normal D, 
which makes it easy to be confused with truly normal D. 
And the other is that the source code appends extra format symbol to !B! and !M!
making them be !B!! and !M!!. This should be fixed in FixCNTokensNotWrappedCorrectly.
*/

@SpirePatch(
		clz=DescriptionLine.class,
		method="tokenizeCN"
)
public class CustomDynamicVariableTokenizeCN
{
	// postfix the vanilla method to change its return value
	@SpirePostfixPatch
	public static String[] RetokenizeCN(String[] tokens, String desc) {
		tokens = desc.split("\\s+");
		// no need to do any replacement so that variables like !D!, !B! and !M! and other custom variables stay the same
		return tokens;
	}
	
	
//	@SpireInsertPatch(
//			locator=Locator.class,
//			localvars={"tokenized", "i"}
//	)
//	public static void Insert(String desc, String[] tokenized, int i)
//	{
//		if (tokenized[i].startsWith("!")) {
//			String key = tokenized[i];
//
//			Pattern pattern = Pattern.compile("!(.+)!!");
//			java.util.regex.Matcher matcher = pattern.matcher(key);
//			if (matcher.find()) {
//				key = matcher.group(1);
//			}
//
//			DynamicVariable dv = BaseMod.cardDynamicVariableMap.get(key);
//			if (dv != null) {
//				tokenized[i] = tokenized[i].replace("!", "$");
//			}
//		}
//	}

//	private static class Locator extends SpireInsertLocator
//	{
//		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
//		{
//			Matcher finalMatcher = new Matcher.MethodCallMatcher(String.class, "replace");
//			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
//		}
//	}
}
