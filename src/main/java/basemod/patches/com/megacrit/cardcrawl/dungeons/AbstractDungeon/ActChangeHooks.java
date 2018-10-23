package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import java.util.ArrayList;

public class ActChangeHooks {

	@SpirePatch(
			clz=AbstractDungeon.class,
			method=SpirePatch.CONSTRUCTOR,
			paramtypez={
					String.class,
					String.class,
					AbstractPlayer.class,
					ArrayList.class
			}
	)
	public static class InGameConstructor {
		
		public static void Postfix(AbstractDungeon __instance,
								   String name, String levelId, AbstractPlayer p, ArrayList<String> newSpecialOneTimeEventList) {
			if (levelId.equals(Exordium.ID) && AbstractDungeon.floorNum == 0) {
				BaseMod.publishStartGame();
			}
			BaseMod.publishStartAct();
		}
		
	}
	
	@SpirePatch(
			clz=AbstractDungeon.class,
			method=SpirePatch.CONSTRUCTOR,
			paramtypez={
					String.class,
					AbstractPlayer.class,
					SaveFile.class
			}
	)
	public static class SavedGameConstructor {

		public static void Postfix(Object __obj_instance,
				String name, AbstractPlayer p, SaveFile saveFile) {
			BaseMod.publishStartGame();
		}
		
	}
	
}
