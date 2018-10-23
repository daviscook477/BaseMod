package basemod.test;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.events.AbstractEvent;

public class GetEventPatch {
    @SpirePatch(
            cls="com.megacrit.cardcrawl.helpers.EventHelper",
            method="getEvent",
            paramtypes={"java.lang.String"}
    )
    public static class GetEvent {
        public static AbstractEvent Postfix(AbstractEvent origReturn, final String key) {
            if (origReturn == null && key.equals("TestDuplicator"))
            {
                return new TestDuplicator();
            }
            return origReturn;
        }
    }
}
