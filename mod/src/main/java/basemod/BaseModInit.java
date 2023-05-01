package basemod;

import basemod.interfaces.ImGuiSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.patches.imgui.ImGuiPatches;
import basemod.patches.whatmod.WhatMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;

/**
 *
 * Handles the creation of the ModBadge and settings panel for BaseMod
 *
 */
public class BaseModInit implements PostInitializeSubscriber, ImGuiSubscriber {
	public static final String MODNAME = "BaseMod";
	public static final String AUTHOR = "t-larson, test447, FlipskiZ, Haashi, Blank The Evil, kiooeht, robojumper, Skrelpoid";
	public static final String DESCRIPTION = "Modding API and Dev console.";

	private InputProcessor oldInputProcessor;

	public static final float BUTTON_X = 350.0f;
	public static final float BUTTON_Y = 650.0f;
	public static final float BUTTON_LABEL_X = 475.0f;
	public static final float BUTTON_LABEL_Y = 700.0f;
	public static final float IMGUI_BUTTON_Y = 185.0f;
	public static final float IMGUI_BUTTON_LABEL_Y = 235.0f;
	public static final float BUTTON_ENABLE_X = 350.0f;
	public static final float BUTTON_ENABLE_Y = 600.0f;
	public static final float AUTOCOMPLETE_BUTTON_ENABLE_X = 350.0f;
	public static final float AUTOCOMPLETE_BUTTON_ENABLE_Y = 550.0f;
	public static final float AUTOCOMPLETE_LABEL_X = 350.0f;
	public static final float AUTOCOMPLETE_LABEL_Y = 425.0f;
	private static final String AUTOCOMPLETE_INFO = "Press L_Shift + Up/Down to scroll through suggestions.\nPress Tab or Right to complete the current command.\nPress Left to delete the last token.";
	public static final float WHATMOD_BUTTON_X = 350.0f;
	public static final float WHATMOD_BUTTON_Y = 350.0f;
	public static final float FIXES_BUTTON_X = 350.0f;
	public static final float FIXES_BUTTON_Y = 300.0f;

