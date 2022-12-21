package basemod.eventUtil.util;

import basemod.eventUtil.AddEventParams;
import basemod.patches.com.megacrit.cardcrawl.events.AbstractEvent.AdditionalEventParameters;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import static basemod.eventUtil.EventUtils.eventLogger;

public class ConditionalEvent<T extends AbstractEvent> {
    public Class<T> eventClass;
    public AbstractPlayer.PlayerClass playerClass;
    public AbstractPlayer.PlayerClass[] playerClasses;
    public Condition spawnCondition;
    public List<String> actIDs;

    public String overrideEvent = "";

    private final AddEventParams additionalParams;

    public ConditionalEvent(Class<T> eventClass, AbstractPlayer.PlayerClass playerClass, Condition spawnCondition, String[] actIDs, AddEventParams additionalParams) {
        this(eventClass, new AbstractPlayer.PlayerClass[] { playerClass }, spawnCondition, actIDs, additionalParams);
    }
    public ConditionalEvent(Class<T> eventClass, AbstractPlayer.PlayerClass[] playerClasses, Condition spawnCondition, String[] actIDs, AddEventParams additionalParams) {
        this.eventClass = eventClass;
        this.playerClass = playerClasses.length == 0 ? null : playerClasses[0];
        this.playerClasses = playerClasses;
        this.spawnCondition = spawnCondition;
        this.additionalParams = additionalParams;

        if (spawnCondition == null)
            this.spawnCondition = () -> true;

        this.actIDs = Arrays.asList(actIDs);
    }

    public AbstractEvent getEvent() {
        try {
            AbstractEvent event = eventClass.getConstructor().newInstance();
            AdditionalEventParameters.additionalParameters.set(event, additionalParams);
            return event;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            eventLogger.info("Failed to instantiate event " + eventClass.getName());
            e.printStackTrace();
        }
        return null;
    }

    public boolean isValid() {
        return (actIDs.isEmpty() || actIDs.contains(AbstractDungeon.id)) &&
                spawnCondition.test() &&
                playerMatch();
    }
    public boolean playerMatch() {
        if (playerClass == null)
            return true;

        for (AbstractPlayer.PlayerClass pClass : playerClasses)
            if (AbstractDungeon.player.chosenClass == pClass)
                return true;

        return false;
    }

    @Override
    public String toString() {
        return eventClass.getSimpleName();
    }

    public String getConditions() {
        return (playerClass != null ? playerClass.name().toUpperCase() : "ANY") + " | " + (actIDs.isEmpty() ? "ANY" : actIDs);
    }
}
