package basemod.test;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Strike_Purple extends CustomCard {
	public static final String ID = "Strike_P";
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final int COST = 1;
	private static final int ATTACK_DMG = 6;
	private static final int UPGRADE_PLUS_DMG = 3;

	public Strike_Purple() {
		super(ID, NAME, TestMod.makePath(TestMod.ASSET_FOLDER, TestMod.STRIKE_PURPLE), COST, DESCRIPTION, AbstractCard.CardType.ATTACK,
				ColorEnumPatch.PURPLE, AbstractCard.CardRarity.BASIC,
				AbstractCard.CardTarget.ENEMY);

		this.baseDamage = ATTACK_DMG;
	}

	public void use(com.megacrit.cardcrawl.characters.AbstractPlayer p, AbstractMonster m) {
		if (com.megacrit.cardcrawl.core.Settings.isDebug) {
			if (com.megacrit.cardcrawl.core.Settings.isInfo) {
				this.multiDamage = new int[AbstractDungeon.getCurrRoom().monsters.monsters.size()];
				for (int i = 0; i < AbstractDungeon.getCurrRoom().monsters.monsters.size(); i++) {
					this.multiDamage[i] = 150;
				}
				AbstractDungeon.actionManager.addToBottom(
						new com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction(p, this.multiDamage,
								this.damageTypeForTurn, AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
			} else {
				AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.common.DamageAction(m,
						new DamageInfo(p, 150, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
			}
		} else {
			AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.common.DamageAction(m,
					new DamageInfo(p, this.damage, this.damageTypeForTurn),
					AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
		}
	}

	public AbstractCard makeCopy() {
		return new Strike_Purple();
	}

	public void upgrade() {
		if (!this.upgraded) {
			upgradeName();
			upgradeDamage(UPGRADE_PLUS_DMG);
		}
	}

	public boolean isStrike() {
		return true;
	}
}