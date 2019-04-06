package basemod6.events;

import basemod.BaseMod;
import com.megacrit.cardcrawl.helpers.GameDictionary;

public class EditKeywordsEvent extends Event
{
	public void addKeyword(String[] names, String description) {
		addKeyword(null, names, description);
	}

	public void addKeyword(String proper, String[] names, String description) {
		addKeyword(null, proper, names, description);
	}

	public void addKeyword(String modID, String proper, String[] names, String description) {
		if (modID != null && !modID.isEmpty()) {
			if (!modID.endsWith(":")) {
				modID = modID + ":";
			}
			String uniqueParent = names[0];
			for (int i=0; i<names.length; ++i) {
				names[i] = modID + names[i];
			}
			for (String name : names) {
				BaseMod._internal_AddKeywordUnique(name, uniqueParent);
				BaseMod._internal_AddKeywordPrefix(name, modID);
			}
		}

		String parent = names[0];

		if (proper != null) {
			BaseMod._internal_AddKeywordProperName(parent, proper);
		}

		for (String name : names) {
			GameDictionary.keywords.put(name, description);
			GameDictionary.parentWord.put(name, parent);
		}
	}
}
