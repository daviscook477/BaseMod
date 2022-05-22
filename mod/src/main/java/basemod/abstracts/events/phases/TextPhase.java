package basemod.abstracts.events.phases;

import basemod.abstracts.events.PhasedEvent;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TextPhase extends ImageEventPhase {
    private final String body;
    private final List<OptionInfo> options;
    private final List<Consumer<Integer>> optionResults;

    public TextPhase(String bodyText) {
        body = bodyText;
        options = new ArrayList<>();
        optionResults = new ArrayList<>();
    }

    public void transition(PhasedEvent event) {
        AbstractDungeon.rs = AbstractDungeon.RenderScene.EVENT;
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.EVENT;
        AbstractEvent.type = AbstractEvent.EventType.IMAGE;

        event.resetCardRarity();

        GenericEventDialog.show();
        event.imageEventText.updateBodyText(getBody());
        setOptions(event);
    }

    public TextPhase addOption(String optionText, Consumer<Integer> onClick, AbstractRelic relicReward) {
        options.add(new OptionInfo(optionText, relicReward));
        optionResults.add(onClick);
        return this;
    }

    public TextPhase addOption(String optionText, Consumer<Integer> onClick) {
        options.add(new OptionInfo(optionText));
        optionResults.add(onClick);
        return this;
    }
    public TextPhase addOption(OptionInfo option, Consumer<Integer> onClick) {
        options.add(option);
        optionResults.add(onClick);
        return this;
    }

    public TextPhase addOption(String optionText, AbstractRelic previewRelic, Consumer<Integer> onClick) {
        options.add(new OptionInfo(optionText, previewRelic));
        optionResults.add(onClick);
        return this;
    }


    public String getBody() {
        return body;
    }
    public void setOptions(PhasedEvent e) {
        e.imageEventText.clearAllDialogs();
        for (OptionInfo option : options) {
            option.set(e.imageEventText);
        }
    }
    @Override
    public void optionChosen(int index) {
        if (index < optionResults.size()) {
            optionResults.get(index).accept(index);
        }
    }

    public static class OptionInfo {
        private final OptionType type;
        private final String text;
        private final AbstractCard card;
        private final AbstractRelic relic;

        private Supplier<Boolean> condition;

        public OptionInfo(String text) {
            this.type = OptionType.TEXT;
            this.text = text;
            this.card = null;
            this.relic = null;
        }
        public OptionInfo(String text, AbstractCard c) {
            this.type = OptionType.CARD;
            this.text = text;
            this.card = c;
            this.relic = null;
        }
        public OptionInfo(String text, AbstractRelic r) {
            this.type = OptionType.RELIC;
            this.text = text;
            this.card = null;
            this.relic = r;
        }
        public OptionInfo(String text, AbstractCard c, AbstractRelic r) {
            this.type = OptionType.BOTH;
            this.text = text;
            this.card = c;
            this.relic = r;
        }
        public OptionInfo enabledCondition(Supplier<Boolean> enabledCondition) {
            this.condition = enabledCondition;
            return this;
        }

        private boolean disabled() {
            return condition != null && !condition.get();
        }

        public void set(GenericEventDialog dialog) {
            switch (type) {
                case CARD:
                    dialog.setDialogOption(text, disabled(), card);
                    break;
                case RELIC:
                    dialog.setDialogOption(text, disabled(), relic);
                    break;
                case BOTH:
                    dialog.setDialogOption(text, disabled(), card, relic);
                    break;
                default:
                    dialog.setDialogOption(text, disabled());
                    break;
            }
        }


        private enum OptionType {
            TEXT,
            CARD,
            RELIC,
            BOTH
        }
    }
}
