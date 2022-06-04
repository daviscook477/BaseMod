package basemod.patches.com.megacrit.cardcrawl.events.AbstractEvent;

import basemod.eventUtil.AddEventParams;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.events.AbstractEvent;

@SpirePatch(clz = AbstractEvent.class, method = SpirePatch.CLASS)
public class AdditionalEventParameters
{
    public static SpireField<AddEventParams> additionalParameters = new SpireField<>(() -> null);
}
