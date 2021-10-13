package basemod.helpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.PowerTip;

public class CardPowerTip extends PowerTip {
    public AbstractCard card;
    public float textHeight = 0.0f;
    public float cardScale = 0.8f;

    public CardPowerTip(AbstractCard card) {
        this.card = card;
        header = null;
        body = null;
        img = null;
        imgRegion = null;
    }

    public CardPowerTip(AbstractCard card, String header, String body) {
        super(header, body);
        this.card = card;
    }

    public CardPowerTip(AbstractCard card, String header, String body, Texture img) {
        super(header, body, img);
        this.card = card;
    }

    public CardPowerTip(AbstractCard card, String header, String body, TextureAtlas.AtlasRegion region48) {
        super(header, body, region48);
        this.card = card;
    }
}
