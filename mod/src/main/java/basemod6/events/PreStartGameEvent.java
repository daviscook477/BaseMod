package basemod6.events;

import basemod.BaseMod;

public class PreStartGameEvent extends Event
{
	public PreStartGameEvent()
	{
		BaseMod.MAX_HAND_SIZE = BaseMod.DEFAULT_MAX_HAND_SIZE;
	}
}
