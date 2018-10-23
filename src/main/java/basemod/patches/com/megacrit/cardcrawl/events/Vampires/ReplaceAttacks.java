package basemod.patches.com.megacrit.cardcrawl.events.Vampires;

import basemod.abstracts.CustomCard;
import basemod.helpers.BaseModCardTags;
import basemod.helpers.CardTags;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.Vampires;

import java.util.Iterator;

@SpirePatch(
		clz=Vampires.class,
		method="replaceAttacks"
)
public class ReplaceAttacks {
	@SpireInsertPatch(rloc=8)
	public static void Insert(Vampires __instance) {
		for (Iterator<AbstractCard> i = AbstractDungeon.player.masterDeck.group.iterator(); i.hasNext();) {
			AbstractCard e = i.next();
			if (e instanceof CustomCard && ((CustomCard)e).isStrike()) {
				i.remove();
			} else if (CardTags.hasTag(e, BaseModCardTags.BASIC_STRIKE)) {
				i.remove();
			}
		}
	}
}
