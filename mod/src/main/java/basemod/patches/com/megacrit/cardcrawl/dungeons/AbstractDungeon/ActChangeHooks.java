package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.StartActEvent;
import basemod6.events.StartGameEvent;
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
				BaseMod6.EVENT_BUS.post(new StartGameEvent());
				BaseMod.publishStartGame();
			}
			BaseMod6.EVENT_BUS.post(new StartActEvent());
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
			BaseMod6.EVENT_BUS.post(new StartGameEvent());
			BaseMod.publishStartGame();
		}
		
	}
	
}
