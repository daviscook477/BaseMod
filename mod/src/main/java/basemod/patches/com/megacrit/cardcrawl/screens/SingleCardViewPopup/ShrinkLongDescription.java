package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CtBehavior;

public class ShrinkLongDescription
{
	@SpirePatch(
			clz=SingleCardViewPopup.class,
			method="renderDescription"
	)
	@SpirePatch(
			clz=SingleCardViewPopup.class,
			method="renderDescriptionCN"
	)
	public static class ShiftSizeLineDescription
	{
		@SpireInsertPatch(
				locator=Locator.class,
				localvars={"card", "draw_y", "font", "current_x"}
		)
		public static void Insert(SingleCardViewPopup __instance, SpriteBatch sb,
								  AbstractCard card, @ByRef float[] draw_y, @ByRef BitmapFont[] font,
								  @ByRef float[] current_x)
		{
			current_x[0] = Settings.WIDTH / 2.0f + 6.0f * Settings.scale;

			if (card.description.size() > 5) {
				draw_y[0] -= 18.0f * Settings.scale * card.drawScale;
			}

			float scale = basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.ShrinkLongDescription.Scale.descriptionScale.get(card);
			if (scale != 1.0f) {
				font[0].getData().setScale(font[0].getScaleX() * scale, font[0].getScaleY() * scale);
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
