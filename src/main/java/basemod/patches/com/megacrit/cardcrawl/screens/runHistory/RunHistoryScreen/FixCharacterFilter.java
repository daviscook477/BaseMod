package basemod.patches.com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;
import com.megacrit.cardcrawl.screens.stats.RunData;

import basemod.BaseMod;

public class FixCharacterFilter {
	public static final Logger logger = LogManager.getLogger(BaseMod.class.getName());
	
	private static final int IRONCLAD_NAME = 0;
	private static final int SILENT_NAME = 1;
	private static final int DEFECT_NAME = 2;
	private static final int ALL_CHARACTERS_TEXT = 23;
	
	@SpirePatch(cls="com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen", method="refreshData")
	public static class RefreshData {
		
		public static void Postfix(Object __obj_instance) {
			RunHistoryScreen screen = (RunHistoryScreen) __obj_instance;
			String[] text = RunHistoryScreen.TEXT;
			
			ArrayList<String> charFilterOptions = new ArrayList<>();
			charFilterOptions.add(text[ALL_CHARACTERS_TEXT]);
			charFilterOptions.add(text[IRONCLAD_NAME]);
			charFilterOptions.add(text[SILENT_NAME]);
			charFilterOptions.add(text[DEFECT_NAME]);
			for (String name : BaseMod.playerSelectTextMap.values()) {
				charFilterOptions.add(name);
			}
			String[] optionsAsArray = new String[charFilterOptions.size()];
			for (int i = 0; i < charFilterOptions.size(); i++) {
				optionsAsArray[i] = charFilterOptions.get(i);
			}
			
			try {
				Field characterFilterField = RunHistoryScreen.class.getDeclaredField("characterFilter");
				characterFilterField.setAccessible(true);
				characterFilterField.set(screen, new DropdownMenu(screen, optionsAsArray, FontHelper.cardDescFont_N, Settings.CREAM_COLOR));
				
				Method resetRunsDropdown = RunHistoryScreen.class.getDeclaredMethod("resetRunsDropdown");
				resetRunsDropdown.setAccessible(true);
				resetRunsDropdown.invoke(screen);
			} catch (Exception e) {
				logger.error("could not fix character filter for run history screen");
			}
		}
		
	}
	
	@SpirePatch(cls="com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen", method="characterText")
	public static class CharacterText {
		
		public static String Postfix(String __result, Object __obj_instance, String chosenCharacter) {
			String[] text = RunHistoryScreen.TEXT;
			
			if (!__result.equals(text[IRONCLAD_NAME])
					&& !__result.equals(text[SILENT_NAME])
					&& !__result.equals(text[DEFECT_NAME])) {
				String possibleReturn = BaseMod.playerSelectTextMap.get(__result);
				if (possibleReturn != null) {
					return possibleReturn;
				}
			}
			
			return __result;
		}
		
	}
	
	@SpirePatch(cls="com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen", method="resetRunsDropdown")
	public static class ResetRunsDropdown {
		
		@SpireInsertPatch(rloc=31, localvars={"includeMe", "data"})
		public static void Insert(Object __obj_instance, @ByRef boolean[] includeMe, RunData data) {
			RunHistoryScreen screen = (RunHistoryScreen) __obj_instance;
			
			try {
				Field characterFilterField = RunHistoryScreen.class.getDeclaredField("characterFilter");
				characterFilterField.setAccessible(true);
			
				int selectedIndex = ((DropdownMenu) characterFilterField.get(screen)).getSelectedIndex();
				int index = 4; // start at index 4 b/c 0,1,2,3 are used by base game
				if (selectedIndex < index) return; // don't need to filter if base game handled the filter
				
				String compareTo = null;
				for (String name : BaseMod.playerSelectTextMap.keySet()) {
					if (selectedIndex == index) {
						compareTo = name;
						break;
					}
					index++;
				}
				if (compareTo != null) {
					String runCharacter = data.character_chosen;
					includeMe[0] = includeMe[0] && (runCharacter.equals(compareTo));
				}
			} catch (Exception e) {
				logger.error("unable to filter characters on run history screen");
			}
		}
	}
	
}
