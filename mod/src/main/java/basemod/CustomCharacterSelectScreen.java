package basemod;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;

import java.util.ArrayList;
import java.util.Set;

public class CustomCharacterSelectScreen extends CharacterSelectScreen {

    //Number of characters per selection screen. If changed update arrow positions.
    private int optionsPerIndex = 4;
    private int selectIndex = 0;
    private int maxSelectIndex;
    private int optionsIndex;
    private LeftOptionsButton leftArrow;
    private RightOptionsButton rightArrow;
    private ArrayList<CharacterOption> allOptions;

    public CustomCharacterSelectScreen(){
        super();
        leftArrow = new LeftOptionsButton(
                "images/ui/popupArrow.png",
                (int)(425 * Settings.scale),
                (int)((Settings.isFourByThree ? 244 : 180) * Settings.scale)
        );
        rightArrow = new RightOptionsButton(
                "images/ui/popupArrow.png",
                (int)(1425 * Settings.scale),
                (int)((Settings.isFourByThree ? 244 : 180) * Settings.scale)
        );
        updateOptionsIndex();
        allOptions = new ArrayList<>();
    }

    @Override
    public void initialize() {
        super.initialize();
        allOptions.clear();
        for (CharacterOption option : BaseMod.generateCharacterOptions()) {
            options.add(option);
        }
        for (CharacterOption option : this.options) {
            allOptions.add(option);
        }

        if (allOptions.size() == optionsPerIndex + 1) {
            ++optionsPerIndex;
        }

        selectIndex = 0;
		updateOptionsIndex();
        maxSelectIndex = (int)Math.ceil((float)allOptions.size() / optionsPerIndex) - 1;
        this.options = new ArrayList<> (allOptions.subList(0, Math.min(optionsPerIndex, allOptions.size())));
        this.positionButtons();
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if(this.selectIndex < this.maxSelectIndex) {
            rightArrow.render(sb);
        }
        if(this.selectIndex != 0){
            leftArrow.render(sb);
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.selectIndex < this.maxSelectIndex) {
            rightArrow.update();
        }
        if (this.selectIndex != 0) {
            leftArrow.update();
        }
    }

    private void positionButtons() {
        int count = this.options.size();

        float offsetX = Settings.WIDTH / 2.0F - (optionsPerIndex / 2f) * 220.0F * Settings.scale + 0.5F * 220.0F * Settings.scale;

        for(int i = 0; i < count; ++i) {
            this.options.get(i).hb.move(
                    offsetX + (float)i * 220.0F * Settings.scale,
                    (Settings.isFourByThree ? 254.0F : 190.0F) * Settings.scale
            );
        }

        leftArrow.move(
                offsetX - 220.0F * Settings.scale,
                (Settings.isFourByThree ? 254.0F : 190.0F) * Settings.scale
        );

        rightArrow.move(
                offsetX + (float)count * 220.0F * Settings.scale,
                (Settings.isFourByThree ? 254.0F : 190.0F) * Settings.scale
        );

    }

    private void setCurrentOptions(boolean rightClicked){

        if(rightClicked && selectIndex < maxSelectIndex) {
            selectIndex++;
        } else if(!rightClicked && selectIndex > 0) {
            selectIndex--;
        }

        updateOptionsIndex();

        int endIndex = optionsIndex + optionsPerIndex;

        this.options = new ArrayList<> (this.allOptions.subList(optionsIndex, Math.min(allOptions.size(), endIndex)));
        this.options.forEach(o -> o.selected = false);
        this.positionButtons();
    }

    private void updateOptionsIndex() {
        optionsIndex = optionsPerIndex * selectIndex;
    }

    private class LeftOptionsButton implements IUIElement {

        private Texture arrow;
        private int x;
        private int y;
        private int w;
        private int h;

        private Hitbox hitbox;


        public LeftOptionsButton (String imgUrl, int x, int y) {
            this.arrow = ImageMaster.loadImage(imgUrl);
            this.x = x;
            this.y = y;
            this.w = (int)(Settings.scale * arrow.getWidth() / 2f);
            this.h = (int)(Settings.scale * arrow.getHeight() / 2f);
            this.hitbox = new Hitbox(x,y,w,h);
        }

        public void move(float newX, float newY) {
            x = (int) (newX - w / 2f);
            y = (int) (newY - h / 2f);
            hitbox.move(newX, newY);
        }

        @Override
        public void render(SpriteBatch sb) {
            if (hitbox.hovered) {
                sb.setColor(Color.WHITE);
            } else {
                sb.setColor(Color.LIGHT_GRAY);
            }
            float halfW = arrow.getWidth() / 2f;
            float halfH = arrow.getHeight() / 2f;
            sb.draw(
                    arrow,
                    x + 10f * Settings.xScale - halfW + halfW * 0.5f * Settings.scale, y + 10f * Settings.yScale - halfH + halfH * 0.5f * Settings.scale,
                    halfW, halfH,
                    arrow.getWidth(), arrow.getHeight(),
                    0.75f * Settings.scale,
                    0.75f * Settings.scale,
                    0f,
                    0, 0,
                    arrow.getWidth(), arrow.getHeight(),
                    false, false
            );
            hitbox.render(sb);
        }

        @Override
        public void update() {
            hitbox.update();
            if(this.hitbox.hovered && InputHelper.justClickedLeft) {
                CardCrawlGame.sound.play("UI_CLICK_1");
                setCurrentOptions(false);
            }
        }

        @Override
        public int renderLayer() {
            return 0;
        }

        @Override
        public int updateOrder() {
            return 0;
        }
    }

    private class RightOptionsButton implements IUIElement {


        private Texture arrow;
        private int x;
        private int y;
        private int w;
        private int h;

        private Hitbox hitbox;


        public RightOptionsButton (String imgUrl, int x, int y) {
            this.arrow = ImageMaster.loadImage(imgUrl);
            this.x = x;
            this.y = y;
            this.w = (int)(Settings.scale * arrow.getWidth() / 2f);
            this.h = (int)(Settings.scale * arrow.getHeight() / 2f);
            this.hitbox = new Hitbox(x,y,w,h);
        }

        public void move(float newX, float newY) {
            x = (int) (newX - w / 2f);
            y = (int) (newY - h / 2f);
            hitbox.move(newX, newY);
        }

        @Override
        public void render(SpriteBatch sb) {
            if (hitbox.hovered) {
                sb.setColor(Color.WHITE);
            } else {
                sb.setColor(Color.LIGHT_GRAY);
            }
            float halfW = arrow.getWidth() / 2f;
            float halfH = arrow.getHeight() / 2f;
            sb.draw(
                    arrow,
                    x - 10f * Settings.xScale - halfW + halfW * 0.5f * Settings.scale, y + 10f * Settings.yScale - halfH + halfH * 0.5f * Settings.scale,
                    halfW, halfH,
                    arrow.getWidth(), arrow.getHeight(),
                    0.75f * Settings.scale,
                    0.75f * Settings.scale,
                    0f,
                    0, 0,
                    arrow.getWidth(), arrow.getHeight(),
                    true, false
            );
            hitbox.render(sb);
        }

        @Override
        public void update() {
            hitbox.update();
            if(this.hitbox.hovered && InputHelper.justClickedLeft) {
                CardCrawlGame.sound.play("UI_CLICK_1");
                setCurrentOptions(true);
            }
        }

        @Override
        public int renderLayer() {
            return 0;
        }

        @Override
        public int updateOrder() {
            return 0;
        }
    }
}
