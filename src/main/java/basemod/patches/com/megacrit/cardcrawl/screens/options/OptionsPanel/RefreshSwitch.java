package basemod.patches.com.megacrit.cardcrawl.screens.options.OptionsPanel;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.options.ExitGameButton;
import com.megacrit.cardcrawl.screens.options.OptionsPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

@SpirePatch(cls="com.megacrit.cardcrawl.screens.options.OptionsPanel", method="refresh")
public class RefreshSwitch {
	public static final Logger logger = LogManager.getLogger(BaseMod.class.getName());
	
	public static void Postfix(Object __obj_instance) {
		OptionsPanel panel = (OptionsPanel) __obj_instance;
		if (!Settings.isDailyRun) {
			AbstractPlayer.PlayerClass selection = AbstractDungeon.player.chosenClass;
			if (!BaseMod.isBaseGameCharacter(selection)) {
				System.out.println("looking for file: " + (BaseMod.save_path + selection.name() + ".autosave"));
				if (!Gdx.files.local(BaseMod.save_path + selection.name() + ".autosave").exists()) {
					try {
						Field exitBtn = panel.getClass().getDeclaredField("exitBtn");
						exitBtn.setAccessible(true);
						ExitGameButton btn;
						btn = (ExitGameButton) exitBtn.get(panel);
						btn.updateLabel(OptionsPanel.TEXT[15]);
					} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
						logger.error("could not set confirm button for exiting as: " + selection.toString());
						logger.error("exception is: " + e.toString());
						e.printStackTrace();
					}

				}
			}
		}
	}
}
