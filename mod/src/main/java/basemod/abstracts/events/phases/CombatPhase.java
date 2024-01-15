package basemod.abstracts.events.phases;

import basemod.abstracts.events.PhasedEvent;
import basemod.patches.com.megacrit.cardcrawl.rooms.AbstractRoom.EventCombatSave;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.function.Consumer;

public class CombatPhase extends EventPhase {
    //For combat rewards: See AbstractRoom's update method.
    private final String encounterKey;

    private boolean postCombatSave;

    private boolean allowRewards;
    public boolean cardReward;
    private Consumer<AbstractRoom> addRewards = null;
    public boolean waitingRewards;

    private EventPhase followup = null;
    private Object key = null;
    private AbstractMonster.EnemyType type;

    private FollowupType followupType;

    private enum FollowupType {
        NONE,
        PHASE,
        KEY
    }

    public CombatPhase(String encounterKey) {
        this.encounterKey = encounterKey;
        this.allowRewards = false;
        this.cardReward = true;
        this.postCombatSave = true; //If there is no followup, this is the end of the event so saving is enabled.

        this.type = AbstractMonster.EnemyType.NORMAL;

        waitingRewards = false;
        followupType = FollowupType.NONE;
    }
    public CombatPhase addRewards(boolean cardReward, Consumer<AbstractRoom> addRewards) {
        //Enables a reward screen after the combat.
        //cardReward controls whether or not a card will drop.
        //A potion may drop based on standard potion rng
        //There will not be gold.
        //To add relics/gold to the reward, use the addRewards parameter.
        this.allowRewards = true;
        this.cardReward = cardReward;
        this.addRewards = addRewards;
        return this;
    }
    public CombatPhase setType(AbstractMonster.EnemyType type) {
        //If the encounter is an elite/boss encounter, set it appropriately.
        this.type = type;
        return this;
    }
    //Use only one of these. Whichever is used last will take priority.
    public CombatPhase setNextPhase(EventPhase postCombat) {
        followup = postCombat;
        if (followup != null) {
            followupType = FollowupType.PHASE;
            postCombatSave = false;
        }
        return this;
    }
    public CombatPhase setNextKey(Object postCombatKey) {
        key = postCombatKey;
        if (key != null) {
            followupType = FollowupType.KEY;
            postCombatSave = false;
        }
        return this;
    }

    public boolean hasFollowup() {
        return followupType != FollowupType.NONE;
    }

    public String getEncounter() {
        return encounterKey;
    }

    public void postCombatTransition(PhasedEvent event) {
        EventCombatSave.allowSave();
        if (hasFollowup()) {
            switch (followupType) {
                case PHASE:
                    event.transitionPhase(followup);
                    break;
                case KEY:
                    event.transitionKey(key);
                    break;
            }
        }
    }

    @Override
    public void transition(PhasedEvent event) {
        AbstractDungeon.getCurrRoom().cannotLose = false;
        AbstractDungeon.getCurrRoom().rewardTime = false;
        AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(encounterKey);
        AbstractDungeon.lastCombatMetricKey = encounterKey;

        AbstractEvent.type = AbstractEvent.EventType.ROOM;
        event.resetCardRarity();
        if (type == AbstractMonster.EnemyType.ELITE) {
            event.setCardRarity(40, 10);
            AbstractDungeon.getCurrRoom().eliteTrigger = true;
        }
        else if (type == AbstractMonster.EnemyType.BOSS) {
            event.setCardRarity(0, 420);
        }
        event.noCardsInRewards = !cardReward;

        AbstractDungeon.getCurrRoom().rewards.clear();
        AbstractDungeon.getCurrRoom().rewardAllowed = allowRewards;
        if (addRewards != null)
            addRewards.accept(AbstractDungeon.getCurrRoom());

        if (encounterKey.equals(MonsterHelper.SHIELD_SPEAR_ENC)) {
            AbstractDungeon.player.movePosition((float) Settings.WIDTH / 2.0F, AbstractDungeon.floorY);
        } else {
            AbstractDungeon.player.movePosition((float)Settings.WIDTH * 0.25F, AbstractDungeon.floorY);
            AbstractDungeon.player.flipHorizontal = false;
        }

        event.enterCombat(); //sets rs

        if (allowRewards) {
            waitingRewards = true;
        }

        if (!postCombatSave)
            EventCombatSave.preventSave();
        else
            EventCombatSave.allowSave();
    }

    @Override
    public boolean reopen(PhasedEvent phasedEvent) {
        if (waitingRewards) {
            if (hasFollowup())
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
            waitingRewards = false;
            phasedEvent.waitTimer = 69; //will not reopen again until reward screen is finished
            //See EventCombatProceed
        }
        else if (!hasFollowup()) {
            return false;
        }
        else {
            AbstractDungeon.resetPlayer();
            phasedEvent.finishCombat();
            postCombatTransition(phasedEvent);
        }
        return true;
    }

    @Override
    public void hide(PhasedEvent event) {
        AbstractDungeon.getCurrRoom().monsters.monsters.clear();
        AbstractDungeon.getCurrRoom().rewards.clear();
        AbstractDungeon.getCurrRoom().cannotLose = false;
        AbstractDungeon.getCurrRoom().isBattleOver = false;
        AbstractDungeon.getCurrRoom().rewardTime = false;
        AbstractDungeon.getCurrRoom().eliteTrigger = false;
        event.noCardsInRewards = false;
    }
}
