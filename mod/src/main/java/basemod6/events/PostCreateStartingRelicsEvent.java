package basemod6.events;

import com.megacrit.cardcrawl.characters.AbstractPlayer;

import java.util.ArrayList;

public class PostCreateStartingRelicsEvent extends ResultEvent<ArrayList<String>>
{
	private AbstractPlayer.PlayerClass playerClass;
	private ArrayList<String> relicIDs;

	public PostCreateStartingRelicsEvent(AbstractPlayer.PlayerClass playerClass, ArrayList<String> relicIDs)
	{
		this.playerClass = playerClass;
		this.relicIDs = relicIDs;
	}

	@Override
	public ArrayList<String> result()
	{
		return relicIDs;
	}

	public AbstractPlayer.PlayerClass getPlayerClass()
	{
		return playerClass;
	}

	public ArrayList<String> getRelicIDs()
	{
		return relicIDs;
	}

	public void addRelic(String id)
	{
		relicIDs.add(id);
	}

	public void removeRelic(String id)
	{
		relicIDs.remove(id);
	}
}
