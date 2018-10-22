package basemod.patches.com.megacrit.cardcrawl.monsters.MonsterInfo;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.MonsterInfo;

import java.util.ArrayList;

@SpirePatch(
		clz=MonsterInfo.class,
		method="normalizeWeights"
)
public class AddCustomMonsters
{
	private static int calls = 0;

	public static void Prefix(@ByRef ArrayList<MonsterInfo>[] monsters)
	{
		++calls;

		switch (calls) {
			case 1: // Normal monsters
				monsters[0].addAll(BaseMod.getMonsterEncounters(AbstractDungeon.id));
				break;
			case 2: // Strong monsters
				monsters[0].addAll(BaseMod.getStrongMonsterEncounters(AbstractDungeon.id));
				break;
			case 3: // Elites
				monsters[0].addAll(BaseMod.getEliteEncounters(AbstractDungeon.id));
			default:
				calls = 0;
				break;
		}
	}
}
