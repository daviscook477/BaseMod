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
				for (MonsterInfo i : BaseMod.getMonsterEncounters(AbstractDungeon.id)) {
					monsters[0].add(new MonsterInfo(i.name, i.weight));
				}
				break;
			case 2: // Strong monsters
				for (MonsterInfo i : BaseMod.getStrongMonsterEncounters(AbstractDungeon.id)) {
					monsters[0].add(new MonsterInfo(i.name, i.weight));
				}
				break;
			case 3: // Elites
				for (MonsterInfo i : BaseMod.getEliteEncounters(AbstractDungeon.id)) {
					monsters[0].add(new MonsterInfo(i.name, i.weight));
				}
				break;
			default:
				calls = 0;
				break;
		}
	}
}
