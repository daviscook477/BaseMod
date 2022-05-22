package basemod.patches.com.megacrit.cardcrawl.ui.buttons.ProceedButton;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.CombatPhase;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import javassist.CtBehavior;

@SpirePatch(
        clz = ProceedButton.class,
        method = "update"
)
public class EventCombatProceed {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void postCombat(ProceedButton __instance) {
        //Before line 140 of ProceedButton
        if (AbstractDungeon.getCurrRoom().event instanceof PhasedEvent) {
            AbstractDungeon.getCurrRoom().event.waitTimer = 0.0f;
            //Setting wait timer to 0 causes transition back to event after combat.
            //Will have no effect on non-combat phases since their waitTimer should be 0 already anyways.
            //See EventRoom.update
            if (((PhasedEvent) AbstractDungeon.getCurrRoom().event).currentPhase instanceof CombatPhase &&
                !((CombatPhase) ((PhasedEvent) AbstractDungeon.getCurrRoom().event).currentPhase).hasFollowup()) {
                //Going from a combat reward screen to end of event
                AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.COMBAT_REWARD;
            }
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(ProceedButton.class, "hide");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
