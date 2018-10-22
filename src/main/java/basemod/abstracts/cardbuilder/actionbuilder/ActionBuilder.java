package basemod.abstracts.cardbuilder.actionbuilder;

import basemod.abstracts.cardbuilder.CardBasic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public abstract class ActionBuilder {
	public boolean toTop() { return false; }
	
	public String description(CardBasic card) { return ""; }
	
	public abstract AbstractGameAction buildAction(CardBasic card, AbstractPlayer player, AbstractMonster monster);
}