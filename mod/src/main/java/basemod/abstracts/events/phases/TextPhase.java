package basemod.abstracts.events.phases;

import basemod.Pair;
import basemod.abstracts.events.PhasedEvent;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TextPhase extends ImageEventPhase {
    private final String body;
    private final List<OptionInfo> options;

    public TextPhase(String bodyText) {
        body = bodyText;
        options = new ArrayList<>();
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
        options.add(new OptionInfo(optionText, relicReward).setOptionResult(onClick));
        return this;
    }

    public TextPhase addOption(String optionText, Consumer<Integer> onClick) {
        options.add(new OptionInfo(optionText).setOptionResult(onClick));
        return this;
    }
    public TextPhase addOption(OptionInfo option, Consumer<Integer> onClick) {
        options.add(option.setOptionResult(onClick));
        return this;
    }
    public TextPhase addOption(OptionInfo option) {
        options.add(option);
        return this;
    }

    public TextPhase addOption(String optionText, AbstractRelic previewRelic, Consumer<Integer> onClick) {
        options.add(new OptionInfo(optionText, previewRelic).setOptionResult(onClick));
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
    public void optionChosen(PhasedEvent event, int index) {
        if (index < options.size()) {
            options.get(index).choose(event, index);
        }
    }

    public static class OptionInfo {
        private static final BiConsumer<PhasedEvent, Integer> emptyConsumer = (e, i)->{};

        private final OptionType type;
        private final String text;
        private final AbstractCard card;
        private final AbstractRelic relic;

        private final List<Pair<Supplier<Boolean>, String>> conditions = new ArrayList<>();

        public BiConsumer<PhasedEvent, Integer> optionResult = emptyConsumer;

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
            return enabledCondition(enabledCondition, text);
        }

        public OptionInfo enabledCondition(Supplier<Boolean> enabledCondition, String disabledText) {
            this.conditions.add(new Pair<>(enabledCondition, disabledText));
            return this;
        }

        public OptionInfo setOptionResult(Consumer<Integer> optionResult) {
            this.optionResult = (event, i)->optionResult.accept(i);
            return this;
        }
        public OptionInfo setOptionResult(BiConsumer<PhasedEvent, Integer> optionResult) {
            this.optionResult = optionResult;
            return this;
        }

        public OptionInfo cardSelectOption(Object followupKey, Supplier<CardGroup> cardSupplier, String text, int amount, boolean forUpgrade, boolean forTransform, boolean canCancel, boolean forPurge, BiConsumer<PhasedEvent, ArrayList<AbstractCard>> result) {
            return setOptionResult((event, i) -> {
                Consumer<EventPhase> origHandler = event.currentPhase.getUpdateHandler();

                AbstractDungeon.gridSelectScreen.open(cardSupplier.get(), amount, text, forUpgrade, forTransform, canCancel, forPurge);

                event.currentPhase.setUpdateHandler(
                        eventPhase -> {
                            if (AbstractDungeon.isScreenUp)
                                return;

                            eventPhase.setUpdateHandler(origHandler);

                            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                                result.accept(event, AbstractDungeon.gridSelectScreen.selectedCards);
                                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                            }

                            if (followupKey != null)
                                event.transitionKey(followupKey);
                        }
                );
            });
        }

        public OptionInfo cardSelectOption(Object followupKey, Supplier<CardGroup> cardSupplier, String text, int amount, boolean forUpgrade, boolean forTransform, boolean canCancel, boolean forPurge, Consumer<ArrayList<AbstractCard>> result) {
            return cardSelectOption(followupKey, cardSupplier, text, amount, forUpgrade, forTransform, canCancel, forPurge,
                    (event, cards)->result.accept(cards));
        }

        public OptionInfo cardUpgradeOption(Object followupKey, String upgradeText, int amount) {
            return cardSelectOption(followupKey, ()->AbstractDungeon.player.masterDeck.getUpgradableCards(), upgradeText, amount, true, false, false, false,
                    (event, cards)->{
                        if (cards.size() == 1) {
                            AbstractCard c = cards.get(0);
                            c.upgrade();
                            AbstractEvent.logMetricCardUpgrade(event.id, "Upgraded", c);
                            AbstractDungeon.player.bottledCardUpgradeCheck(c);
                            AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        }
                        else {
                            List<String> cardUpgrades = new ArrayList<>();
                            for (AbstractCard c : cards) {
                                c.upgrade();
                                cardUpgrades.add(c.cardID);
                                AbstractDungeon.player.bottledCardUpgradeCheck(c);
                                AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                                AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                            }
                            AbstractEvent.logMetricUpgradeCards(event.id, "Upgrades", cardUpgrades);
                        }
                    });
        }

        public OptionInfo cardRemovalOption(Object followupKey, String removalText, int amount) {
            return cardSelectOption(followupKey, ()->CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), removalText, amount, false, false, false, true,
                    (event, cards)->{
                        if (cards.size() == 1) {
                            AbstractCard c = cards.get(0);
                            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2)));
                            AbstractEvent.logMetricCardRemoval(event.id, "Purged", c);
                            AbstractDungeon.player.masterDeck.removeCard(c);
                        }
                        else if (cards.size() > 1) {
                            float x = Settings.WIDTH / 2f;
                            float offset = 0;
                            if (cards.size() < 5) {
                                offset = Settings.WIDTH / 6f;
                                x -= offset * ((cards.size() - 1) / 2f);
                            }

                            List<String> removedCards = new ArrayList<>();
                            for (AbstractCard c : cards) {
                                AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, x, (float) (Settings.HEIGHT / 2)));
                                removedCards.add(c.cardID);
                                AbstractDungeon.player.masterDeck.removeCard(c);
                                x += offset;
                            }

                            AbstractEvent.logMetric(event.id, "Purged", null, removedCards, null, null, null, null, null, 0, 0, 0, 0, 0, 0);
                        }
                    });
        }

        public void set(GenericEventDialog dialog) {
            boolean disabled = false;
            String finalText = text;
            for (Pair<Supplier<Boolean>, String> condition : conditions) {
                if (!condition.getKey().get()) {
                    disabled = true;
                    finalText = condition.getValue();
                    break;
                }
            }

            switch (type) {
                case CARD:
                    dialog.setDialogOption(finalText, disabled, card);
                    break;
                case RELIC:
                    dialog.setDialogOption(finalText, disabled, relic);
                    break;
                case BOTH:
                    dialog.setDialogOption(finalText, disabled, card, relic);
                    break;
                default:
                    dialog.setDialogOption(finalText, disabled);
                    break;
            }
        }

        public void choose(PhasedEvent event, int i) {
            optionResult.accept(event, i);
        }

        private enum OptionType {
            TEXT,
            CARD,
            RELIC,
            BOTH
        }
    }
}
