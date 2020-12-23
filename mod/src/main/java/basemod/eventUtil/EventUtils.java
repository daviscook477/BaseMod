package basemod.eventUtil;

import basemod.eventUtil.util.Condition;
import basemod.eventUtil.util.ConditionalEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Predicate;

public class EventUtils {
    /* D O C U M E N T A T I O N
        ID:
            The event ID. Make it whatever you want.
        eventClass:
            The class of the event itself.
        playerClass:
            The class for an AbstractPlayer this event can ONLY spawn for. If you want more specific control, use the bonusCondition.
            Default: null
        actIDs:
            The act IDs this event can spawn in.
            Default: null
        spawnCondition:
            Condition that must be fulfilled for the event to be added to the act.
            Default: null
        overrideEvent:
            The ID of an event to replace. There are two modes of replacement: Override and Full Replace.
            Override will conditionally replace the event when it is encountered. If the conditions fail, the normal event will occur.
            Full Replace will perform its check when the dungeon is instantiated, completely replacing the original event. The original event cannot appear.
                For Full Replace, the bonus condition is checked to determine if the event can appear, not when the dungeon is instantiated.

            Use certain constructors or the type parameter to determine the override method.

            Default: null
        bonusCondition:
            A condition that determines if the event can be triggered. This is checked whenever an event is encountered.
            Default: null
        type:
            The event type. Determines what pool it is (or the type of replacement, if overriding)
            Default: EventType.NORMAL

        Normal events can only be seen once, regardless of where they can appear.
        SpecialOneTimeEvents can only be seen once, but are rarer.
        Shrines can be seen once per act (but are added back to the list when save and quit occurs).
        Overload/Full Replace events can be seen whenever the event they are replacing can be seen.
     */
    public static Logger eventLogger = LogManager.getLogger("EventUtil");
    public static HashMap<String, Condition> normalEventBonusConditions = new HashMap<>();
    public static HashMap<String, Condition> specialEventBonusConditions = new HashMap<>();
    public static HashMap<ConditionalEvent<? extends AbstractEvent>, Condition> overrideBonusConditions = new HashMap<>();
    public static HashMap<String, ConditionalEvent<? extends AbstractEvent>> normalEvents = new HashMap<>();
    public static HashMap<String, ConditionalEvent<? extends AbstractEvent>> shrineEvents = new HashMap<>();
    public static HashMap<String, ConditionalEvent<? extends AbstractEvent>> oneTimeEvents = new HashMap<>();
    public static HashMap<String, ConditionalEvent<? extends AbstractEvent>> fullReplaceEvents = new HashMap<>(); //to actually get the replacement events, after replacement occurs
    public static HashMap<String, ArrayList<ConditionalEvent<? extends AbstractEvent>>> overrideEvents = new HashMap<>();
    public static HashMap<String, ArrayList<ConditionalEvent<? extends AbstractEvent>>> fullReplaceEventList = new HashMap<>();

    public static HashSet<String> eventIDs = new HashSet<>(); //for normal, shrine, oneTime, and fullReplace events.
    private static int id = 0;

