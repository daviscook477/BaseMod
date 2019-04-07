package basemod.patches.com.megacrit.cardcrawl.core.CardCrawlGame;

import basemod6.BaseMod6;
import basemod6.events.EditColorsEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;

@SpirePatch(
		clz=CardCrawlGame.class,
		method="create"
)
public class EditColorsHook
{
	public static void Prefix(CardCrawlGame __instance)
	{
		BaseMod6.EVENT_BUS.post(new EditColorsEvent());
	}
}
