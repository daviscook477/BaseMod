package basemod.abstracts.cardbuilder.actionbuilder;

import com.megacrit.cardcrawl.powers.PoisonPower;

public class PoisonActionBuilder extends ApplyPowerActionBuilder {
	
	public PoisonActionBuilder() {
		super(
				(card, player, monster, amount) -> new PoisonPower(monster, player, amount),
				() -> "Posion");
	}
	
	public PoisonActionBuilder(int amount, int upgradedAmount) {
		super(amount, upgradedAmount,
				(card, player, monster, realAmount) -> new PoisonPower(monster, player, realAmount),
				() -> "Posion");
	}
	
}
