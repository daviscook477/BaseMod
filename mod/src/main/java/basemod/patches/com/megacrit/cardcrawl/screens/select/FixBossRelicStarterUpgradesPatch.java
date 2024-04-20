package basemod.patches.com.megacrit.cardcrawl.screens.select;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.screens.select.BossRelicSelectScreen;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.HashMap;
import java.util.Map;

@SpirePatch2(clz = BossRelicSelectScreen.class, method = "relicObtainLogic")
public class FixBossRelicStarterUpgradesPatch {
	private static Map<String, String> starterMappings;
	static {
		starterMappings = new HashMap<>();
		starterMappings.put(BlackBlood.ID, BurningBlood.ID);
		starterMappings.put(RingOfTheSerpent.ID, SnakeRing.ID);
		starterMappings.put(FrozenCore.ID, CrackedCore.ID);
		starterMappings.put(HolyWater.ID, PureWater.ID);
	}

	@SpireInstrumentPatch
	public static ExprEditor ReplaceCorrectStarterRelic() {
		return new ExprEditor() {
			public void edit(MethodCall m) throws CannotCompileException
			{
				if (m.getMethodName().equals("instantObtain"))
					m.replace(
							"$2 = " + FixBossRelicStarterUpgradesPatch.class.getName() + ".getCorrectRelicLocation(r);" +
									"$_ = $proceed($$);"
					);
			}
		};
	}

	public static int getCorrectRelicLocation(AbstractRelic r) {
		if (!BaseMod.fixesEnabled) {
			return 0;
		}

		String startRelicId = starterMappings.getOrDefault(r.relicId, null);

		if (startRelicId == null) {
			return 0;
		}

		for (int i = 0; i < AbstractDungeon.player.relics.size(); i++) {
			if (startRelicId.equals(AbstractDungeon.player.relics.get(i).relicId)) {
				return i;
			}
		}

		return 0;
	}
}
