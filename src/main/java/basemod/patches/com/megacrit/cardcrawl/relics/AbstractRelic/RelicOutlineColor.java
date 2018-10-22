package basemod.patches.com.megacrit.cardcrawl.relics.AbstractRelic;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.HashMap;
import java.util.Map;


public class RelicOutlineColor
{
	@SpirePatch(
			cls="com.megacrit.cardcrawl.relics.AbstractRelic",
			method="render",
			paramtypes={
					"com.badlogic.gdx.graphics.g2d.SpriteBatch",
					"boolean",
					"com.badlogic.gdx.graphics.Color"
			}
	)
	public static class Normal
	{
		public static void Prefix(AbstractRelic __instance, SpriteBatch sb, boolean renderAmount, @ByRef Color[] outlineColor)
		{
			for (Map.Entry<AbstractCard.CardColor, HashMap<String, AbstractRelic>> map : BaseMod.getAllCustomRelics().entrySet()) {
				if (map.getValue().containsKey(__instance.relicId)) {
					outlineColor[0] = BaseMod.getFrameOutlineColor(map.getKey());
				}
			}
		}
	}

	@SpirePatch(
			cls="com.megacrit.cardcrawl.relics.AbstractRelic",
			method="renderLock",
			paramtypes={
					"com.badlogic.gdx.graphics.g2d.SpriteBatch",
					"com.badlogic.gdx.graphics.Color"
			}
	)
	public static class Locked
	{
		public static void Prefix(AbstractRelic __instance, SpriteBatch sb, @ByRef Color[] outlineColor)
		{
			for (Map.Entry<AbstractCard.CardColor, HashMap<String, AbstractRelic>> map : BaseMod.getAllCustomRelics().entrySet()) {
				if (map.getValue().containsKey(__instance.relicId)) {
					outlineColor[0] = BaseMod.getFrameOutlineColor(map.getKey());
				}
			}
		}
	}
}
