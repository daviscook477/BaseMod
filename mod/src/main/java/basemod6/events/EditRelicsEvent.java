package basemod6.events;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomBottleRelic;
import basemod6.BaseMod6;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.HashMap;
import java.util.function.Predicate;

public class EditRelicsEvent extends Event
{
	public void addRelic(AbstractRelic relic)
	{
		addRelic(relic, null);
	}

	public void addRelic(AbstractRelic relic, AbstractCard.CardColor color)
	{
		if (color == null) {
			RelicLibrary.add(relic);
		} else {
			switch (color) {
				case RED:
					RelicLibrary.addRed(relic);
					break;
				case GREEN:
					RelicLibrary.addGreen(relic);
					break;
				case BLUE:
					RelicLibrary.addBlue(relic);
					break;
				default:
					BaseMod._internal_AddRelicToCustomPool(relic, color);
					break;
			}
		}

		if (relic instanceof CustomBottleRelic) {
			registerBottleRelic(((CustomBottleRelic) relic).isOnCard(), relic);
		}
	}

	public void removeRelic(AbstractRelic relic)
	{
		removeRelic(relic, null);
	}

	@SuppressWarnings("unchecked")
	public void removeRelic(AbstractRelic relic, AbstractCard.CardColor color)
	{
		// note that this has to use reflection hacks to change the private
		// variables in RelicLibrary to successfully remove the relics
		//
		// this could be accomplished without reflection hacks by creating a
		// @SpirePatch to enable relic removal functionality
		//
		// as of right now I'm not sure which method is preferable
		// removeCard is using the @SpirePatch method
		if (color == null) {
			HashMap<String, AbstractRelic> sharedRelics = (HashMap<String, AbstractRelic>) ReflectionHacks
					.getPrivateStatic(RelicLibrary.class, "sharedRelics");
			if (sharedRelics.containsKey(relic.relicId)) {
				sharedRelics.remove(relic.relicId);
				RelicLibrary.totalRelicCount--;
				removeRelicFromTierList(relic);
			}
		} else {
			switch (color) {
				case RED:
					HashMap<String, AbstractRelic> redRelics = (HashMap<String, AbstractRelic>) ReflectionHacks
							.getPrivateStatic(RelicLibrary.class, "redRelics");
					if (redRelics.containsKey(relic.relicId)) {
						redRelics.remove(relic.relicId);
						RelicLibrary.totalRelicCount--;
						removeRelicFromTierList(relic);
					}
					break;
				case GREEN:
					HashMap<String, AbstractRelic> greenRelics = (HashMap<String, AbstractRelic>) ReflectionHacks
							.getPrivateStatic(RelicLibrary.class, "greenRelics");
					if (greenRelics.containsKey(relic.relicId)) {
						greenRelics.remove(relic.relicId);
						RelicLibrary.totalRelicCount--;
						removeRelicFromTierList(relic);
					}
					break;
				case BLUE:
					HashMap<String, AbstractRelic> blueRelics = (HashMap<String, AbstractRelic>) ReflectionHacks
							.getPrivateStatic(RelicLibrary.class, "blueRelics");
					if (blueRelics.containsKey(relic.relicId)) {
						blueRelics.remove(relic.relicId);
						RelicLibrary.totalRelicCount--;
						removeRelicFromTierList(relic);
					}
					break;
				default:
					BaseMod._internal_RemoveRelicFromCustomPool(relic, color, this::removeRelicFromTierList);
					break;
			}
		}
	}

	public void registerBottleRelic(Predicate<AbstractCard> isOnCard, AbstractRelic relic)
	{
		BaseMod._internal_AddBottleRelic(relic.relicId, isOnCard, relic);
	}

	public void registerBottleRelic(SpireField<Boolean> isOnCard, AbstractRelic relic)
	{
		registerBottleRelic(isOnCard::get, relic);
	}

	private void removeRelicFromTierList(AbstractRelic relic) {
		switch (relic.tier) {
			case STARTER:
				RelicLibrary.starterList.remove(relic);
				break;
			case COMMON:
				RelicLibrary.commonList.remove(relic);
				break;
			case UNCOMMON:
				RelicLibrary.uncommonList.remove(relic);
				break;
			case RARE:
				RelicLibrary.rareList.remove(relic);
				break;
			case SHOP:
				RelicLibrary.shopList.remove(relic);
				break;
			case SPECIAL:
				RelicLibrary.specialList.remove(relic);
				break;
			case BOSS:
				RelicLibrary.bossList.remove(relic);
				break;
			case DEPRECATED:
				BaseMod6.logger.info(relic.relicId + " is deprecated.");
				break;
			default:
				BaseMod6.logger.info(relic.relicId + "is undefined tier.");
		}
	}
}