    public static <T extends AbstractEvent> void registerEvent(String ID, Class<T> eventClass, AbstractPlayer.PlayerClass playerClass, String[] actIDs, Condition spawnCondition, String overrideEvent, Condition bonusCondition, EventType type) {
        /*if (!(overrideEvent != null || spawnCondition != null || actIDs != null || playerClass != null || bonusCondition != null)) {
            eventLogger.info("Event " + eventClass.getName() + " has no special conditions, and should be registered through BaseMod instead.");
            return;
        }*/

        ID = ID.replace(' ', '_');

        if (eventIDs.contains(ID)) {
            ID = generateEventKey(ID);
        }
        eventIDs.add(ID);

        ConditionalEvent<T> c = new ConditionalEvent<T>(eventClass,
                playerClass,
                spawnCondition,
                actIDs == null ? new String[]{} : actIDs);

        if (type == EventType.FULL_REPLACE && overrideEvent != null) {
            c.overrideEvent = ID;

            if (!fullReplaceEventList.containsKey(overrideEvent)) {
                fullReplaceEventList.put(overrideEvent, new ArrayList<>());
            }
            fullReplaceEventList.get(overrideEvent).add(c);

            fullReplaceEvents.put(ID, c);

            eventLogger.info("Full Replacement event " + c + " for event " + overrideEvent + " registered. " + c.getConditions());
            if (bonusCondition != null) {
                normalEventBonusConditions.put(ID, bonusCondition);
                specialEventBonusConditions.put(ID, bonusCondition);
                eventLogger.info("  This event has a bonus condition.");
            }
        } else if (overrideEvent != null) { // type == EventType.OVERRIDE && 
            c.overrideEvent = overrideEvent;

            if (!overrideEvents.containsKey(overrideEvent)) {
                overrideEvents.put(overrideEvent, new ArrayList<>());
            }
            overrideEvents.get(overrideEvent).add(c);

            eventLogger.info("Override event " + c + " for event " + overrideEvent + " registered. " + c.getConditions());

            if (bonusCondition != null) {
                overrideBonusConditions.put(c, bonusCondition);
                eventLogger.info("  This event has a bonus condition.");
            }
        } else if (type == EventType.ONE_TIME) {
            oneTimeEvents.put(ID, c);

            eventLogger.info("SpecialOneTimeEvent " + c + " registered. " + c.getConditions());
            if (bonusCondition != null) {
                specialEventBonusConditions.put(ID, bonusCondition);
                eventLogger.info("  This event has a bonus condition.");
            }
        } else if (type == EventType.SHRINE) {
            shrineEvents.put(ID, c);

            eventLogger.info("Shrine " + c + " registered. " + c.getConditions());
            if (bonusCondition != null) {
                specialEventBonusConditions.put(ID, bonusCondition);
                eventLogger.info("  This event has a bonus condition.");
            }
        } else {
            normalEvents.put(ID, c);

            eventLogger.info("Event " + c + " registered. " + c.getConditions());
            if (bonusCondition != null) {
                normalEventBonusConditions.put(ID, bonusCondition);
                eventLogger.info("  This event has a bonus condition.");
            }
        }

    }

    private static String generateEventKey(String ID) {
        return ID + id++;
    }

    public static AbstractEvent getEvent(String eventID) {
        if (EventUtils.normalEvents.containsKey(eventID)) {
            return EventUtils.normalEvents.get(eventID).getEvent();
        } else if (EventUtils.shrineEvents.containsKey(eventID)) {
            return EventUtils.shrineEvents.get(eventID).getEvent();
        } else if (EventUtils.oneTimeEvents.containsKey(eventID)) {
            return EventUtils.oneTimeEvents.get(eventID).getEvent();
        } else if (EventUtils.fullReplaceEvents.containsKey(eventID)) {
            return EventUtils.fullReplaceEvents.get(eventID).getEvent();
        }

        return null;
    }

    public static Class<? extends AbstractEvent> getEventClass(String eventID) {
        if (EventUtils.normalEvents.containsKey(eventID)) {
            return EventUtils.normalEvents.get(eventID).eventClass;
        } else if (EventUtils.shrineEvents.containsKey(eventID)) {
            return EventUtils.shrineEvents.get(eventID).eventClass;
        } else if (EventUtils.oneTimeEvents.containsKey(eventID)) {
            return EventUtils.oneTimeEvents.get(eventID).eventClass;
        } else if (EventUtils.fullReplaceEvents.containsKey(eventID)) {
            return EventUtils.fullReplaceEvents.get(eventID).eventClass;
        }

        return null;
    }

    public static HashMap<String, Class<? extends AbstractEvent>> getDungeonEvents(String dungeonID)
    {
        HashMap<String, Class<? extends AbstractEvent>> events = new HashMap<>();

        for (Map.Entry<String, ConditionalEvent<? extends AbstractEvent>> c : normalEvents.entrySet())
        {
            if (c.getValue().actIDs.contains(dungeonID))
                events.put(c.getKey(), c.getValue().eventClass);
        }
        for (Map.Entry<String, ConditionalEvent<? extends AbstractEvent>> c : shrineEvents.entrySet())
        {
            if (c.getValue().actIDs.contains(dungeonID))
                events.put(c.getKey(), c.getValue().eventClass);
        }

        return events;
    }

    public static void loadBaseEvents()
    {
        try
        {
            Field eventStrings = LocalizedStrings.class.getDeclaredField("events");
            eventStrings.setAccessible(true);

            Map<String, EventStrings> events = (Map<String, EventStrings>) eventStrings.get(null);

            if (events != null) {
                for (String key : events.keySet()) {
                    eventIDs.add(key.replace(' ', '_'));
                }
            }

        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public enum EventType {
        NORMAL,
        SHRINE,
        ONE_TIME,
        OVERRIDE,
        FULL_REPLACE
    }
}
