package basemod.abstracts.cardbuilder.actionbuilder;

import basemod.abstracts.cardbuilder.CardBasic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class AllEnemyActionBuilder extends ActionBuilder {
	private ActionBuilder singleAction;
	
	public AllEnemyActionBuilder(ActionBuilder singleAction) {
		this.singleAction = singleAction;
	}
	
	@Override
	public String description(CardBasic card) {
		String singleActionDescription = singleAction.description(card);
		return singleActionDescription.substring(0, singleActionDescription.length())
				.concat(" to all enemies.");
	}
	
	@Override
	public AbstractGameAction buildAction(CardBasic card, AbstractPlayer player, AbstractMonster monster) {
		return new AbstractGameAction() {
			
			@Override
			public void update() {
				if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
					for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
						if ((!m.isDead) && (!m.isDying)) {
							if (singleAction.toTop()) {
								AbstractDungeon.actionManager.addToTop(singleAction.buildAction(card, player, monster));
							} else {
								AbstractDungeon.actionManager.addToBottom(singleAction.buildAction(card, player, monster));
							}
						}
					}
				}
			}
			
		};
	}
	
}