	@Override
	public void receivePostInitialize() {
		// BaseMod post initialize handling
		ModPanel settingsPanel = new ModPanel();

		ModLabel buttonLabel = new ModLabel("", BUTTON_LABEL_X, BUTTON_LABEL_Y, settingsPanel, (me) -> {
			if (me.parent.waitingOnEvent) {
				me.text = "Press key";
			} else {
				me.text = "Change console hotkey (" + DevConsole.newToggleKey.toString() + ")";
			}
		});
		settingsPanel.addUIElement(buttonLabel);

		ModButton consoleKeyButton = new ModButton(BUTTON_X, BUTTON_Y, settingsPanel, (me) -> {
			me.parent.waitingOnEvent = true;
			oldInputProcessor = Gdx.input.getInputProcessor();
			Gdx.input.setInputProcessor(new HotkeyInput() {
				@Override
				public boolean keyUp(int keycode) {
					if (!super.keyUp(keycode)) {
						DevConsole.newToggleKey = new DevConsole.KeyWithMods(keycode, ctrl, shift, alt);
						BaseMod.setString("console-key", DevConsole.newToggleKey.save());
						me.parent.waitingOnEvent = false;
						Gdx.input.setInputProcessor(oldInputProcessor);
					}
					return true;
				}
			});
		});
		settingsPanel.addUIElement(consoleKeyButton);

		if (Loader.LWJGL3_ENABLED) {
			ModLabel imguiButtonLabel = new ModLabel("", BUTTON_LABEL_X, IMGUI_BUTTON_LABEL_Y, settingsPanel, (me) -> {
				if (me.parent.waitingOnEvent) {
					me.text = "Press key";
				} else {
					me.text = "Change ImGui hotkey (" + ImGuiPatches.toggleKey.toString() + ")";
				}
			});
			settingsPanel.addUIElement(imguiButtonLabel);

			ModButton imguiKeyButton = new ModButton(BUTTON_X, IMGUI_BUTTON_Y, settingsPanel, (me) -> {
				me.parent.waitingOnEvent = true;
				oldInputProcessor = Gdx.input.getInputProcessor();
				Gdx.input.setInputProcessor(new HotkeyInput() {
					@Override
					public boolean keyUp(int keycode) {
						if (!super.keyUp(keycode)) {
							ImGuiPatches.toggleKey = new DevConsole.KeyWithMods(keycode, ctrl, shift, alt);
							BaseMod.setString("imgui-key", ImGuiPatches.toggleKey.save());
							me.parent.waitingOnEvent = false;
							Gdx.input.setInputProcessor(oldInputProcessor);
						}
						return true;
					}
				});
			});
			settingsPanel.addUIElement(imguiKeyButton);
		}

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

		ModLabeledToggleButton enableWhatMod = new ModLabeledToggleButton(
				"Enable mod name in tooltips",
				FontHelper.colorString("Must restart game to take effect.", "r"),
				WHATMOD_BUTTON_X, WHATMOD_BUTTON_Y, Settings.CREAM_COLOR, FontHelper.charDescFont,
				WhatMod.enabled, settingsPanel, (label) -> {},
				(button) -> {
					WhatMod.enabled = button.enabled;
					BaseMod.setBoolean("whatmod-enabled", button.enabled);
				}
		);
		settingsPanel.addUIElement(enableWhatMod);

		ModLabeledToggleButton enabledFixes = new ModLabeledToggleButton(
				"Enable base game fixes",
				"BaseMod makes some gameplay changes to facilitate modded gameplay. Disabling this option disables those changes so you can have a purer vanilla experience.",
				FIXES_BUTTON_X, FIXES_BUTTON_Y, Settings.CREAM_COLOR, FontHelper.charDescFont,
				BaseMod.fixesEnabled, settingsPanel, (label) -> {},
				(button) -> {
					BaseMod.fixesEnabled = button.enabled;
					BaseMod.setBoolean("basemod-fixes", button.enabled);
				}
		);
		settingsPanel.addUIElement(enabledFixes);

		Texture badgeTexture = ImageMaster.loadImage("img/BaseModBadge.png");
		BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);
		
		// Couldn't find a better place to put these. If they're not right here, please move them to a different classes' receivePostInitialize()
		BaseMod.initializeUnderscoreCardIDs();
		BaseMod.initializeUnderscorePotionIDs();
		BaseMod.initializeUnderscoreEventIDs();
		BaseMod.initializeUnderscoreRelicIDs();
		BaseMod.initializeEncounters();
	}

	private BaseModImGuiUI ui = null;

	@Override
	public void receiveImGui()
	{
		if (ui == null) {
			ui = new BaseModImGuiUI();
		}
		ui.receiveImGui();
	}

	private abstract static class HotkeyInput extends InputAdapter
	{
		protected boolean ctrl = false;
		protected boolean shift = false;
		protected boolean alt = false;

		@Override
		public boolean keyDown(int keycode)
		{
			switch (keycode) {
				case Keys.CONTROL_LEFT:
				case Keys.CONTROL_RIGHT:
					ctrl = true;
					break;
				case Keys.SHIFT_LEFT:
				case Keys.SHIFT_RIGHT:
					shift = true;
					break;
				case Keys.ALT_LEFT:
				case Keys.ALT_RIGHT:
					alt = true;
					break;
				default:
					return false;
			}
			return true;
		}

		@Override
		public boolean keyUp(int keycode) {
			switch (keycode) {
				case Keys.CONTROL_LEFT:
				case Keys.CONTROL_RIGHT:
					if (!shift && !alt) {
						return false;
					}
					ctrl = false;
					break;
				case Keys.SHIFT_LEFT:
				case Keys.SHIFT_RIGHT:
					if (!ctrl && !alt) {
						return false;
					}
					shift = false;
					break;
				case Keys.ALT_LEFT:
				case Keys.ALT_RIGHT:
					if (!ctrl && !shift) {
						return false;
					}
					alt = false;
					break;
				default:
					return false;
			}
			return true;
		}
	}
}