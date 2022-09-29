package basemod;

import basemod.interfaces.ImGuiSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.patches.whatmod.WhatMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImInt;

import java.util.ArrayList;
import java.util.function.Consumer;

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

	@Override
	public void receiveImGui() {
		if (ImGui.collapsingHeader("Combat")) {
			if (AbstractDungeon.player != null) {
				if (ImGui.treeNodeEx("Player", ImGuiTreeNodeFlags.DefaultOpen)) {
					creatureInfo(AbstractDungeon.player, p -> {
						// max hp
						ImInt data = new ImInt(p.maxHealth);
						ImGui.inputInt("Max HP", data, 1, 10);
						if (data.get() < 1) {
							data.set(1);
						}
						if (data.get() != p.maxHealth) {
							p.maxHealth = data.get();
							p.currentHealth = Integer.min(p.currentHealth, p.maxHealth);
							p.healthBarUpdatedEvent();
							ReflectionHacks.setPrivate(p, AbstractCreature.class, "healthBarAnimTimer", 0.2f);
						}
						//gold
						data.set(p.gold);
						ImGui.inputInt("Gold", data, 1, 100);
						if (data.get() < 0) {
							data.set(0);
						}
						if (data.get() != p.gold) {
							p.gold = data.get();
							p.displayGold = p.gold;
						}
						// hand
						ArrayList<AbstractCard> cards = AbstractDungeon.player.hand.group;
						if (ImGui.treeNode(String.format("Hand (%d)###hand", cards.size()))) {
							if (ImGui.button("Draw Card")) {
								addToTop(new DrawCardAction(1));
							}
							ImGui.sameLine();
							if (ImGui.button("Discard All")) {
								addToTop(new DiscardAction(p, p, cards.size(), false));
							}
							for (int i=0; i<cards.size(); ++i) {
								ImGui.bulletText(cards.get(i).name);
								ImGui.sameLine();
								if (ImGui.button("Discard##discard" + i)) {
									addToTop(new DiscardSpecificCardAction(cards.get(i)));
								}
							}
							ImGui.treePop();
						}
					});
					ImGui.treePop();
				}
			}

			if (inCombat()) {
				if (ImGui.treeNodeEx("Monsters", ImGuiTreeNodeFlags.DefaultOpen)) {
					Boolean openAction = null;
					if (ImGui.button("Open All")) {
						openAction = true;
					}
					ImGui.sameLine();
					if (ImGui.button("Close All")) {
						openAction = false;
					}

					int i = 0;
					for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
						monsterInfo(i, m, openAction);
						++i;
					}
					ImGui.treePop();
				}
			}
		}
	}

	private void monsterInfo(int i, AbstractMonster c, Boolean openAction) {
		if (c.isDeadOrEscaped()) return;
		if (openAction != null) {
			ImGui.setNextItemOpen(openAction);
		}
		if (ImGui.treeNode(i, c.name)) {
			creatureInfo(c, m -> {
				ImGui.sameLine();
				if (ImGui.button("Kill")) {
					addToTop(new InstantKillAction(m));
				}
			});
			ImGui.treePop();
		}
	}

	private void creatureInfo(AbstractCreature c, Consumer<AbstractCreature> callback) {
		// current hp
		ImInt hp = new ImInt(c.currentHealth);
		ImGui.sliderInt("HP", hp.getData(), 1, c.maxHealth);
		if (hp.get() != c.currentHealth) {
			c.currentHealth = hp.get();
			c.healthBarUpdatedEvent();
			ReflectionHacks.setPrivate(c, AbstractCreature.class, "healthBarAnimTimer", 0.2f);
		}
		if (callback != null) {
			callback.accept(c);
		}

		// powers
		if (!c.powers.isEmpty() && ImGui.treeNode("Powers")) {
			for (AbstractPower p : c.powers) {
				ImGui.bulletText(p.name);
				ImGui.sameLine();
				if (ImGui.button("Remove")) {
					addToTop(new RemoveSpecificPowerAction(c, c, p));
				}
			}
			ImGui.treePop();
		}
	}

	private void addToTop(AbstractGameAction action) {
		AbstractDungeon.actionManager.addToTop(action);
	}

	private boolean inCombat() {
		return AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT;
	}
}