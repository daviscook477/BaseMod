package basemod.abstracts.cardbuilder.actionbuilder;

import basemod.abstracts.cardbuilder.CardBasic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.function.Supplier;

public class ApplyPowerActionBuilder extends AmountActionBuilder {
	
	@FunctionalInterface
	public interface PowerFunction {
		public AbstractPower apply(CardBasic card, AbstractPlayer player, AbstractMonster monster, Integer amount);
	}
	
	public enum PowerTarget {
		PLAYER, MONSTER
	}
	
	private PowerFunction buildPower;
	private Supplier<String> effectName;
	
	private PowerTarget target;
	
	private boolean doTarget = false;
	
	public ApplyPowerActionBuilder(PowerFunction buildPower,
			Supplier<String> effectName) {
		super();
		this.buildPower = buildPower;
		this.effectName = effectName;
	}
	
	public ApplyPowerActionBuilder(int amount, int upgradedAmount,
			PowerFunction buildPower, Supplier<String> effectName) {
		super(amount, upgradedAmount);
		this.buildPower = buildPower;
		this.effectName = effectName;
	}
	
	public ApplyPowerActionBuilder setTarget(PowerTarget target) {
		this.target = target;
		this.doTarget = true;
		
		return this;
	}
	
	private boolean isTargetPlayer(CardBasic card) {
		if (doTarget) return (target == PowerTarget.PLAYER);
		
		return (card.target == CardTarget.SELF);
	}
	
	@Override
	public String description(CardBasic card) {
		StringBuilder description = new StringBuilder();
		if (isTargetPlayer(card)) {
			description.append("Gain ");
		} else {
			description.append("Apply ");
		}
		if (usingMagicNumber()) {
			description.append("!M! ");
		} else {
			description.append(getAmount(card));
			description.append(" ");
		}
		description.append(effectName.get());
		description.append(". ");
		return description.toString();
	}
	
	@Override
	public AbstractGameAction buildAction(CardBasic card, AbstractPlayer player, AbstractMonster monster) {
		AbstractPower powerToApply = buildPower.apply(card, player, monster, getAmount(card));
		if (isTargetPlayer(card)) {
			return new ApplyPowerAction(player, player, powerToApply, getAmount(card));
		} else {
			return new ApplyPowerAction(monster, player, powerToApply, getAmount(card));
		}
		
	}
	
}
