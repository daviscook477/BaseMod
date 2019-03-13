package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DescriptionLine;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import javassist.CtBehavior;

import java.util.List;


public class ShrinkLongDescription
{
	@SpirePatch(
			clz=AbstractCard.class,
			method=SpirePatch.CLASS
	)
	public static class Scale
	{
		public static SpireField<Float> descriptionScale = new SpireField<>(() -> 1.0f);
	}

	@SpirePatch(
			clz=AbstractCard.class,
			method="getDescFont"
	)
	public static class ShrinkFontSize
	{
		public static BitmapFont Postfix(BitmapFont __result, AbstractCard __instance)
		{
			float scale = Scale.descriptionScale.get(__instance);
			if (scale != 1.0f) {
				__result = FontHelper.cardDescFont_L;
				__result.getData().setScale(__result.getScaleX() * scale, __result.getScaleY() * scale);
			}

			return __result;
		}
	}

	@SpirePatch(
			clz=AbstractCard.class,
			method="initializeDescription"
	)
	@SpirePatch(
			clz=AbstractCard.class,
			method="initializeDescriptionCN"
	)
	public static class ShrinkInitializeDescription
	{
		private static final float TARGET_HEIGHT = 95.0f * Settings.scale;
		private static final int MAX_DEPTH = 10;
		private static int depth = 0;

		public static void Prefix(AbstractCard __instance)
		{
			if (depth == 0) {
				Scale.descriptionScale.set(__instance, 1.0f);
			}
		}

		public static void Postfix(AbstractCard __instance)
		{
			float descHeight = descriptionHeight(FontHelper.cardDescFont_N, __instance.description);
			if (__instance.description.size() > 6 && descHeight > TARGET_HEIGHT && depth < MAX_DEPTH) {
				++depth;
				FontHelper.cardDescFont_N.getData().setScale(1 - depth * 0.05f);
				Scale.descriptionScale.set(__instance, 1 - depth * 0.05f);
				__instance.initializeDescription();
				--depth;
				FontHelper.cardDescFont_N.getData().setScale(1.0f);
			} else if (depth >= MAX_DEPTH) {
			}
		}

		private static float descriptionHeight(BitmapFont font, List<DescriptionLine> description)
		{
			float height = 0;
			GlyphLayout gl = new GlyphLayout();
			for (int i=0; i<description.size(); ++i) {
				gl.setText(font, description.get(i).text);
				height += gl.height;
			}
			return height;
		}
	}

	@SpirePatch(
			clz=AbstractCard.class,
			method="renderDescription"
	)
	@SpirePatch(
			clz=AbstractCard.class,
			method="renderDescriptionCN"
	)
	public static class ShiftSizeLineDescription
	{
		@SpireInsertPatch(
				locator=Locator.class,
				localvars={"draw_y"}
		)
		public static void Insert(AbstractCard __instance, SpriteBatch sb, @ByRef float[] draw_y)
		{
			if (__instance.description.size() > 5) {
				draw_y[0] -= 6.0f * Settings.scale * __instance.drawScale;
			}
		}

		private static class Locator extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
			{
				Matcher finalMatcher = new Matcher.MethodCallMatcher(BitmapFont.class, "getCapHeight");
				return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
			}
		}
	}
}
