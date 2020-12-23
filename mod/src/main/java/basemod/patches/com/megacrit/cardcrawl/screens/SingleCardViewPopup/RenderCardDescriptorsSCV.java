package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import basemod.abstracts.CustomCard;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.RenderCardDescriptors;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.List;

public class RenderCardDescriptorsSCV
{
	@SpirePatch(
			clz = SingleCardViewPopup.class,
			method = "renderCardTypeText"
	)
	public static class Text
	{
		@SpireInsertPatch(
				locator = Locator.class,
				localvars = {"label"}
		)
		public static void Insert(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card, @ByRef String[] text)
		{
			RenderCardDescriptors.Text.Insert(___card, sb, text);
		}

		private static class Locator extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws Exception
			{
				Matcher matcher = new Matcher.MethodCallMatcher(FontHelper.class, "renderFontCentered");
				return LineFinder.findInOrder(ctBehavior, matcher);
			}
		}
	}
	@SpirePatch(
			clz = SingleCardViewPopup.class,
			method = "renderFrame"
	)
	public static class Frame
	{
		@SpireInsertPatch(
				locator = Locator.class,
				localvars = {"tOffset", "tWidth"}
		)
		public static void Insert(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card, @ByRef float[] tOffset, @ByRef float[] tWidth)
		{
			//RenderCardDescriptors.Frame.Insert(___card, sb, 0, 0, tOffset, tWidth);
			if (___card instanceof CustomCard) {
				String typeText;
				switch (___card.type) {
					case ATTACK:
						typeText = AbstractCard.TEXT[0];
						break;
					case SKILL:
						typeText = AbstractCard.TEXT[1];
						break;
					case POWER:
						typeText = AbstractCard.TEXT[2];
						break;
					case STATUS:
						typeText = AbstractCard.TEXT[7];
						break;
					case CURSE:
						typeText = AbstractCard.TEXT[3];
						break;
					default:
						typeText = AbstractCard.TEXT[5];
						break;
				}
				List<String> descriptors = new ArrayList<>();
				descriptors.add(typeText);
				descriptors.addAll(((CustomCard) ___card).getCardDescriptors());
				if (descriptors.size() > 1) {
					String text = String.join(RenderCardDescriptors.SEPARATOR, descriptors);
					GlyphLayout gl = new GlyphLayout();
					FontHelper.panelNameFont.getData().setScale(1f);
					gl.setText(FontHelper.panelNameFont, text);
					tOffset[0] = (gl.width - 70 * Settings.scale) / 2f;
					tWidth[0] = (gl.width - 0f) / (62 * Settings.scale);
				}
			}
		}

		private static class Locator extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws Exception
			{
				Matcher matcher = new Matcher.MethodCallMatcher(SingleCardViewPopup.class, "renderDynamicFrame");
				return LineFinder.findInOrder(ctBehavior, matcher);
			}
		}
	}

	@SpirePatch(
			clz = SingleCardViewPopup.class,
			method = "dynamicFrameRenderHelper",
			paramtypez = {
					SpriteBatch.class,
					TextureAtlas.AtlasRegion.class,
					float.class,
					float.class
			}
	)
	public static class FixDynamicFrame
	{
		private static final Vector2 tmp = new Vector2(0, 0);

		public static SpireReturn<Void> Prefix(SingleCardViewPopup __instance, SpriteBatch sb, TextureAtlas.AtlasRegion img, float xOffset, float xScale, AbstractCard ___card)
		{
			if (___card instanceof CustomCard) {
				tmp.set(xOffset, 0);
				sb.draw(
						img,
						Settings.WIDTH / 2f + img.offsetX - (img.originalWidth / 2f + 2) + tmp.x,
						Settings.HEIGHT / 2f + img.offsetY - img.originalHeight / 2f + tmp.y,
						img.originalWidth / 2f - img.offsetX + 2,
						img.originalHeight / 2f - img.offsetY,
						img.packedWidth,
						img.packedHeight,
						Settings.scale * xScale,
						Settings.scale,
						0f
				);
				return SpireReturn.Return(null);
			}
			return SpireReturn.Continue();
		}
	}
}
