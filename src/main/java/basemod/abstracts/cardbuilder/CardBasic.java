package basemod.abstracts.cardbuilder;

import basemod.abstracts.CustomCard;
import basemod.abstracts.cardbuilder.actionbuilder.ActionBuilder;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.ExhaustAllEtherealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CardBasic extends CustomCard {

	public static final int BASE_MAX_UPGRADES = 1;
	public static final int BASE_COST = -1;
	public static final String BASE_DESCRIPTION = "";

	protected boolean doDamage = false;
	protected boolean doBlock = false;
	protected boolean doMagicNumber = false;
	protected boolean doCost = false;
	protected boolean doEthereal = false;
	protected boolean doExhaust = false;
	
	protected boolean hasDescription = false;
	protected String description;

	protected int upgradeDamageAmt;
	protected int upgradeBlockAmt;
	protected int upgradeMagicNumberAmt;
	protected int upgradedCost;
	protected boolean upgradedEthereal;
	protected boolean upgradedExhaust;

	protected boolean doUpgradeDamage = false;
	protected boolean doUpgradeBlock = false;
	protected boolean doUpgradeMagicNumber = false;
	protected boolean doUpgradeCost = false;
	protected boolean doUpgradeEthereal = false;
	protected boolean doUpgradeExhaust = false;

	protected int upgradeCount = 0;
	protected int maxUpgrades = BASE_MAX_UPGRADES;

	protected List<ActionBuilder> actions;

	protected AttackEffect attackEffect;

	public CardBasic(String cardId, String name, String img,
			CardType cardType, CardColor cardColor,
			CardRarity rarity, CardTarget target) {
		super(cardId, name, img, BASE_COST, BASE_DESCRIPTION,
				cardType, cardColor, rarity, target);
		
		if (target == CardTarget.ALL_ENEMY) {
			this.isMultiDamage = true;
		}
		
		// init lists
		this.actions = new ArrayList<ActionBuilder>();
	}

	// must be called at end of building card basic
	public CardBasic end() {
		this.rawDescription = buildDescription();
		initializeDescription();
		
		return this;
	}
	
	public CardBasic setDescription(String description) {
		this.description = description;
		this.hasDescription = true;
		return this;
	}

	public CardBasic setDamage(int damage) {
		this.baseDamage = damage;
		this.doDamage = true;
		return this;
	}

	public CardBasic setUpgradeDamageAmt(int damage) {
		this.upgradeDamageAmt = damage;
		this.doUpgradeDamage = true;
		return this;
	}

	public CardBasic setBlock(int block) {
		this.baseBlock = block;
		this.doBlock = true;
		return this;
	}

	public CardBasic setUpgradeBlockAmt(int block) {
		this.upgradeBlockAmt = block;
		this.doUpgradeBlock = true;
		return this;
	}

	public CardBasic setMagicNumber(int number) {
		this.magicNumber = this.baseMagicNumber = number;
		this.doMagicNumber = true;
		return this;
	}

	public CardBasic setUpgradeMagicNumberAmt(int number) {
		this.upgradeMagicNumberAmt = number;
		this.doUpgradeMagicNumber = true;
		return this;
	}

	public CardBasic setCost(int cost) {
		this.costForTurn = this.cost = cost;
		this.doCost = true;
		return this;
	}

	public CardBasic setUpgradedCost(int cost) {
		this.upgradedCost = cost;
		this.doUpgradeCost = true;
		return this;
	}

	public CardBasic setEthereal(boolean isEthereal) {
		this.isEthereal = isEthereal;
		this.doEthereal = true;
		return this;
	}

	public CardBasic setUpgradedEthereal(boolean isEthereal) {
		this.upgradedEthereal = isEthereal;
		this.doUpgradeEthereal = true;
		return this;
	}

	public CardBasic setExhaust(boolean exhaust) {
		this.exhaust = exhaust;
		this.doExhaust = true;
		return this;
	}

	public CardBasic setUpgradedExhaust(boolean isExhaust) {
		this.upgradedExhaust = isExhaust;
		this.doUpgradeExhaust = true;
		return this;
	}
	
	public CardBasic addAction(ActionBuilder builder) {
		this.actions.add(builder);
		return this;
	}

	public CardBasic setAttackEffect(AttackEffect effect) {
		this.attackEffect = effect;
		return this;
	}
	
	private String buildDescription() {
		if (hasDescription) {
			return description;
		}
		
		StringBuilder description = new StringBuilder();
		
		description
				.append(isEthereal ? "Ethereal. NL " : "")
		
				.append(doDamage ? "Deal !D! damage. ": "")
				.append(doBlock ? "Gain !B! block. " : "");
		
		for (ActionBuilder builder : actions) {
			description.append(builder.description(this));
		}
		
		description.append(exhaust ? " NL Exhaust." : "");
		
		return description.toString();
	}

	@Override
	public boolean canUpgrade() {
		if (this.upgradeCount < this.maxUpgrades)
			return true;
		return false;
	}

	@Override
	public void upgrade() {
		if (!canUpgrade()) {
			return;
		}

		upgradeName();
		if (doUpgradeDamage)
			upgradeDamage(upgradeDamageAmt);
		if (doUpgradeBlock)
			upgradeBlock(upgradeBlockAmt);
		if (doUpgradeMagicNumber)
			upgradeMagicNumber(upgradeMagicNumberAmt);
		if (doUpgradeCost)
			upgradeBaseCost(upgradedCost);
		if (doUpgradeEthereal)
			this.isEthereal = upgradedEthereal;
		if (doUpgradeExhaust)
			this.exhaust = upgradedExhaust;
		
		this.rawDescription = buildDescription();
		initializeDescription();
		this.upgradeCount++;
	}

	// helper function that checks doApply and if true takes the result of the
	// supplier
	// and if false returns this CardBasic - designed so we can make a chain of
	// optional
	// calls to a CardBasic in makeCopy()
	private CardBasic apply(boolean doApply, Supplier<CardBasic> applyFunc) {
		return doApply ? applyFunc.get() : this;
	}

	@Override
	public AbstractCard makeCopy() {
		CardBasic card = new CardBasic(this.cardID, this.name, this.textureImg,
				this.type, this.color, this.rarity, this.target);

		card
				.apply(hasDescription, () -> card.setDescription(this.description))
				.apply(this.doDamage, () -> card.setDamage(this.baseDamage))
				.apply(this.doBlock, () -> card.setBlock(this.baseBlock))
				.apply(this.doMagicNumber, () -> card.setMagicNumber(this.magicNumber))
				.apply(this.doCost, () -> card.setCost(this.cost))
				.apply(this.doEthereal, () -> card.setEthereal(this.isEthereal))
				.apply(this.doExhaust, () -> card.setExhaust(this.exhaust))
				
				.apply(this.doUpgradeDamage, () -> card.setUpgradeDamageAmt(this.upgradeDamageAmt))
				.apply(this.doUpgradeBlock, () -> card.setUpgradeBlockAmt(this.upgradeBlockAmt))
				.apply(this.doUpgradeMagicNumber, () -> card.setUpgradeMagicNumberAmt(this.upgradeMagicNumberAmt))
				.apply(this.doUpgradeCost, () -> card.setUpgradedCost(this.upgradedCost))
				.apply(this.doUpgradeEthereal, () -> card.setUpgradedEthereal(this.upgradedEthereal))
				.apply(this.doUpgradeExhaust, () -> card.setUpgradedExhaust(this.upgradedExhaust));
		
		for (ActionBuilder builder : actions) {
			card.addAction(builder);
		}
		
		return card.end();
	}

	@Override
	public void triggerOnEndOfPlayerTurn() {
		AbstractDungeon.actionManager.addToTop(new ExhaustAllEtherealAction());
	}

	@Override
	public void use(AbstractPlayer player, AbstractMonster monster) {
		if (this.damage > 0) {
			if (this.target == CardTarget.ENEMY) {
				AbstractDungeon.actionManager.addToBottom(new DamageAction(monster,
						new DamageInfo(player, this.damage, this.damageTypeForTurn), attackEffect));
			}

			if (this.target == CardTarget.ALL_ENEMY) {
				AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(player, this.multiDamage,
						this.damageTypeForTurn, attackEffect));
			}
		}

		if (this.block > 0)
			AbstractDungeon.actionManager.addToBottom(new GainBlockAction(player, player, this.block));			
		
		if (!this.actions.isEmpty()) {
			for (ActionBuilder builder : actions) {
				if (builder.toTop()) {
					AbstractDungeon.actionManager.addToTop(builder.buildAction(this, player, monster));
				} else {
					AbstractDungeon.actionManager.addToBottom(builder.buildAction(this, player, monster));
				}
			}
		}
	}
	
}