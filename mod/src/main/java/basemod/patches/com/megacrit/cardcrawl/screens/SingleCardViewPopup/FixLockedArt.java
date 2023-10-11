package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.helpers.ImageMaster;

@SpirePatch2(
		clz = ImageMaster.class,
		method = "initialize"
)
public class FixLockedArt
{
	public static void Postfix()
	{
		ImageMaster.CARD_LOCKED_ATTACK_L = toRegion(ImageMaster.loadImage("images/cards/locked_attack_l.png"));
		ImageMaster.CARD_LOCKED_SKILL_L = toRegion(ImageMaster.loadImage("images/cards/locked_skill_l.png"));
		ImageMaster.CARD_LOCKED_POWER_L = toRegion(ImageMaster.loadImage("images/cards/locked_power_l.png"));
	}

	private static TextureAtlas.AtlasRegion toRegion(Texture tex)
	{
		return new TextureAtlas.AtlasRegion(tex, 0, 0, tex.getWidth(), tex.getHeight());
	}
}
