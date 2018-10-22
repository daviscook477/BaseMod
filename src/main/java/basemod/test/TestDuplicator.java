package basemod.test;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class TestDuplicator extends AbstractImageEvent {
    public static final String ID = "TestDuplicator";
    private static final EventStrings eventStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    private int screenNum = 0;
    private static final String DIALOG_1;
    private static final String DIALOG_2;
    private static final String IGNORE;

    public TestDuplicator() {
        super(NAME, DIALOG_1, "images/events/shrine4.jpg");
        imageEventText.setDialogOption(OPTIONS[0]);
        imageEventText.setDialogOption(OPTIONS[1]);
    }

    public void onEnterRoom() {
        CardCrawlGame.music.playTempBGM("SHRINE");
    }

    public void update() {
        super.update();
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty() && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = ((AbstractCard)AbstractDungeon.gridSelectScreen.selectedCards.get(0)).makeStatEquivalentCopy();
            c.inBottleFlame = false;
            c.inBottleLightning = false;
            c.inBottleTornado = false;
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, (float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }

    }

    protected void buttonEffect(int buttonPressed) {
        switch(this.screenNum) {
            case 0:
                switch(buttonPressed) {
                    case 0:
                        imageEventText.updateBodyText(DIALOG_2);
                        imageEventText.updateDialogOption(0, OPTIONS[1]);
                        imageEventText.clearRemainingOptions();
                        this.use();
                        this.screenNum = 2;
                        this.logMetric("One dupe");
                        return;
                    case 1:
                        this.screenNum = 2;
                        imageEventText.updateBodyText(IGNORE);
                        imageEventText.updateDialogOption(0, OPTIONS[1]);
                        imageEventText.clearRemainingOptions();
                        this.logMetric("Ignored");
                        return;
                    default:
                        return;
                }
            case 1:
                this.screenNum = 2;
                break;
            case 2:
                this.openMap();
        }

    }

    public void use() {
        AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck, 1, OPTIONS[2], false, false, false, false);
    }

    public void logMetric(String result) {
        AbstractEvent.logMetric("Duplicator", result);
    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString("Duplicator");
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
        DIALOG_1 = DESCRIPTIONS[0];
        DIALOG_2 = DESCRIPTIONS[1];
        IGNORE = DESCRIPTIONS[2];
    }
}
