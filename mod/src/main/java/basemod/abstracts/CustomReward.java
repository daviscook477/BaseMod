package basemod.abstracts;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.RewardGlowEffect;

import java.util.ArrayList;

public abstract class CustomReward extends RewardItem
{
	public Texture icon;
	public ArrayList<AbstractGameEffect> effects;

	public CustomReward(Texture icon, String text, RewardItem.RewardType type)
	{
		this.icon = icon;
		this.text = text;
		this.type = type;
		this.effects = new ArrayList<>();
		if (!BaseMod.customRewardTypeExists(type)) {
			BaseMod.logger.info("CUSTOM REWARD WILL NOT BE SAVED PROPERLY WITHOUT BEING REGISTERED");
		}
	}

	public abstract boolean claimReward();

	@Override
	public void update()
	{
		if (this.flashTimer > 0.0f) {
			this.flashTimer -= Gdx.graphics.getDeltaTime();
			if (this.flashTimer < 0.0f) {
				this.flashTimer = 0.0f;
			}
		}

		this.hb.update();

		if (this.effects.size() == 0) {
			this.effects.add(new RewardGlowEffect(this.hb.cX, this.hb.cY));
		}

		for (AbstractGameEffect effect : effects) {
			effect.update();
		}

		effects.removeIf((effect) -> effect.isDone);

		if (this.hb.justHovered) {
			CardCrawlGame.sound.play("UI_HOVER");
		}

		if (this.hb.hovered && InputHelper.justClickedLeft && !this.isDone) {
			CardCrawlGame.sound.playA("UI_CLICK_1", 0.1f);
			this.hb.clickStarted = true;
		}

		if (this.hb.hovered && CInputActionSet.select.isJustPressed() && !this.isDone) {
			this.hb.clicked = true;
			CardCrawlGame.sound.playA("UI_CLICK_1", 0.1f);
		}

		if (this.hb.clicked) {
			this.hb.clicked = false;
			this.isDone = true;
		}
	}

	@Override
	public void render(SpriteBatch sb)
	{
		if (this.hb.hovered) {
			sb.setColor(new Color(0.4f, 0.6f, 0.6f, 1.0f));
		} else {
			sb.setColor(new Color(0.5f, 0.6f, 0.6f, 0.8f));
		}

		if (this.hb.clickStarted) {
			sb.draw(ImageMaster.REWARD_SCREEN_ITEM, Settings.WIDTH / 2.0f - 232.0f, this.y - 49.0f, 232.0f, 49.0f, 464.0f, 98.0f, Settings.xScale * 0.98f, Settings.scale * 0.98f, 0.0f, 0, 0, 464, 98, false, false);
		} else {
			sb.draw(ImageMaster.REWARD_SCREEN_ITEM, Settings.WIDTH / 2.0f - 232.0f, this.y - 49.0f, 232.0f, 49.0f, 464.0f, 98.0f, Settings.xScale, Settings.scale, 0.0f, 0, 0, 464, 98, false, false);
		}

		if (this.flashTimer != 0.0f) {
			sb.setColor(0.6f, 1.0f, 1.0f, this.flashTimer * 1.5f);
			sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
			sb.draw(ImageMaster.REWARD_SCREEN_ITEM, Settings.WIDTH / 2.0f - 232.0f, this.y - 49.0f, 232.0f, 49.0f, 464.0f, 98.0f, Settings.scale * 1.03f, Settings.scale * 1.15f, 0.0f, 0, 0, 464, 98, false, false);
			sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		}

		sb.setColor(Color.WHITE.cpy());

		sb.draw(this.icon, RewardItem.REWARD_ITEM_X - 32.0f, this.y - 32.0f - 2.0f * Settings.scale, 32.0f, 32.0f, 64.0f, 64.0f, Settings.scale, Settings.scale, 0.0f, 0, 0, 64, 64, false, false);

		Color c = Settings.CREAM_COLOR.cpy();
		if (this.hb.hovered) {
			c = Settings.GOLD_COLOR.cpy();
		}

		FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, this.text, Settings.WIDTH * 0.434F, this.y + 5.0f * Settings.scale, 1000.0f * Settings.scale, 0.0f, c);

		if (!this.hb.hovered) {
			for (AbstractGameEffect e : this.effects) {
				e.render(sb);
			}
		}

		this.hb.render(sb);
	}
}
