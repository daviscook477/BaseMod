package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class TurnStartHooks
{
	@SpirePatch2(
			clz = AbstractPlayer.class,
			method = "applyStartOfTurnRelics"
	)
	public static class PreDraw
	{
		public static void Prefix()
		{
			BaseMod.publishOnPlayerTurnStart();
		}
	}

	@SpirePatch2(
			clz = AbstractPlayer.class,
			method = "applyStartOfTurnPostDrawRelics"
	)
	public static class PostDraw
	{
		public static void Prefix()
		{
			BaseMod.publishOnPlayerTurnStartPostDraw();
		}
	}
}
