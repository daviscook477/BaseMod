package basemod;

import basemod.interfaces.PostUpdateSubscriber;
import basemod.interfaces.RenderSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

import java.util.function.Consumer;

public class ModTextPanel implements RenderSubscriber, PostUpdateSubscriber {
	private String prevName;
	public static String textField;
	public Hitbox yesHb;
	public Hitbox noHb;
	public Consumer<ModTextPanel> confirm = null;
	public Consumer<ModTextPanel> cancel = null;
	public String defaultName;
	public String explanationText;
	public static final String CANCEL_TEXT = "Cancel";
	public static final String CONFIRM_TEXT = "Confirm";
	private static final int CONFIRM_W = 360;
	private static final int CONFIRM_H = 414;
	private static final int YES_W = 173;
	private static final int NO_W = 161;
	private static final int BUTTON_H = 74;
	private Color screenColor;
	private Color uiColor;
	private float animTimer;
	private float waitTimer;
	private static final float ANIM_TIME = 0.25f;
	public boolean shown;
	private static final float SCREEN_DARKNESS = 0.75f;
	private InputProcessor oldInputProcessor;
	private ModPanel panel;

	public ModTextPanel() {
		this.prevName = "";
		this.yesHb = null;
		this.noHb = null;
		this.screenColor = new Color(0.0f, 0.0f, 0.0f, 0.0f);
		this.uiColor = new Color(1.0f, 1.0f, 1.0f, 0.0f);
		this.animTimer = 0.0f;
		this.waitTimer = 0.0f;
		this.shown = false;

		BaseMod.subscribe(this);
	}

	public void receivePostUpdate() {
		if (!this.shown)
			return;
		
		if (Gdx.input.isKeyPressed(67) && !ModTextPanel.textField.equals("") && this.waitTimer <= 0.0f) {
			ModTextPanel.textField = ModTextPanel.textField.substring(0, ModTextPanel.textField.length() - 1);
			this.waitTimer = 0.09f;
		}
		if (this.waitTimer > 0.0f) {
			this.waitTimer -= Gdx.graphics.getDeltaTime();
		}
		if (this.shown) {
			if (this.animTimer != 0.0f) {
				this.animTimer -= Gdx.graphics.getDeltaTime();
				if (this.animTimer < 0.0f) {
					this.animTimer = 0.0f;
				}
				this.screenColor.a = Interpolation.fade.apply(SCREEN_DARKNESS, 0.0f, this.animTimer * 1.0f / ANIM_TIME);
				this.uiColor.a = Interpolation.fade.apply(1.0f, 0.0f, this.animTimer * 1.0f / ANIM_TIME);
			} else {
				this.updateYes();
				this.updateNo();
				if (Gdx.input.isKeyJustPressed(66)) {
					this.confirm();
				} else if (InputHelper.pressedEscape) {
					InputHelper.pressedEscape = false;
					this.cancel();
				}
			}
		} else if (this.animTimer != 0.0f) {
			this.animTimer -= Gdx.graphics.getDeltaTime();
			if (this.animTimer < 0.0f) {
				this.animTimer = 0.0f;
			}
			this.screenColor.a = Interpolation.fade.apply(0.0f, SCREEN_DARKNESS, this.animTimer * 1.0f / ANIM_TIME);
			this.uiColor.a = Interpolation.fade.apply(0.0f, 1.0f, this.animTimer * 1.0f / ANIM_TIME);
		}
	}

	private void updateYes() {
		this.yesHb.update();
		if (this.yesHb.justHovered) {
			CardCrawlGame.sound.play("UI_HOVER");
		}
		if (InputHelper.justClickedLeft && this.yesHb.hovered) {
			CardCrawlGame.sound.play("UI_CLICK_1");
			this.yesHb.clickStarted = true;
		}
		if (this.yesHb.clicked) {
			this.yesHb.clicked = false;
			this.confirm();
		}
	}

	private void updateNo() {
		this.noHb.update();
		if (this.noHb.justHovered) {
			CardCrawlGame.sound.play("UI_HOVER");
		}
		if (InputHelper.justClickedLeft && this.noHb.hovered) {
			CardCrawlGame.sound.play("UI_CLICK_1");
			this.noHb.clickStarted = true;
		}
		if (this.noHb.clicked) {
			this.noHb.clicked = false;
			this.cancel();
		}
	}

