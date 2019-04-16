package basemod6.events;

public class PostCampfireEvent extends Event
{
	private boolean[] selected;

	public PostCampfireEvent(boolean[] selected)
	{
		this.selected = selected;
	}

	public boolean getOptionSelected()
	{
		return selected[0];
	}

	public void setOptionSelected(boolean selected)
	{
		this.selected[0] = selected;
	}
}
