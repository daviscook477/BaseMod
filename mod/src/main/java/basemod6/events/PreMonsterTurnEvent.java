package basemod6.events;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class PreMonsterTurnEvent extends ResultEvent<Boolean>
{
	private AbstractMonster monster;
	private boolean doTurn;

	public PreMonsterTurnEvent(AbstractMonster monster)
	{
		this(monster, true);
	}

	public PreMonsterTurnEvent(AbstractMonster monster, boolean doTurn)
	{
		this.monster = monster;
		this.doTurn = doTurn;
	}

	@Override
	public Boolean result()
	{
		return doTurn;
	}

	public AbstractMonster getMonster()
	{
		return monster;
	}

	public void setDoTurn(boolean doTurn)
	{
		this.doTurn = doTurn;
	}
}
