package basemod.patches.com.megacrit.cardcrawl.screens.options.SettingsScreen;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.options.SettingsScreen;

@SpirePatch(cls="com.megacrit.cardcrawl.screens.options.SettingsScreen", method="popup")
public class PopupSwitch {
	@SpireInsertPatch(rloc=35)
	public static void Insert(Object __obj_instance, Object typeObj) {
		// Probably not in a game
		if (AbstractDungeon.player == null) {
			return;
		}

		SettingsScreen screen = (SettingsScreen) __obj_instance;
		AbstractPlayer.PlayerClass selection = AbstractDungeon.player.chosenClass;
		if (!BaseMod.isBaseGameCharacter(selection)) {
			System.out.println("looking for file: " + (BaseMod.save_path + selection.name() + ".autosave"));
			if (!Gdx.files.local(BaseMod.save_path + selection.name() + ".autosave").exists()) {
				screen.exitPopup.desc = SettingsScreen.TEXT[3];
			}
		}
	}
}
