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
	public AbstractPlayer.PlayerClass playerClass = null;

	public Condition spawnCondition = null;
	public Condition bonusCondition = null;

	public String overrideEventID = null;

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
			params.playerClass = playerClass;
			return this;
		}

		public Builder spawnCondition(Condition spawnCondition)
		{
			params.spawnCondition = spawnCondition;
			return this;
		}

		public Builder bonusCondition(Condition bonusCondition)
		{
			params.bonusCondition = bonusCondition;
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
	}
}
