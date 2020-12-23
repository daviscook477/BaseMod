package basemod;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.function.Consumer;

public class ModMinMaxSlider implements IUIElement {
    private static final float L_X = 1235.0f * Settings.scale;
    private static final float SLIDE_W = 230.0f * Settings.scale;

    private Consumer<ModMinMaxSlider> change;
    private Hitbox hb;
    private Hitbox bgHb;
    private float x;
    private float sliderX;
    private float handleX;
    private float y;
    private boolean sliderGrabbed = false;
    private String label;
    private String format;

    private float value;
    private float minValue;
    private float maxValue;
    public ModPanel parent;

    public ModMinMaxSlider(String lbl, float posX, float posY, float min, float max, float val, String format, ModPanel p, Consumer<ModMinMaxSlider> changeAction) {
        label = lbl;
        if (format == null || format.isEmpty()) {
            this.format = "%.2f";
        } else {
            this.format = format;
        }

        minValue = min;
        maxValue = max;
        parent = p;
        change = changeAction;

        if (posX == -1) {
            posX = L_X;
        } else {
            posX *= Settings.scale;
        }
        x = posX;
        sliderX = x - 11.0f * Settings.scale;
        handleX = x + SLIDE_W;
        y = posY * Settings.scale;

        hb = new Hitbox(42.0f * Settings.scale, 38.0f * Settings.scale);
        bgHb = new Hitbox(300.0f * Settings.scale, 38.0f * Settings.scale);
        bgHb.move(sliderX + SLIDE_W / 2.0f, y);

        setValue(val);
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);

        sb.draw(
                ImageMaster.OPTION_SLIDER_BG,
                sliderX, y - 12.0f,
                0.0f, 12.0f,
                250.0f, 24.0f,
                Settings.scale, Settings.scale,
                0.0f,
                0, 0,
                250, 24,
                false, false
        );
        sb.draw(
                ImageMaster.OPTION_SLIDER,
                handleX - 22.0f, y - 22.0f,
                22.0f, 22.0f,
                44.0f, 44.0f,
                Settings.scale, Settings.scale,
                0.0f,
                0, 0,
                44, 44,
                false, false
        );

        FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, label, sliderX - 55.0f * Settings.scale, y, Color.WHITE);

        String renderVal = String.format(format, getValue());
        if (sliderGrabbed) {
            FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, renderVal, sliderX + SLIDE_W + 55.0f * Settings.scale, y, Settings.GREEN_TEXT_COLOR);
        } else {
            FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, renderVal, sliderX + SLIDE_W + 55.0f * Settings.scale, y, Settings.BLUE_TEXT_COLOR);
        }

        this.hb.render(sb);
        this.bgHb.render(sb);
    }

    public void update() {
        hb.update();
        bgHb.update();
        hb.move(handleX, y);

        if (sliderGrabbed) {
            if (InputHelper.isMouseDown) {
                handleX = MathHelper.fadeLerpSnap(handleX, InputHelper.mX);
                handleX = Math.min(sliderX + SLIDE_W + 11.0f * Settings.scale, Math.max(handleX, sliderX + 11.0f * Settings.scale));
            } else {
                sliderGrabbed = false;
            }
        } else if (InputHelper.justClickedLeft) {
            if (hb.hovered || bgHb.hovered) {
                sliderGrabbed = true;
            }
        }

        float oldVal = getValue();
        value = (handleX - 11.0f * Settings.scale - sliderX) / SLIDE_W;

        if (oldVal != getValue()) {
            onChange();
        }
    }

    public float getValue() {
        return value * (maxValue - minValue) + minValue;
    }

    public void setValue(float val) {
        if (val < minValue) {
            val = minValue;
        } else if (val > maxValue) {
            val = maxValue;
        }
        val = (val - minValue) / (maxValue - minValue);
        value = val;
        handleX = sliderX + (SLIDE_W * val) + 11.0f * Settings.scale;
    }

    private void onChange() {
        change.accept(this);
    }

    @Override
    public int renderLayer() {
        return ModPanel.MIDDLE_LAYER;
    }

    @Override
    public int updateOrder() {
        return ModPanel.DEFAULT_UPDATE;
    }

    @Override
    public void set(float xPos, float yPos) {
        xPos *= Settings.scale;
        handleX = handleX + (xPos - x);
        x = xPos;
        sliderX = x - 11.0f * Settings.scale;
        y = yPos * Settings.scale;

        bgHb.move(sliderX + SLIDE_W / 2.0f, y);
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

