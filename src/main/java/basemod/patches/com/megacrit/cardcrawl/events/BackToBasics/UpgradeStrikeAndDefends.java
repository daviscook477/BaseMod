package basemod.patches.com.megacrit.cardcrawl.events.BackToBasics;

import basemod.abstracts.CustomCard;
import basemod.helpers.BaseModCardTags;
import basemod.helpers.CardTags;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.BackToBasics;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

@SpirePatch(
		clz=BackToBasics.class,
		method="upgradeStrikeAndDefends"
)
public class UpgradeStrikeAndDefends {
	@SpireInsertPatch(rloc = 30)
	public static void Insert(BackToBasics __instance) {
		for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
			if (c.canUpgrade()) {
				boolean doUpgrade = false;
				if (c instanceof CustomCard && (((CustomCard) c).isStrike() || ((CustomCard) c).isDefend())) {
					doUpgrade = true;
				} else if (CardTags.hasTag(c, BaseModCardTags.BASIC_STRIKE) || CardTags.hasTag(c, BaseModCardTags.BASIC_DEFEND)) {
					doUpgrade = true;
				}

				if (doUpgrade) {
					c.upgrade();
					AbstractDungeon.player.bottledCardUpgradeCheck(c);
					AbstractDungeon.effectList.add(
							new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), MathUtils.random(0.1F, 0.9F) * Settings.WIDTH,
									MathUtils.random(0.2F, 0.8F) * Settings.HEIGHT));
				}
			}
		}
	}
}
