package basemod.patches.com.megacrit.cardcrawl.screens.runHistory.TinyCard;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.runHistory.TinyCard;

public class ColorPatch
{
	@SpirePatch(
			clz = TinyCard.class,
			method = "getIconBackgroundColor"
	)
	public static class BackgroundColor
	{
		public static Color Postfix(Color __result, TinyCard __instance, AbstractCard card)
		{
			if (!BaseMod.isBaseGameCardColor(card.color)) {
				Color c = BaseMod.getBackColor(card.color);
				if (c != null) {
					return c;
				}
			}
			return __result;
		}
	}

	@SpirePatch(
			clz = TinyCard.class,
			method = "getIconDescriptionColor"
	)
	public static class DescriptionColor
	{
		public static Color Postfix(Color __result, TinyCard __instance, AbstractCard card)
		{
			if (!BaseMod.isBaseGameCardColor(card.color)) {
				Color c = BaseMod.getBackColor(card.color);
				if (c != null) {
					return c.cpy().lerp(Color.DARK_GRAY, 0.7f);
				}
			}
			return __result;
		}
	}
}
