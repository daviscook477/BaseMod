package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.eventUtil.EventUtils;
import basemod.eventUtil.util.ConditionalEvent;
import basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer.SeenEvents;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Map;

import static basemod.eventUtil.EventUtils.eventLogger;

public class AddEvents {
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = { String.class, String.class, AbstractPlayer.class, ArrayList.class }
    )
    public static class NormalAndShrineEvents {
        @SpireInsertPatch(
                locator = EventLocator.class
        )
        public static void insert(AbstractDungeon __instance, String a, String b, AbstractPlayer c, ArrayList<String> bup) {
            eventLogger.info("Adding normal events and shrines on Dungeon Instantiation");

            for (Map.Entry<String, ConditionalEvent<? extends AbstractEvent>> e : EventUtils.normalEvents.entrySet()) {
                if (e.getValue().isValid()) {
                    if (SeenEvents.seenEvents.get(c).contains(e.getKey()))
                    {
                        eventLogger.info("Event " + e.getValue() + " was already seen and will not be added.");
                        continue;
                    }
                    eventLogger.info("Added " + e.getValue() + " to the event list for " + AbstractDungeon.id + ".");
                    AbstractDungeon.eventList.add(e.getKey());
                }
            }
            for (Map.Entry<String, ConditionalEvent<? extends AbstractEvent>> e : EventUtils.shrineEvents.entrySet()) {
                if (e.getValue().isValid()) {
                    eventLogger.info("Added " + e.getValue() + " to the shrine list for " + AbstractDungeon.id + ".");
                    AbstractDungeon.shrineList.add(e.getKey());
                }
            }

            eventLogger.info("Checking for replacement normal events and shrines...");
            for (Map.Entry<String, ArrayList<ConditionalEvent<? extends AbstractEvent>>> replacements : EventUtils.fullReplaceEventList.entrySet()) {
                if (AbstractDungeon.eventList.contains(replacements.getKey())) {
                    for (ConditionalEvent<? extends AbstractEvent> e : replacements.getValue()) {
                        if (e.isValid()) {
                            AbstractDungeon.eventList.add(AbstractDungeon.eventList.indexOf(replacements.getKey()), e.overrideEvent);
                            AbstractDungeon.eventList.remove(replacements.getKey());
                            eventLogger.info("Replaced " + replacements.getKey() + " with " + e.overrideEvent + " in event list for " + AbstractDungeon.id + ".");
                            break;
                        }
                    }
                }
                if (AbstractDungeon.shrineList.contains(replacements.getKey())) {
                    for (ConditionalEvent<? extends AbstractEvent> e : replacements.getValue()) {
                        if (e.isValid()) {
                            AbstractDungeon.shrineList.add(AbstractDungeon.shrineList.indexOf(replacements.getKey()), e.overrideEvent);
                            AbstractDungeon.shrineList.remove(replacements.getKey());
                            eventLogger.info("Replaced " + replacements.getKey() + " with " + e.overrideEvent + " in shrine list for " + AbstractDungeon.id + ".");
                            break;
                        }
                    }
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {String.class, AbstractPlayer.class, SaveFile.class}
    )
    public static class SaveAndLoadShrineEvents {
        @SpireInsertPatch(
                locator = EventLocator.class
        )
        public static void insert(AbstractDungeon __instance, String a, AbstractPlayer who, SaveFile why) {
            eventLogger.info("Initializing Shrine List on load.");
            for (Map.Entry<String, ConditionalEvent<? extends AbstractEvent>> e : EventUtils.shrineEvents.entrySet()) {
                if (e.getValue().isValid()) {
                    eventLogger.info("Added " + e.getValue() + " to the shrine list for " + AbstractDungeon.id + ".");
                    AbstractDungeon.shrineList.add(e.getKey());
                }
            }

            eventLogger.info("Checking for Shrine replacements...");
            for (Map.Entry<String, ArrayList<ConditionalEvent<? extends AbstractEvent>>> replacements : EventUtils.fullReplaceEventList.entrySet()) {
                if (AbstractDungeon.shrineList.contains(replacements.getKey())) {
                    for (ConditionalEvent<? extends AbstractEvent> e : replacements.getValue()) {
                        if (e.isValid()) {
                            AbstractDungeon.shrineList.add(AbstractDungeon.shrineList.indexOf(replacements.getKey()), e.overrideEvent);
                            AbstractDungeon.shrineList.remove(replacements.getKey());
                            eventLogger.info("Replaced " + replacements.getKey() + " with " + e.overrideEvent + " in shrine list for " + AbstractDungeon.id + ".");
                            break;
                        }
                    }
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "initializeSpecialOneTimeEventList"
    )
    public static class OneTimeEvents {
        @SpirePostfixPatch
        public static void postfix(AbstractDungeon __instance) {
            eventLogger.info("Adding conditional SpecialOneTimeEvents.");

            for (Map.Entry<String, ConditionalEvent<? extends AbstractEvent>> e : EventUtils.oneTimeEvents.entrySet()) {
                if (!e.getValue().spawnCondition.test()) //not an evil event, or evil event + evil mode
                {
                    if (e.getValue().playerClass == null || AbstractDungeon.player.getClass().equals(e.getValue().playerClass)) {
                        eventLogger.info("Added " + e.getValue() + " to the specialOneTimeEventList.");
                        AbstractDungeon.specialOneTimeEventList.add(e.getKey());
                    }
                }
            }

            eventLogger.info("Checking for SpecialOneTimeEvent replacements...");
            for (Map.Entry<String, ArrayList<ConditionalEvent<? extends AbstractEvent>>> replacements : EventUtils.fullReplaceEventList.entrySet()) {
                if (AbstractDungeon.specialOneTimeEventList.contains(replacements.getKey())) {
                    for (ConditionalEvent<? extends AbstractEvent> e : replacements.getValue()) {
                        if (e.isValid()) {
                            AbstractDungeon.specialOneTimeEventList.add(AbstractDungeon.specialOneTimeEventList.indexOf(replacements.getKey()), e.overrideEvent);
                            AbstractDungeon.specialOneTimeEventList.remove(replacements.getKey());
                            eventLogger.info("Replaced " + replacements.getKey() + " with " + e.overrideEvent + " in special one time event list for " + AbstractDungeon.id + ".");
                            break;
                        }
                    }
                }
            }
        }
    }

    private static class EventLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "initializeCardPools");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
