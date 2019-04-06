package basemod.patches.com.megacrit.cardcrawl.helpers.GameDictionary;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.EditKeywordsEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SpirePatch(cls="com.megacrit.cardcrawl.helpers.GameDictionary", method="initialize")
public class PostKeywordInitialize {

	public static void Postfix() {
		BaseMod6.EVENT_BUS.post(new EditKeywordsEvent());
		BaseMod.publishEditKeywords();
	}
	
}
