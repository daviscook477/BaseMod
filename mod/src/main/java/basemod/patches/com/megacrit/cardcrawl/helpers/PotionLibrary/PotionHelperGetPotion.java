package basemod.patches.com.megacrit.cardcrawl.helpers.PotionLibrary;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpirePatch2(
        clz = PotionHelper.class,
        method = "getPotion"
)
public class PotionHelperGetPotion {
    private static final Logger logger = LogManager.getLogger(PotionHelperGetPotion.class.getName());

    @SpireInsertPatch(locator = Locator.class)
    public static SpireReturn<?> patch(String name) {
        //If this gets called, no potion has been returned yet
		Class<? extends AbstractPotion> possiblePotion = BaseMod.getPotionClass(name);
		if(possiblePotion != null) {
			logger.info("Getting custom potion: " + name);
			try {
				return SpireReturn.Return(possiblePotion.newInstance());
			} catch (Exception e) {
				logger.catching(e);
			}
		}

        return SpireReturn.Continue();
    }

	//Inserts before the basegame missing potion key logging
	public static class Locator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctBehavior) throws Exception {
			Matcher matcher = new Matcher.MethodCallMatcher(Logger.class, "info");
			return LineFinder.findInOrder(ctBehavior, matcher);
		}
	}
}