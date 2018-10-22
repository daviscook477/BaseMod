package basemod.patches.com.megacrit.cardcrawl.helpers.PotionLibrary;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.potions.FirePotion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpirePatch(
		cls="com.megacrit.cardcrawl.helpers.PotionHelper",
		method="getPotion"
)
public class PotionHelperGetPotion {
	private static final Logger logger = LogManager.getLogger(PotionHelperGetPotion.class.getName());

	public static Object Postfix(Object __result, String potionID) {
		if (__result == null) {
			return null;
		}
		if (__result instanceof FirePotion && !potionID.equals(FirePotion.POTION_ID)) {
			logger.info("Getting custom potion: " + potionID);
			try {
				Class potionClass = BaseMod.getPotionClass(potionID);
				return potionClass.newInstance();
			} catch (Exception e) {
				logger.warn(e.getMessage());
				return new FirePotion();
			}
		} else {
			return __result;
		}
	}
}