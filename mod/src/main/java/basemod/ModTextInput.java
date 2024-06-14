package basemod;

import basemod.interfaces.TextReceiver;
import basemod.patches.com.megacrit.cardcrawl.helpers.input.ScrollInputProcessor.TextInput;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModTextInput implements IUIElement, TextReceiver {
    private Consumer<ModTextInput> valueSetListener;

    public ModPanel parent;
    public String text;
    private String lastValidText;

    private Hitbox hb;
    public float x, y;
    public float width, height;

    public Color color, backColor = Color.WHITE.cpy(), tickColor = Color.BLACK.cpy();
    public BitmapFont font;

    private int charLimit = -1;
    private Predicate<Character> charFilter = null;
    private Predicate<String> validator = null;

    boolean inputActive = false;
    float tick = 1f;

    public ModTextInput(String initialText, float xPos, float yPos, float width, float height, ModPanel p, Consumer<ModTextInput> onValueSet) {
        this(initialText, xPos, yPos, width, height, Color.BLACK, FontHelper.buttonLabelFont, p, onValueSet);
    }
    public ModTextInput(String initialText, float xPos, float yPos, float width, float height, Color color, ModPanel p, Consumer<ModTextInput> onValueSet) {
        this(initialText, xPos, yPos, width, height, color, FontHelper.buttonLabelFont, p, onValueSet);
    }
    public ModTextInput(String initialText, float xPos, float yPos, float width, float height, BitmapFont font, ModPanel p, Consumer<ModTextInput> onValueSet) {
        this(initialText, xPos, yPos, width, height, Color.BLACK, font, p, onValueSet);
    }
    public ModTextInput(String initialText, float xPos, float yPos, float width, float height, Color color, BitmapFont font, ModPanel p, Consumer<ModTextInput> onValueSet) {
        lastValidText = text = initialText;
        setX(xPos);
        setY(yPos);

        this.width = width * Settings.scale;
        this.height = height * Settings.scale;

        hb = new Hitbox(x, y, width, height);

        this.color = color.cpy();
        this.font = font;

        parent = p;
        valueSetListener = onValueSet;
    }

    public ModTextInput setCharacterLimit(int limit) {
        charLimit = limit;
        return this;
    }
    public ModTextInput setCharacterFilter(Predicate<Character> filter) {
        charFilter = filter;
        return this;
    }
    public ModTextInput setResultValidator(Predicate<String> validator) {
        this.validator = validator;
        return this;
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x - 2, y - 2, width + 4, height + 4);
        sb.setColor(backColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y, width, height);
        FontHelper.renderFontLeft(sb, this.font, text, x + 4, y + height / 2f, this.color);
        if (inputActive && tick > 0.4f) {
            sb.setColor(tickColor);
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, x + FontHelper.getWidth(font, text, 1) + 3, y + 2, 2, height - 4);
        }
    }

    @Override
    public void update() {
        hb.update(x, y);
        tick -= Gdx.graphics.getRawDeltaTime();
        if (tick <= 0f) tick = 1f;

        if (InputHelper.justClickedLeft || InputHelper.justClickedRight) {
            if (hb.hovered) {
                if (!inputActive) {
                    inputActive = true;
                    tick = 1f;
                }
                TextInput.startTextReceiver(this);
            }
            else {
                finishEntry();
            }
        }
    }

    @Override
    public int renderLayer() {
        return ModPanel.MIDDLE_LAYER;
    }

    @Override
    public int updateOrder() {
        return ModPanel.PRIORITY_UPDATE;
    }

    @Override
    public void set(float xPos, float yPos) {
        setX(xPos);
        setY(yPos);
    }

    @Override
    public void setX(float xPos) {
        x = xPos * Settings.scale;
    }

    @Override
    public void setY(float yPos) {
        y = yPos * Settings.scale;
    }

    @Override
    public float getX() {
        return x / Settings.scale;
    }

    @Override
    public float getY() {
        return y / Settings.scale;
    }

    private void finishEntry() {
        TextInput.stopTextReceiver(this);

        if (inputActive) {
            inputActive = false;

            if (validator != null && !validator.test(text)) {
                text = lastValidText;
            }
            else {
                valueSetListener.accept(this);
            }
        }
    }


    @Override
    public String getCurrentText() {
        return text;
    }

    @Override
    public void setText(String updatedText) {
        text = updatedText;
    }

    @Override
    public boolean isDone() {
        if (!parent.isUp) {
            finishEntry();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPushEnter() {
        finishEntry();
        return true;
    }

    @Override
    public boolean acceptCharacter(char c) {
        return font.getData().hasGlyph(c) && (charFilter == null || charFilter.test(c));
    }

    @Override
    public int getCharLimit() {
        return charLimit;
    }
}
