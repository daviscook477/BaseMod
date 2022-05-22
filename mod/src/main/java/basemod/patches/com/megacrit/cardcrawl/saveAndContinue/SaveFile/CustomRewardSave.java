package basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveFile;


import basemod.BaseMod;
import basemod.abstracts.CustomReward;
import com.evacipated.cardcrawl.modthespire.lib.*;

import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import javassist.CtBehavior;

@SuppressWarnings("unused")
@SpirePatch(
		clz = SaveFile.class,
		method = SpirePatch.CONSTRUCTOR,
		paramtypez = {SaveFile.SaveType.class}
)
public class CustomRewardSave
{
	@SpireInsertPatch(
			locator = Locator.class,
			localvars = { "i" }
	)
	public static void Insert(SaveFile __instance, SaveFile.SaveType saveType, RewardItem i)
	{
		if (i instanceof CustomReward) {
			CustomReward customReward = (CustomReward) i;
			if (BaseMod.customRewardTypeExists(customReward.type)) {
				__instance.combat_rewards.add(BaseMod.saveCustomReward(customReward));
			}
		}
	}

	public static class Locator extends SpireInsertLocator
	{
		@Override
		public int[] Locate(CtBehavior ctBehavior) throws Exception
		{
			Matcher matcher = new Matcher.FieldAccessMatcher(RewardItem.class, "type");
			return LineFinder.findInOrder(ctBehavior, matcher);
		}
	}
}
