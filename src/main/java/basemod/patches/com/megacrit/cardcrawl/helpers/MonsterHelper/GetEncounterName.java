package basemod.patches.com.megacrit.cardcrawl.helpers.MonsterHelper;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.MonsterHelper;

@SpirePatch(
		clz=MonsterHelper.class,
		method="getEncounterName"
)
public class GetEncounterName
{
	public static String Postfix(String __result, String key)
	{
		if (__result.isEmpty() && BaseMod.customMonsterExists(key)) {
			return BaseMod.getMonsterName(key);
		}
		return __result;
	}
}
