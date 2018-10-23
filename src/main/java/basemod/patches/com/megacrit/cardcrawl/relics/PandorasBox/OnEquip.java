package basemod.patches.com.megacrit.cardcrawl.relics.PandorasBox;

import basemod.abstracts.CustomCard;
import basemod.helpers.BaseModCardTags;
import basemod.helpers.CardTags;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.PandorasBox;

import java.lang.reflect.Field;
import java.util.Iterator;

@SpirePatch(
		clz=PandorasBox.class,
		method="onEquip"
)
public class OnEquip {
	@SpireInsertPatch(rloc=12)
	public static void Insert(PandorasBox __instance) {
		Field count;
		try {
			count = __instance.getClass().getDeclaredField("count");
			count.setAccessible(true);
			for (Iterator<AbstractCard> i = AbstractDungeon.player.masterDeck.group.iterator(); i.hasNext();) {
				AbstractCard c = i.next();
				boolean doRemove = false;
				if (c instanceof CustomCard && (((CustomCard)c).isStrike() || ((CustomCard)c).isDefend())) {
					doRemove = true;
				} else if (CardTags.hasTag(c, BaseModCardTags.BASIC_STRIKE) || CardTags.hasTag(c, BaseModCardTags.BASIC_DEFEND)) {
					doRemove = true;
				}

				if (doRemove) {
					i.remove();
					count.set(__instance, ((Integer) count.get(__instance)) + 1);
				}
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}