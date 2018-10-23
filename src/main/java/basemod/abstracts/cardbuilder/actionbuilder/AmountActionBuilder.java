package basemod.abstracts.cardbuilder.actionbuilder;

import basemod.abstracts.cardbuilder.CardBasic;

public abstract class AmountActionBuilder extends ActionBuilder {

	private boolean useMagicNumber;
	private int amount;
	private int upgradedAmount;
	
	public AmountActionBuilder() {
		this.useMagicNumber = true;
	}
	
	public AmountActionBuilder(int amount, int upgradedAmount) {
		this.useMagicNumber = false;
		this.amount = amount;
		this.upgradedAmount = upgradedAmount;
	}
	
	public boolean usingMagicNumber() {
		return useMagicNumber;
	}
	
	public int getAmount(CardBasic card) {
		if (this.useMagicNumber) {
			return card.magicNumber;
		} else {
			return card.upgraded ? upgradedAmount : amount;
		}
	}
	
}
