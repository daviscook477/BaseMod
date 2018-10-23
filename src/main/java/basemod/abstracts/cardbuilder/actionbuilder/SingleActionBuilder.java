package basemod.abstracts.cardbuilder.actionbuilder;

import basemod.abstracts.cardbuilder.CardBasic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.function.Function;

public class SingleActionBuilder extends ActionBuilder {
	
	public static final boolean BASE_TO_TOP = false;
	
	@FunctionalInterface
	public interface ActionFunction {
		public AbstractGameAction apply(CardBasic card, AbstractPlayer player, AbstractMonster monster);
	}
	
	private ActionFunction buildAction;
	private Function<CardBasic, String> description;
	private boolean toTop;
	
	public SingleActionBuilder(ActionFunction buildAction, Function<CardBasic, String> description) {
		this(buildAction, description, BASE_TO_TOP);
	}
	
	public SingleActionBuilder(ActionFunction buildAction, Function<CardBasic, String> description, boolean toTop) {
		this.buildAction = buildAction;
		this.description = description;
		this.toTop = toTop;
	}
	
	@Override
	public boolean toTop() {
		return toTop;
	}
	
	@Override
	public String description(CardBasic card) {
		return description.apply(card);
	}
	
	@Override
	public AbstractGameAction buildAction(CardBasic card, AbstractPlayer player, AbstractMonster monster) {
		return buildAction.apply(card, player, monster);
	}
	
}
