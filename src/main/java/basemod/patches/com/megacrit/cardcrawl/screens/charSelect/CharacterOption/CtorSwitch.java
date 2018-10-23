package basemod.patches.com.megacrit.cardcrawl.screens.charSelect.CharacterOption;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

@SpirePatch(
		clz=CharacterOption.class,
		method=SpirePatch.CONSTRUCTOR,
		paramtypez={
				String.class,
				AbstractPlayer.class,
				String.class,
				String.class
		}
)
public class CtorSwitch {
	public static final Logger logger = LogManager.getLogger(BaseMod.class.getName());
	
	@SpireInsertPatch(rloc=20)
	public static void Insert(CharacterOption __instance, String optionName, AbstractPlayer c, String buttonUrl, String portraiImg) {
		if (!BaseMod.isBaseGameCharacter(c.chosenClass)) {
			try {
				// fix texture loading
				Field buttonImgField;
				buttonImgField = CharacterOption.class.getDeclaredField("buttonImg");
				buttonImgField.setAccessible(true);
				buttonImgField.set(__instance, ImageMaster.loadImage(BaseMod.getPlayerButton(c.chosenClass)));
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				logger.error("could not create character select button for " + c.chosenClass.toString());
				logger.error("with exception: " + e.getMessage());
				e.printStackTrace();
			}

		}
	}
}
