package basemod.patches.com.megacrit.cardcrawl.screens.SingleRelicViewPopup;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;

@SpirePatch(
		clz = SingleRelicViewPopup.class,
		method = "initializeLargeImg"
)
public class FixLargeRelicArt
{
	public static void Postfix(SingleRelicViewPopup __instance, AbstractRelic ___relic, @ByRef Texture[] ___largeImg)
	{
		if (___largeImg[0] == null) {
			if (___relic.largeImg != null) {
				TextureData textureData = ___relic.largeImg.getTextureData();
				if (textureData instanceof FileTextureData) {
					___largeImg[0] = ImageMaster.loadImage(((FileTextureData) textureData).getFileHandle().path());
				}
			}
			else if (___relic instanceof CustomRelic) {
				___relic.loadLargeImg();
				if (___relic.largeImg != null) {
					___largeImg[0] = ___relic.largeImg;
					___relic.largeImg = null;
				}
			}
		}
	}
}
