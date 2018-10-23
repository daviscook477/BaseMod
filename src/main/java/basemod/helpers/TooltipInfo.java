package basemod.helpers;

import com.megacrit.cardcrawl.helpers.PowerTip;

public class TooltipInfo
{
    public String title;
    public String description;

    public TooltipInfo(String title, String description)
    {
        this.title = title;
        this.description = description;
    }

    public PowerTip toPowerTip()
    {
        return new PowerTip(title, description);
    }
}
