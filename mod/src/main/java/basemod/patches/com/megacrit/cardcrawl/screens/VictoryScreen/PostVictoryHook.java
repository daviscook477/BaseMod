package basemod.patches.com.megacrit.cardcrawl.screens.VictoryScreen;

import basemod.BaseMod;
import basemod.patches.com.megacrit.cardcrawl.screens.DeathScreen.PostVictoryEvent;
import basemod6.BaseMod6;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cutscenes.Cutscene;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.VictoryScreen;

@SpirePatch(
		clz= VictoryScreen.class,
		method=SpirePatch.CONSTRUCTOR
)
public class PostVictoryHook
{
	private static boolean doHook = true;

	public static void Postfix(VictoryScreen __instance, Object __monster_group)
	{
		if (!doHook) {
			return;
		}

		BaseMod6.EVENT_BUS.post(new PostVictoryEvent());
		BaseMod.publishPostDeath();
	}

	@SpirePatch(
			clz= Cutscene.class,
			method="openVictoryScreen"
	)
	public static class StopMultiVictory
	{
		public static void Prefix(Cutscene __instance)
		{
			doHook = (AbstractDungeon.victoryScreen == null);
		}
	}
}
