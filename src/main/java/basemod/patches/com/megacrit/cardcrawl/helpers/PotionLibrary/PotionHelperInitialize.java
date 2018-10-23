package basemod.patches.com.megacrit.cardcrawl.helpers.PotionLibrary;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpirePatch(
		cls="com.megacrit.cardcrawl.helpers.PotionHelper",
		method="initialize"
)
public class PotionHelperInitialize
{
	private static final Logger logger = LogManager.getLogger(PotionHelperInitialize.class.getName());

	public static void Postfix(AbstractPlayer.PlayerClass chosenClass)
	{
		for (String potionID : BaseMod.getPotionIDs()) {
			AbstractPlayer.PlayerClass potionClass = BaseMod.getPotionPlayerClass(potionID);
			if (potionClass == null || potionClass == chosenClass) {
				logger.info("Adding " + potionID);
				PotionHelper.potions.add(potionID);
			}
		}
	}
} 