package basemod.patches.com.megacrit.cardcrawl.helpers.EventHelper;

import basemod.eventUtil.EventUtils;
import basemod.eventUtil.util.ConditionalEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.helpers.EventHelper;

@SpirePatch(
        clz = EventHelper.class,
        method = "getEvent"
)
public class GetCustomEvents {
    @SpirePrefixPatch
    public static SpireReturn<AbstractEvent> prefix(String key) {
        if (EventUtils.overrideEvents.containsKey(key)) {
            for (ConditionalEvent<? extends AbstractEvent> c : EventUtils.overrideEvents.get(key)) {
                if (c.isValid() &&
                        (!EventUtils.overrideBonusConditions.containsKey(c) ||
                        EventUtils.overrideBonusConditions.get(c).test())) {
                    return SpireReturn.Return(c.getEvent());
                }
            }
        }

        AbstractEvent e = EventUtils.getEvent(key);

        if (e != null)
            return SpireReturn.Return(e);

        return SpireReturn.Continue();
    }
}
