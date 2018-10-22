package basemod.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.List;

public class ModalChoiceScreen
{
    public static class Enum
    {
        @SpireEnum
        public static AbstractDungeon.CurrentScreen MODAL_CHOICE;
    }

    private static float PAD_X;
    public List<AbstractCard> cardGroup;
    public boolean isOpen = false;
    private String header;
    private float showTimer;
    private CardGroup savedHand;

    private CardGroup exhaustCardGroup;
    private AbstractCard exhaustCard;

    public void open(List<AbstractCard> cards, String header)
    {
        PAD_X = 40.0f * Settings.scale;

        AbstractDungeon.topPanel.unhoverHitboxes();
        cardGroup = cards;
        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.screen = Enum.MODAL_CHOICE;
        this.header = header;
        AbstractDungeon.dynamicBanner.appear(header);
        AbstractDungeon.overlayMenu.proceedButton.hide();
        AbstractDungeon.overlayMenu.endTurnButton.disable();
        placeCards(Settings.WIDTH / 2.0f, Settings.HEIGHT * 0.45f);
        showTimer = 0.8f;
        isOpen = true;
    }

    public void reopen()
    {
        AbstractDungeon.screen = Enum.MODAL_CHOICE;
        AbstractDungeon.topPanel.unhoverHitboxes();
        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.dynamicBanner.appear(header);
        AbstractDungeon.overlayMenu.proceedButton.hide();
        AbstractDungeon.overlayMenu.endTurnButton.disable();
    }

    public void close()
    {
        isOpen = false;
        AbstractDungeon.player.hand = savedHand;
        //savedHand = null;

        AbstractDungeon.overlayMenu.endTurnButton.enable();
        AbstractDungeon.dynamicBanner.hide();
        AbstractDungeon.screen = AbstractDungeon.CurrentScreen.NONE;
        AbstractDungeon.isScreenUp = false;

        if (exhaustCardGroup != null) {
            exhaustCardGroup.moveToExhaustPile(exhaustCard);
        }
    }

    private void fakeClose()
    {
        AbstractDungeon.overlayMenu.endTurnButton.disable();
        AbstractDungeon.screen = AbstractDungeon.CurrentScreen.NONE;
        AbstractDungeon.isScreenUp = false;
    }

    public void delayExhaust(CardGroup group, AbstractCard card)
    {
        exhaustCardGroup = group;
        exhaustCard = card;
    }

    public void update()
    {
        if (showTimer > 0) {
            showTimer -= Gdx.graphics.getDeltaTime();
            if (Settings.FAST_MODE) {
                showTimer -= Gdx.graphics.getDeltaTime();
            }
            for (AbstractCard c : cardGroup) {
                c.update();
            }
            return;
        }

        savedHand = AbstractDungeon.player.hand;
        AbstractDungeon.player.hand = new CardGroup(CardGroup.CardGroupType.HAND);
        for (AbstractCard c : cardGroup) {
            AbstractDungeon.player.hand.addToHand(c);
        }
        fakeClose();
    }

    public void render(SpriteBatch sb)
    {
        renderCardReward(sb);
    }

    private void renderCardReward(SpriteBatch sb)
    {
        for (AbstractCard c : cardGroup) {
            c.render(sb);
        }
        for (AbstractCard c : cardGroup) {
            c.renderCardTip(sb);
        }
    }

    private void placeCards(float x, float y)
    {
        float spacing = AbstractCard.IMG_WIDTH;
        if (cardGroup.size() <= 5) {
            spacing += PAD_X;
        } else if (cardGroup.size() > 6){
            spacing -= PAD_X;
        }
        for (int i=0; i<cardGroup.size(); ++i) {
            cardGroup.get(i).target_y = y;

            cardGroup.get(i).target_x = Settings.WIDTH / 2.0f;
            int posOffset = i - (cardGroup.size() / 2);
            cardGroup.get(i).target_x += posOffset * spacing;

            if (cardGroup.size() % 2 == 0) {
                // if even, shift everything one half spacing
                cardGroup.get(i).target_x += (spacing / 2.0f);
            }
        }

        for (AbstractCard c : cardGroup) {
            c.drawScale = 0.75f;
            c.targetDrawScale = 0.75f;
            c.current_x = x;
            c.current_y = y;
        }
    }
}