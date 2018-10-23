package basemod;

import basemod.interfaces.PostInitializeSubscriber;
import basemod.patches.whatmod.WhatMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;

/**
 *
 * Handles the creation of the ModBadge and settings panel for BaseMod
 *
 */
public class BaseModInit implements PostInitializeSubscriber {
	public static final String MODNAME = "BaseMod";
	public static final String AUTHOR = "t-larson, test447, FlipskiZ, Haashi, Blank The Evil, kiooeht, robojumper, Skrelpoid";
	public static final String DESCRIPTION = "Modding API and Dev console.";

	private InputProcessor oldInputProcessor;

	public static final float BUTTON_X = 350.0f;
	public static final float BUTTON_Y = 650.0f;
	public static final float BUTTON_LABEL_X = 475.0f;
	public static final float BUTTON_LABEL_Y = 700.0f;
	public static final float BUTTON_ENABLE_X = 350.0f;
	public static final float BUTTON_ENABLE_Y = 600.0f;
	public static final float AUTOCOMPLETE_BUTTON_ENABLE_X = 350.0f;
	public static final float AUTOCOMPLETE_BUTTON_ENABLE_Y = 550.0f;
	public static final float AUTOCOMPLETE_LABEL_X = 350.0f;
	public static final float AUTOCOMPLETE_LABEL_Y = 425.0f;
	private static final String AUTOCOMPLETE_INFO = "Press L_Shift + Up/Down to scroll through suggestions.\nPress Tab or Right to complete the current command.\nPress Left to delete the last token.";
	public static final float WHATMOD_BUTTON_X = 350.0f;
	public static final float WHATMOD_BUTTON_Y = 350.0f;
	
	@Override
	public void receivePostInitialize() {
		// BaseMod post initialize handling
		ModPanel settingsPanel = new ModPanel();

		ModLabel buttonLabel = new ModLabel("", BUTTON_LABEL_X, BUTTON_LABEL_Y, settingsPanel, (me) -> {
			if (me.parent.waitingOnEvent) {
				me.text = "Press key";
			} else {
				me.text = "Change console hotkey (" + Keys.toString(DevConsole.toggleKey) + ")";
			}
		});
		settingsPanel.addUIElement(buttonLabel);

		ModButton consoleKeyButton = new ModButton(BUTTON_X, BUTTON_Y, settingsPanel, (me) -> {
			me.parent.waitingOnEvent = true;
			oldInputProcessor = Gdx.input.getInputProcessor();
			Gdx.input.setInputProcessor(new InputAdapter() {
				@Override
				public boolean keyUp(int keycode) {
					DevConsole.toggleKey = keycode;
					BaseMod.setString("console-key", Keys.toString(keycode));
					me.parent.waitingOnEvent = false;
					Gdx.input.setInputProcessor(oldInputProcessor);
					return true;
				}
			});
		});
		settingsPanel.addUIElement(consoleKeyButton);

		ModLabeledToggleButton enableConsole = new ModLabeledToggleButton("Enable dev console",
				BUTTON_ENABLE_X, BUTTON_ENABLE_Y, Settings.CREAM_COLOR, FontHelper.charDescFont,
				DevConsole.enabled, settingsPanel, (label) -> {}, (button) -> {
					DevConsole.enabled = button.enabled;
					BaseMod.setBoolean("console-enabled", button.enabled);
				});
		settingsPanel.addUIElement(enableConsole);
		
		
		final ModLabel autoCompleteInfo = new ModLabel(AutoComplete.enabled ? AUTOCOMPLETE_INFO : "", AUTOCOMPLETE_LABEL_X, AUTOCOMPLETE_LABEL_Y, settingsPanel, (me) -> {} );
		settingsPanel.addUIElement(autoCompleteInfo);
		
		ModLabeledToggleButton enableAutoComplete = new ModLabeledToggleButton("Enable Autocompletion",
				AUTOCOMPLETE_BUTTON_ENABLE_X, AUTOCOMPLETE_BUTTON_ENABLE_Y, Settings.CREAM_COLOR, FontHelper.charDescFont,
				AutoComplete.enabled, settingsPanel, (label) -> {}, (button) -> {
					AutoComplete.enabled = button.enabled;
					AutoComplete.resetAndSuggest();
					BaseMod.setString("autocomplete-enabled", button.enabled ? "true" : "false");
					autoCompleteInfo.text = AutoComplete.enabled ? AUTOCOMPLETE_INFO : "";
				});
		settingsPanel.addUIElement(enableAutoComplete);

		ModLabeledToggleButton enableWhatMod = new ModLabeledToggleButton("Enable mod name in tooltips",
				WHATMOD_BUTTON_X, WHATMOD_BUTTON_Y, Settings.CREAM_COLOR, FontHelper.charDescFont,
				WhatMod.enabled, settingsPanel, (label) -> {},
				(button) -> {
					WhatMod.enabled = button.enabled;
					BaseMod.setBoolean("whatmod-enabled", button.enabled);

				}
		);
		settingsPanel.addUIElement(enableWhatMod);
		settingsPanel.addUIElement(new ModLabel("Must restart game to take effect", WHATMOD_BUTTON_X, WHATMOD_BUTTON_Y - 30.0f, settingsPanel, (me) -> {} ));

		Texture badgeTexture = ImageMaster.loadImage("img/BaseModBadge.png");
		BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);
		
		// Couldn't find a better place to put these. If they're not right here, please move them to a different classes' receivePostInitialize()
		BaseMod.initializeUnderscoreCardIDs();
		BaseMod.initializeUnderscorePotionIDs();
		BaseMod.initializeUnderscoreEventIDs();
		BaseMod.initializeUnderscoreRelicIDs();
		BaseMod.initializeEncounters();
	}

}