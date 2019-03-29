package basemod.abstracts;

import basemod.ReflectionHacks;
import basemod.helpers.AchievementJSON;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.stats.AchievementItem;

public class CustomAchievement extends AchievementItem
{
	public CustomAchievement(String key, String title, String desc, String imgUrl, String imgUrlLocked, boolean hidden)
	{
		super(title, desc, "", key, hidden);

		Texture tempImg;
		if (isUnlocked) {
			if (imgUrl == null) {
				imgUrl = "images/achievements/unlocked/0.jpg";
			}
			tempImg = ImageMaster.loadImage(imgUrl);
		} else {
			if (imgUrlLocked == null) {
				imgUrlLocked = "images/achievements/locked/0.jpg";
			}
			tempImg = ImageMaster.loadImage(imgUrlLocked);
		}
		if (tempImg != null) {
			ReflectionHacks.setPrivate(this, AchievementItem.class, "img", tempImg);
		}
	}

	public CustomAchievement(String key, String title, String desc, String imgUrl, String imgUrlLocked)
	{
		this(key, title, desc, imgUrl, imgUrlLocked, false);
	}

	public CustomAchievement(String key, AchievementJSON json, boolean hidden)
	{
		this(key, json.NAME, json.TEXT, json.IMG, json.IMG_LOCKED, hidden);
	}

	public CustomAchievement(String key, AchievementJSON json)
	{
		this(key, json, false);
	}
}
