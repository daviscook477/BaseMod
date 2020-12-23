package basemod;

import basemod.helpers.UIElementModificationHelper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.function.Consumer;

public class ModLabeledButton implements IUIElement {
    private Consumer<ModLabeledButton> click;
    private Hitbox hb;
    private float x;
    private float y;
    private float w;
    private float middle_width;
    private float h;

    public BitmapFont font;
    public String label;
    public ModPanel parent;
    public Color color, colorHover;

    // this "sinks" the text to the sides - i.e. makes the middle part of button a bit shorter than the text width,
    // looks a bit more balanced to the eye
    private static final float TEXT_OFFSET = 9F;

    private Texture textureLeft, textureRight, textureMiddle;

    public ModLabeledButton(String label, float xPos, float yPos, ModPanel p, Consumer<ModLabeledButton> c) {
        this(label, xPos, yPos, Color.WHITE, Color.GREEN, FontHelper.buttonLabelFont, p, c);
    }

    public ModLabeledButton(String label, float xPos, float yPos, Color textColor, Color textColorHover,
                            ModPanel p, Consumer<ModLabeledButton> c) {
        this(label, xPos, yPos, textColor, textColorHover, FontHelper.buttonLabelFont, p, c);
    }

    public ModLabeledButton(String label, float xPos, float yPos, Color textColor, Color textColorHover,
                            BitmapFont font, ModPanel p, Consumer<ModLabeledButton> c) {
        this.label = label;
        this.font = font;
        color = textColor;
        colorHover = textColorHover;

        textureLeft = ImageMaster.loadImage("img/ButtonLeft.png");
        textureRight = ImageMaster.loadImage("img/ButtonRight.png");
        textureMiddle = ImageMaster.loadImage("img/ButtonMiddle.png");

        x = xPos * Settings.scale;
        y = yPos * Settings.scale;

        middle_width = Math.max(0, FontHelper.getSmartWidth(font, label, 9999f, 0f) - 2 * TEXT_OFFSET * Settings.scale);
        w = (this.textureLeft.getWidth() + this.textureRight.getWidth()) * Settings.scale + middle_width;
        h = this.textureLeft.getHeight() * Settings.scale;
        hb = new Hitbox(this.x + 1F * Settings.scale, this.y + 1F * Settings.scale, this.w - 2F * Settings.scale, this.h - 2F * Settings.scale);

        parent = p;
        click = c;
    }

    public void render(SpriteBatch sb) {

        sb.draw(textureLeft, x, y, textureLeft.getWidth() * Settings.scale, h);
        sb.draw(textureMiddle, x + textureLeft.getWidth() * Settings.scale, y, middle_width, h);
        sb.draw(textureRight, x + textureLeft.getWidth() * Settings.scale + middle_width, y,
                textureRight.getWidth() * Settings.scale, h);

        hb.render(sb);

        sb.setColor(Color.WHITE);
        if (hb.hovered)
            FontHelper.renderFontCentered(sb, font, label, hb.cX, hb.cY, colorHover );
        else
            FontHelper.renderFontCentered(sb, font, label, hb.cX, hb.cY, color);
    }

    public void update() {
        hb.update();
        if (hb.justHovered) {
            CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
        }

        if (hb.hovered && InputHelper.justClickedLeft) {
            CardCrawlGame.sound.playA("UI_CLICK_1", -0.1F);
            hb.clickStarted = true;
        }

        if (hb.clicked) {
            hb.clicked = false;
            onClick();
        }

    }

    private void onClick() {
        click.accept(this);
    }

    public int renderLayer() {
        return 1;
    }

    public int updateOrder() {
        return 1;
    }

    @Override
    public void set(float xPos, float yPos) {
        x = xPos*Settings.scale;
        y = yPos*Settings.scale;

        UIElementModificationHelper.moveHitboxByOriginalParameters(hb, x + 1F * Settings.scale, y + 1F * Settings.scale);
    }

    @Override
    public void setX(float xPos) {
        set(xPos, y/Settings.scale);
    }

    @Override
    public void setY(float yPos) {
        set(x/Settings.scale, yPos);
    }

    @Override
    public float getX() {
        return x/Settings.scale;
    }

    @Override
    public float getY() {
        return y/Settings.scale;
    }
}
