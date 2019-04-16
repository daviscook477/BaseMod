package basemod6.events;

public class PostCampfireEvent extends ResultEvent<Boolean>
{
	private boolean selected;

	public PostCampfireEvent()
	{
		this(true);
	}

	public PostCampfireEvent(boolean selected)
	{
		this.selected = selected;
	}

	@Override
	public Boolean result()
	{
		return selected;
	}

	public boolean getOptionSelected()
	{
		return selected;
	}

	public void setOptionSelected(boolean selected)
	{
		this.selected = selected;
	}
}
