package basemod.eventUtil;

import basemod.eventUtil.util.Condition;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.events.AbstractEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddEventParams
{
	public String eventID;
	public Class<? extends AbstractEvent> eventClass;

	public EventUtils.EventType eventType = EventUtils.EventType.NORMAL;

	public List<String> dungeonIDs = new ArrayList<>();
	public List<AbstractPlayer.PlayerClass> playerClasses = new ArrayList<>();

	public Condition spawnCondition = null;
	public Condition bonusCondition = null;

	public String overrideEventID = null;

	public boolean endsWithRewardsUI = false;

	public static class Builder
	{
		private AddEventParams params = new AddEventParams();

		public Builder(String eventID, Class<? extends AbstractEvent> eventClass)
		{
			params.eventID = eventID;
			params.eventClass = eventClass;
		}

		public AddEventParams create()
		{
			return params;
		}

		public Builder dungeonID(String dungeonID)
		{
			params.dungeonIDs.add(dungeonID);
			return this;
		}

		public Builder dungeonIDs(String... dungeonIDs)
		{
			Collections.addAll(params.dungeonIDs, dungeonIDs);
			return this;
		}

		public Builder playerClass(AbstractPlayer.PlayerClass playerClass)
		{
			params.playerClasses.add(playerClass);
			return this;
		}

		public Builder playerClasses(AbstractPlayer.PlayerClass... playerClass)
		{
			Collections.addAll(params.playerClasses, playerClass);
			return this;
		}

		public Builder spawnCondition(Condition spawnCondition)
		{
			if (params.spawnCondition == null) {
				params.spawnCondition = spawnCondition;
			}
			else {
				Condition old = params.spawnCondition;
				params.spawnCondition = () -> old.test() && spawnCondition.test();
			}
			return this;
		}

		public Builder bonusCondition(Condition bonusCondition)
		{
			if (params.bonusCondition == null) {
				params.bonusCondition = bonusCondition;
			}
			else {
				Condition old = params.bonusCondition;
				params.bonusCondition = () -> old.test() && bonusCondition.test();
			}
			return this;
		}

		public Builder overrideEvent(String overrideEventID)
		{
			params.overrideEventID = overrideEventID;
			return this;
		}

		public Builder eventType(EventUtils.EventType eventType)
		{
			params.eventType = eventType;
			return this;
		}

		public Builder endsWithRewardsUI(boolean value) {
			params.endsWithRewardsUI = value;
			return this;
		}
	}
}
