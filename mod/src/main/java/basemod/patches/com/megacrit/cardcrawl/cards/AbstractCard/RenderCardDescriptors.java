package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.List;

public class RenderCardDescriptors
{
	public static final String SEPARATOR = " | ";

	@SpirePatch(
			clz = AbstractCard.class,
			method = "renderType"
	)
	public static class Text
	{
		@SpireInsertPatch(
				locator = Locator.class,
				localvars = {"text"}
		)
		public static void Insert(AbstractCard __instance, SpriteBatch sb, @ByRef String[] text)
		{
			if (__instance instanceof CustomCard) {
				List<String> descriptors = new ArrayList<>();
				descriptors.add(text[0]);
				descriptors.addAll(((CustomCard) __instance).getCardDescriptors());
				if (descriptors.size() > 1) {
					text[0] = String.join(SEPARATOR, descriptors);
				}
			}
		}

		private static class Locator extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws Exception
			{
				Matcher matcher = new Matcher.MethodCallMatcher(FontHelper.class, "renderRotatedText");
				return LineFinder.findInOrder(ctBehavior, matcher);
			}
		}
	}

	@SpirePatch(
			clz = AbstractCard.class,
			method = "renderPortraitFrame"
	)
	public static class Frame
	{
		@SpireInsertPatch(
				locator = Locator.class,
				localvars = {"tOffset", "tWidth"}
		)
		public static void Insert(AbstractCard __instance, SpriteBatch sb, float x, float y, @ByRef float[] tOffset, @ByRef float[] tWidth)
		{
			if (__instance instanceof CustomCard) {
				String typeText;
				switch (__instance.type) {
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
				descriptors.addAll(((CustomCard) __instance).getCardDescriptors());
				if (descriptors.size() > 1) {
					String text = String.join(SEPARATOR, descriptors);
					GlyphLayout gl = new GlyphLayout();
					FontHelper.cardTypeFont.getData().setScale(1f);
					gl.setText(FontHelper.cardTypeFont, text);
					tOffset[0] = (gl.width - 38 * Settings.scale) / 2f;
					tWidth[0] = (gl.width - 0f) / (32 * Settings.scale);
				}
			}
		}

		private static class Locator extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws Exception
			{
				Matcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "renderDynamicFrame");
				return LineFinder.findInOrder(ctBehavior, matcher);
			}
		}
	}

	@SpirePatch(
			clz = AbstractCard.class,
			method = "dynamicFrameRenderHelper",
			paramtypez = {
					SpriteBatch.class,
					TextureAtlas.AtlasRegion.class,
					float.class,
					float.class,
					float.class,
					float.class
			}
	)
	public static class FixDynamicFrame
	{
		private static final Vector2 tmp = new Vector2(0, 0);

		public static SpireReturn<Void> Prefix(AbstractCard __instance, SpriteBatch sb, TextureAtlas.AtlasRegion img, float x, float y, float xOffset, float xScale)
		{
			if (__instance instanceof CustomCard) {
				tmp.set(xOffset, 0);
				tmp.scl(__instance.drawScale);
				tmp.rotate(__instance.angle);
				sb.draw(
						img,
						x + img.offsetX - (img.originalWidth / 2f + 1) + tmp.x,
						y + img.offsetY - img.originalHeight / 2f + tmp.y,
						img.originalWidth / 2f - img.offsetX + 1,
						img.originalHeight / 2f - img.offsetY,
						img.packedWidth,
						img.packedHeight,
						Settings.scale * __instance.drawScale * xScale,
						Settings.scale * __instance.drawScale,
						__instance.angle
				);
				return SpireReturn.Return(null);
			}
			return SpireReturn.Continue();
		}
	}
}
