package basemod.patches.com.megacrit.cardcrawl.ui.buttons.ProceedButton;

import basemod.customacts.CustomDungeon;
import basemod.customacts.event.ForkEvent;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import javassist.CtBehavior;

@SpirePatch(
        clz = ProceedButton.class,
        method = "goToNextDungeon"
)
public class GoToNextDungeon {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static SpireReturn<Void> Insert(ProceedButton __instance, AbstractRoom room) {
        //Trigger the fork event if there is a custom act available here.
        if(CustomDungeon.actnumbers.containsKey(AbstractDungeon.actNum + 1)) {
            getForked();

            return SpireReturn.Return(null);
        } else {
            switch(AbstractDungeon.actNum + 1) {
                case CustomDungeon.THECITY:
                    CardCrawlGame.nextDungeon = TheCity.ID;
                    break;
                case CustomDungeon.THEBEYOND:
                    CardCrawlGame.nextDungeon = TheBeyond.ID;
                    break;
                case CustomDungeon.THEENDING:
                    CardCrawlGame.nextDungeon = TheEnding.ID;
                    break;

                default:
                    CardCrawlGame.nextDungeon = Exordium.ID;
                    break;
            }
        }

        return SpireReturn.Continue();
    }

    public static void getForked() {
        getForked(false);
    }
    public static void getForked(boolean afterdoor) {
        AbstractDungeon.currMapNode.room = new ForkEventRoom(AbstractDungeon.currMapNode.room, afterdoor);
        AbstractDungeon.getCurrRoom().onPlayerEntry();
        AbstractDungeon.rs = AbstractDungeon.RenderScene.EVENT;

        AbstractDungeon.combatRewardScreen.clear();
        AbstractDungeon.previousScreen = null;
        AbstractDungeon.closeCurrentScreen();
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "fadeOut");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    public static class ForkEventRoom extends EventRoom {
        public AbstractRoom originalRoom;
        private boolean afterdoor;

        public ForkEventRoom(AbstractRoom originalRoom, boolean afterdoor) {
            this.originalRoom = originalRoom;
            this.afterdoor = afterdoor;
        }

        @Override
        public void onPlayerEntry() {
            AbstractDungeon.overlayMenu.proceedButton.hide();
            this.event = new ForkEvent(afterdoor);
            this.event.onEnterRoom();
        }
    }
}
