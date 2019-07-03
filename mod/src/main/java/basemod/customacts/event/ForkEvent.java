package basemod.customacts.event;

import basemod.customacts.CustomDungeon;
import basemod.patches.com.megacrit.cardcrawl.ui.buttons.ProceedButton.ContinueOntoHeart;
import basemod.patches.com.megacrit.cardcrawl.ui.buttons.ProceedButton.GoToNextDungeon;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;

import java.util.ArrayList;

public class ForkEvent extends AbstractImageEvent {
    public static final String ID = CustomDungeon.makeID("ForkInTheRoad");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String BASE_IMG = "img/events/ForkInTheRoad.png";

    private boolean afterdoor;

    public ForkEvent(boolean afterdoor) {
        super(NAME, DESCRIPTIONS[0], BASE_IMG);

        this.afterdoor = afterdoor;

        //Create a list of all possible acts following the current one.
        String actName = null;
        switch(AbstractDungeon.actNum + 1) {
            case CustomDungeon.EXORDIUM:
                actName = Exordium.NAME;
                break;
            case CustomDungeon.THECITY:
                actName = TheCity.NAME;
                break;
            case CustomDungeon.THEBEYOND:
                actName = TheBeyond.NAME;
                break;

            default:
                actName = afterdoor ? TheEnding.NAME : OPTIONS[1];
                break;
        }
        imageEventText.setDialogOption('[' + actName + ']');

        if(CustomDungeon.actnumbers.containsKey(AbstractDungeon.actNum + 1)) {
            for(final String s : CustomDungeon.actnumbers.get(AbstractDungeon.actNum + 1)) {
                imageEventText.setDialogOption('[' + CustomDungeon.dungeons.get(s).name + ']');
            }
        }
        imageEventText.setDialogOption(OPTIONS[0]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        ArrayList<String> possibilities = new ArrayList<>();

        switch(AbstractDungeon.actNum + 1) {
            case CustomDungeon.EXORDIUM:
                possibilities.add(Exordium.ID);
                break;
            case CustomDungeon.THECITY:
                possibilities.add(TheCity.ID);
                break;
            case CustomDungeon.THEBEYOND:
                possibilities.add(TheBeyond.ID);
                break;

            default:
                possibilities.add(TheEnding.ID);
                break;
        }

        if(CustomDungeon.actnumbers.containsKey(AbstractDungeon.actNum + 1)) {
            for (final String s : CustomDungeon.actnumbers.get(AbstractDungeon.actNum + 1)) {
                possibilities.add(s);
            }
        }

        //Chose the option "random act plx"
        if(buttonPressed == possibilities.size()) {
            if(AbstractDungeon.actNum + 1 >= CustomDungeon.THEENDING && !afterdoor) {
                //Approaching the heart is not among the random options.
                buttonPressed = AbstractDungeon.mapRng.random(possibilities.size() - 2) + 1;
            } else {
                buttonPressed = AbstractDungeon.mapRng.random(possibilities.size() - 1);
            }
        }

        if(AbstractDungeon.actNum + 1 >= CustomDungeon.THEENDING && !afterdoor && buttonPressed == 0) {
            ContinueOntoHeart.heartRoom(new ProceedButton());
        } else {
            if(AbstractDungeon.actNum + 1 >= CustomDungeon.THEENDING && afterdoor && buttonPressed > 0) {
                //Entering a custom act that required the keys removes the keys from your inventory.
                Settings.hasEmeraldKey = false;
                Settings.hasSapphireKey = false;
                Settings.hasRubyKey = false;
            }

            if(possibilities.get(buttonPressed) == Exordium.ID) {
                //Chosen the Exordium itself means it doesn't need to reload the act.
                AbstractDungeon.actNum++;
                GenericEventDialog.hide();
                AbstractDungeon.currMapNode.room = new NeowRoom(false);
                AbstractDungeon.currMapNode.room.onPlayerEntry();
            } else {
                nextDungeon(possibilities.get(buttonPressed));
            }
        }
    }

    public static void nextDungeon(String dungeonID) {
        //Set the stage for the next act.
        CardCrawlGame.nextDungeon = dungeonID;

        AbstractDungeon.rs = AbstractDungeon.RenderScene.NORMAL;
        if (AbstractDungeon.currMapNode.room instanceof GoToNextDungeon.ForkEventRoom && ((GoToNextDungeon.ForkEventRoom) AbstractDungeon.currMapNode.room).originalRoom != null) {
            AbstractDungeon.currMapNode.room = ((GoToNextDungeon.ForkEventRoom) AbstractDungeon.currMapNode.room).originalRoom;
        }
        GenericEventDialog.hide();

        AbstractDungeon.fadeOut();
        AbstractDungeon.isDungeonBeaten = true;
    }
}