	public void show(ModPanel panel, final String curName, String defaultValue, String explanationText, Consumer<ModTextPanel> cancel,
			Consumer<ModTextPanel> confirm) {
		this.panel = panel;
		panel.isUp = false;
		this.oldInputProcessor = Gdx.input.getInputProcessor();
		Gdx.input.setInputProcessor(new ModTextPanelInputHelper());
		System.out.println("setting new input processor");
		if (this.yesHb == null) {
			this.yesHb = new Hitbox(160.0f * Settings.scale, 70.0f * Settings.scale);
		}
		if (this.noHb == null) {
			this.noHb = new Hitbox(160.0f * Settings.scale, 70.0f * Settings.scale);
		}
		this.yesHb.move(860.0f * Settings.scale, Settings.OPTION_Y - 118.0f * Settings.scale);
		this.noHb.move(1062.0f * Settings.scale, Settings.OPTION_Y - 118.0f * Settings.scale);
		this.shown = true;
		this.animTimer = ANIM_TIME;
		ModTextPanel.textField = curName;
		this.prevName = curName;
		this.cancel = cancel;
		this.confirm = confirm;
		this.defaultName = defaultValue;
		this.explanationText = explanationText;
	}

	private void removeListeners() {
		this.confirm = null;
		this.cancel = null;
	}
	
	private void resetToSettings() {
		this.panel.isUp = true;
		CardCrawlGame.mainMenuScreen.darken();
        CardCrawlGame.mainMenuScreen.hideMenuButtons();
        CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.SETTINGS;
        CardCrawlGame.cancelButton.show("Close");
	}

	public void confirm() {
		ModTextPanel.textField = ModTextPanel.textField.trim();
		if (ModTextPanel.textField.equals("")) {
			ModTextPanel.textField = defaultName;
		}
		this.confirm.accept(this);
		removeListeners();
		resetToSettings();
		this.yesHb.move(-1000.0f, -1000.0f);
		this.noHb.move(-1000.0f, -1000.0f);
		this.shown = false;
		this.animTimer = ANIM_TIME;
		Gdx.input.setInputProcessor(this.oldInputProcessor);
	}

	public void cancel() {
		ModTextPanel.textField = this.prevName;
		if (ModTextPanel.textField.equals("")) {
			ModTextPanel.textField = defaultName;
		}
		this.cancel.accept(this);
		removeListeners();
		resetToSettings();
		this.yesHb.move(-1000.0f, -1000.0f);
		this.noHb.move(-1000.0f, -1000.0f);
		this.shown = false;
		this.animTimer = ANIM_TIME;
		Gdx.input.setInputProcessor(this.oldInputProcessor);
	}

