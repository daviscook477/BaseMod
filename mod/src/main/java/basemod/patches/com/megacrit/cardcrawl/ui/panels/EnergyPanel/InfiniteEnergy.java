package basemod.patches.com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import basemod.DevConsole;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

@SpirePatch2(
		clz = EnergyPanel.class,
		method = "useEnergy"
)
public class InfiniteEnergy
{
	private static int saveTotalCount;

	public static void Prefix()
	{
		saveTotalCount = EnergyPanel.totalCount;
	}

	public static void Postfix()
	{
		if (DevConsole.infiniteEnergy) {
			EnergyPanel.totalCount = saveTotalCount;
		}
	}
}
