package basemod6.eventbus;

import basemod6.events.Event;

public interface IEventListener
{
	void invoke(Event event);
}
