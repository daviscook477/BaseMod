package basemod.patches.com.megacrit.cardcrawl.events.SensoryStone;

import basemod.abstracts.CustomPlayer;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.events.beyond.SensoryStone;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Collections;

@SpirePatch(
		clz=SensoryStone.class,
		method="getRandomMemory"
)
public class CustomCharacterText
{
	@SpireInsertPatch(
			locator=Locator.class,
			localvars={"memories"}
	)
	public static void Insert(SensoryStone __instance, ArrayList<String> memories)
	{
		for (AbstractPlayer p : CardCrawlGame.characterManager.getAllCharacters()) {
			if (p instanceof CustomPlayer) {
				String sensoryText = ((CustomPlayer) p).getSensoryStoneText();
				if (sensoryText != null) {
					memories.add(sensoryText);
				}
			}
		}
	}

	private static class Locator extends SpireInsertLocator
	{
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(Collections.class, "shuffle");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}
}
