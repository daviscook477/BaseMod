package basemod.abstracts.cardbuilder.actionbuilder;

import basemod.abstracts.cardbuilder.CardBasic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HealActionBuilder extends AmountActionBuilder {
	
	public HealActionBuilder() {
		super();
	}
	
	public HealActionBuilder(int amount, int upgradedAmount) {
		super(amount, upgradedAmount);
	}

	@Override
	public String description(CardBasic card) {
		StringBuilder description = new StringBuilder();
		description.append("Heal ");
		if (usingMagicNumber()) {
			description.append("!M! ");
		} else {
			description.append(getAmount(card));
			description.append(" ");
		}
		description.append("HP. ");
		return description.toString();
	}
	
	@Override
	public AbstractGameAction buildAction(CardBasic card, AbstractPlayer player, AbstractMonster monster) {
		return new HealAction(player, player, getAmount(card));
	}
	
}
