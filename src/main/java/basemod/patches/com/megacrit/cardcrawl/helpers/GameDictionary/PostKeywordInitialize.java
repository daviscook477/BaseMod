package basemod.patches.com.megacrit.cardcrawl.helpers.GameDictionary;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(cls="com.megacrit.cardcrawl.helpers.GameDictionary", method="initialize")
public class PostKeywordInitialize {

	public static void Postfix() {
		BaseMod.publishEditKeywords();
	}
	
}