	public void receiveRender(final SpriteBatch sb) {
		if (!this.shown)
			return;

		sb.setColor(this.screenColor);
		sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0f, 0.0f, Settings.WIDTH, Settings.HEIGHT);
		sb.setColor(this.uiColor);
		sb.draw(ImageMaster.OPTION_CONFIRM, Settings.WIDTH / 2.0f - 180.0f, Settings.OPTION_Y - 207.0f, 180.0f, 207.0f,
				CONFIRM_W, CONFIRM_H, Settings.scale, Settings.scale, 0.0f, 0, 0, 360, 414, false, false);
		sb.draw(ImageMaster.RENAME_BOX, Settings.WIDTH / 2.0f - 160.0f, Settings.OPTION_Y - 160.0f, 160.0f, 160.0f,
				320.0f, 320.0f, Settings.scale, Settings.scale, 0.0f, 0, 0, 320, 320, false, false);
		FontHelper.renderSmartText(sb, FontHelper.cardTitleFont, ModTextPanel.textField,
				Settings.WIDTH / 2.0f - 120.0f * Settings.scale, Settings.OPTION_Y + 4.0f * Settings.scale, 100000.0f,
				0.0f, this.uiColor, 0.85f);
		final float tmpAlpha = (MathUtils.cosDeg(System.currentTimeMillis() / 3L % 360L) + 1.25f) / 3.0f
				* this.uiColor.a;
		FontHelper.renderSmartText(sb, FontHelper.cardTitleFont, "_",
				Settings.WIDTH / 2.0f - 122.0f * Settings.scale
						+ FontHelper.getSmartWidth(FontHelper.cardTitleFont, ModTextPanel.textField, 1000000.0f,
								0.0f, 0.85f),
				Settings.OPTION_Y + 4.0f * Settings.scale, 100000.0f, 0.0f, new Color(1.0f, 1.0f, 1.0f, tmpAlpha), 0.85f);
		Color c = Settings.GOLD_COLOR.cpy();
		c.a = this.uiColor.a;
		FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, explanationText, Settings.WIDTH / 2.0f,
				Settings.OPTION_Y + 126.0f * Settings.scale, c);
		if (this.yesHb.clickStarted) {
			sb.setColor(new Color(1.0f, 1.0f, 1.0f, this.uiColor.a * 0.9f));
			sb.draw(ImageMaster.OPTION_YES, Settings.WIDTH / 2.0f - 86.5f - 100.0f * Settings.scale,
					Settings.OPTION_Y - 37.0f - 120.0f * Settings.scale, 86.5f, 37.0f, YES_W, BUTTON_H, Settings.scale,
					Settings.scale, 0.0f, 0, 0, 173, 74, false, false);
			sb.setColor(new Color(this.uiColor));
		} else {
			sb.draw(ImageMaster.OPTION_YES, Settings.WIDTH / 2.0f - 86.5f - 100.0f * Settings.scale,
					Settings.OPTION_Y - 37.0f - 120.0f * Settings.scale, 86.5f, 37.0f, YES_W, BUTTON_H, Settings.scale,
					Settings.scale, 0.0f, 0, 0, 173, 74, false, false);
		}
		if (!this.yesHb.clickStarted && this.yesHb.hovered) {
			sb.setColor(new Color(1.0f, 1.0f, 1.0f, this.uiColor.a * ANIM_TIME));
			sb.setBlendFunction(770, 1);
			sb.draw(ImageMaster.OPTION_YES, Settings.WIDTH / 2.0f - 86.5f - 100.0f * Settings.scale,
					Settings.OPTION_Y - 37.0f - 120.0f * Settings.scale, 86.5f, 37.0f, YES_W, BUTTON_H, Settings.scale,
					Settings.scale, 0.0f, 0, 0, 173, 74, false, false);
			sb.setBlendFunction(770, 771);
			sb.setColor(this.uiColor);
		}
		if (this.yesHb.clickStarted) {
			c = Color.LIGHT_GRAY.cpy();
		} else if (this.yesHb.hovered) {
			c = Settings.CREAM_COLOR.cpy();
		} else {
			c = Settings.GOLD_COLOR.cpy();
		}
		c.a = this.uiColor.a;
		FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, CONFIRM_TEXT,
				Settings.WIDTH / 2.0f - 110.0f * Settings.scale, Settings.OPTION_Y - 118.0f * Settings.scale, c, 0.85f);
		sb.draw(ImageMaster.OPTION_NO, Settings.WIDTH / 2.0f - 80.5f + 106.0f * Settings.scale,
				Settings.OPTION_Y - 37.0f - 120.0f * Settings.scale, 80.5f, 37.0f, NO_W, BUTTON_H, Settings.scale,
				Settings.scale, 0.0f, 0, 0, 161, 74, false, false);
		if (!this.noHb.clickStarted && this.noHb.hovered) {
			sb.setColor(new Color(1.0f, 1.0f, 1.0f, this.uiColor.a * ANIM_TIME));
			sb.setBlendFunction(770, 1);
			sb.draw(ImageMaster.OPTION_NO, Settings.WIDTH / 2.0f - 80.5f + 106.0f * Settings.scale,
					Settings.OPTION_Y - 37.0f - 120.0f * Settings.scale, 80.5f, 37.0f, NO_W, BUTTON_H, Settings.scale,
					Settings.scale, 0.0f, 0, 0, 161, 74, false, false);
			sb.setBlendFunction(770, 771);
			sb.setColor(this.uiColor);
		}
		if (this.noHb.clickStarted) {
			c = Color.LIGHT_GRAY.cpy();
		} else if (this.noHb.hovered) {
			c = Settings.CREAM_COLOR.cpy();
		} else {
			c = Settings.GOLD_COLOR.cpy();
		}
		c.a = this.uiColor.a;
		FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, CANCEL_TEXT,
				Settings.WIDTH / 2.0f + 110.0f * Settings.scale, Settings.OPTION_Y - 118.0f * Settings.scale, c, 0.85f);
		if (this.shown) {
			this.yesHb.render(sb);
			this.noHb.render(sb);
		}
	}
}

class ModTextPanelInputHelper implements InputProcessor {

	@Override
	public boolean keyDown(int keycode) {
		System.out.println("keydown and key was " + keycode);
		String tmp = Input.Keys.toString(keycode);
		if ((tmp.equals("Space")) && (tmp.length() != 0)) {
			ModTextPanel.textField += ' ';
			return false;
		}
		if (tmp.length() != 1)
			return false;
		if (FontHelper.getSmartWidth(FontHelper.cardTitleFont, ModTextPanel.textField, 1.0E7F, 0.0F, 0.85f) >= 240.0F
				* Settings.scale) {

			return false;
		}

		if ((!Gdx.input.isKeyPressed(59)) && (!Gdx.input.isKeyPressed(60))) {
			tmp = tmp.toLowerCase();
		}

		char tmp2 = tmp.charAt(0);
		if ((Character.isDigit(tmp2)) || (Character.isLetter(tmp2))) {
			ModTextPanel.textField += tmp2;
		}
		return true;
	}

	@Override
	public boolean keyTyped(char arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

}