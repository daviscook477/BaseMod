package basemod;

import basemod.devcommands.ConsoleCommand;
import basemod.interfaces.PostEnergyRechargeSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.PostRenderSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DevConsole
implements PostEnergyRechargeSubscriber, PostInitializeSubscriber, PostRenderSubscriber, PostUpdateSubscriber {
	public static final Logger logger = LogManager.getLogger(DevConsole.class.getName());

	private static final int HISTORY_SIZE = 10;

	public static final float CONSOLE_X = 200.0f;
	public static final float CONSOLE_Y = 200.0f;
	public static final float CONSOLE_W = 800.0f;
	public static final float CONSOLE_H = 40.0f;
	public static final float CONSOLE_PAD_X = 15.0f;
	public static final int CONSOLE_TEXT_SIZE = 30;
	private static final int MAX_LINES = 8;
	// This regular expression matches any number of consecutive whitespaces (but at least 1)
	public static final String PATTERN = "[\\s]+";
	public static final String PROMPT = "$> ";

	public static BitmapFont consoleFont = null;
	private static InputProcessor consoleInputProcessor;
	private static InputProcessor otherInputProcessor = null;
	public static Texture consoleBackground = null;

	public static boolean infiniteEnergy = false;
	public static boolean forceUnlocks = false;
	public static int unlockLevel = -1;

	public static boolean enabled = false;
	public static boolean visible = false;
	public static int toggleKey = Keys.GRAVE;
	public static String currentText = "";

	public static int priorKey = Keys.UP;
	public static int nextKey = Keys.DOWN;
	public static PriorCommandsList priorCommands;
	public static ArrayList<String> log;
	public static ArrayList<Boolean> prompted;
	public static int commandPos;

	public DevConsole() {
		BaseMod.subscribe(this);

		priorCommands = new PriorCommandsList();
		commandPos = -1;
		log = new ArrayList<>(priorCommands);
		prompted = new ArrayList<>(Collections.nCopies(log.size(), true));

		AutoComplete.init();

		ConsoleCommand.initialize();
	}

	// If you add, remove or change a command make sure to also do the same in the AutoComplete class
	public static void execute() {
		// To get the tokens, we first trim the current Text (removing whitespaces from the start and end)
		// then we split it using a pattern that matches one or more consecutive whitespaces
		// The resulting array tokens only has Strings with no whitespaces
		String[] tokens = currentText.trim().split(PATTERN);
		if (priorCommands.size() == 0 || !priorCommands.get(0).equals(currentText)) {
			priorCommands.add(0, currentText);
		}
		log.add(0, currentText);
		prompted.add(0, true);
		commandPos = -1;
		currentText = "";

		if (tokens.length < 1) {
			return;
		}
		for (int i = 0; i < tokens.length; i++) {
			tokens[i] = tokens[i].trim();
		}


		ConsoleCommand.execute(tokens);
	}




	public static void couldNotParse() {
		log("could not parse previous command");
	}


	@Override
	public void receivePostEnergyRecharge() {
		if (infiniteEnergy) {
			EnergyPanel.setEnergy(9999);
		}
	}

	@Override
	public void receivePostInitialize() {
		consoleInputProcessor = new ConsoleInputProcessor();

		// Console font
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/Kreon-Regular.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = (int) (CONSOLE_TEXT_SIZE * Settings.scale);
		consoleFont = generator.generateFont(parameter);
		generator.dispose();

		consoleBackground = ImageMaster.loadImage("img/ConsoleBackground.png");

		AutoComplete.postInit();
	}

	public static void log(String text) {
		log.add(0, text);
		prompted.add(0, false);
	}

	public static void log(Collection<?> list) {
		for (Object o : list) {
			log(o.toString());
		}
	}

	@Override
	public void receivePostRender(SpriteBatch sb) {
		if (visible && consoleFont != null) {
			int sizeToDraw = log.size() + 1;
			if (sizeToDraw > MAX_LINES) {
				sizeToDraw = MAX_LINES;
			}

			sb.draw(consoleBackground, CONSOLE_X * Settings.scale, CONSOLE_Y * Settings.scale,
					(CONSOLE_W * Settings.scale),
					(CONSOLE_H * Settings.scale + (CONSOLE_TEXT_SIZE * Settings.scale * (sizeToDraw - 1))));

			if (AutoComplete.enabled) {
				AutoComplete.render(sb);
			}

			float x = (CONSOLE_X * Settings.scale + (CONSOLE_PAD_X * Settings.scale));
			float y = (CONSOLE_Y * Settings.scale + (float) Math.floor(CONSOLE_TEXT_SIZE * Settings.scale));
			consoleFont.draw(sb, PROMPT + currentText, x, y);
			for (int i = 0; i < sizeToDraw - 1; i++) {
				y += (float) Math.floor(CONSOLE_TEXT_SIZE * Settings.scale);
				consoleFont.draw(sb, (prompted.get(i) ? PROMPT : "") + log.get(i), x, y);
			}
		}
	}

	@Override
	public void receivePostUpdate() {
		if (Gdx.input.isKeyJustPressed(toggleKey)) {
			AutoComplete.reset();
			if (visible) {
				currentText = "";
				commandPos = -1;
			} else {
				otherInputProcessor = Gdx.input.getInputProcessor();
				
				if (AutoComplete.enabled) {
					AutoComplete.suggest(false);
				}
			}

			// only allow opening console when enabled but allow closing the console anytime
			if (visible || enabled) {
				Gdx.input.setInputProcessor(visible ? otherInputProcessor : consoleInputProcessor);
				visible = !visible;
			}
		}
		
		//	If AutoComplete is enabled and the key to select a suggestion is pressed
		//	select the next or previous suggestion
		if (AutoComplete.enabled && Gdx.input.isKeyPressed(AutoComplete.selectKey)) {

			if (Gdx.input.isKeyJustPressed(priorKey) ) {
				if (visible) {
					AutoComplete.selectUp();
				}
			}
			if (Gdx.input.isKeyJustPressed(nextKey)) {
				if (visible) {
					AutoComplete.selectDown();
				}
			}

		} else {
			// get previous commands
			if (Gdx.input.isKeyJustPressed(priorKey)) {
				if (visible) {
					if (commandPos + 1 < priorCommands.size()) {
						commandPos++;
						currentText = priorCommands.get(commandPos);
						AutoComplete.resetAndSuggest();
					}
				}
			}
			if (Gdx.input.isKeyJustPressed(nextKey)) {
				if (visible) {
					if (commandPos - 1 < 0) {
						currentText = "";
						commandPos = -1;
					} else {
						commandPos--;
						currentText = priorCommands.get(commandPos);
					}
					AutoComplete.resetAndSuggest();
				}
			}
		}
		// If the fill in key is pressed automaticallly fill in what the user wants
		if (AutoComplete.enabled && (Gdx.input.isKeyJustPressed(AutoComplete.fillKey1)
				|| Gdx.input.isKeyJustPressed(AutoComplete.fillKey2))) {
			AutoComplete.fillInSuggestion();
		}

		// if the key to delete the last token is pressed, delete the rightmost token
		if (Gdx.input.isKeyJustPressed(AutoComplete.deleteTokenKey)) {
			AutoComplete.removeOneTokenUsingSpaceAndIdDelimiter();
			if (AutoComplete.enabled) {
				AutoComplete.suggest(false);
			}
		}
	}

	public static class PriorCommandsList extends ArrayList<String>
	{
		private static final String HISTORY_LOCATION = SpireConfig.makeFilePath(BaseModInit.MODNAME, "console-history", "txt");

		public PriorCommandsList()
		{
			try {
				List<String> list = Files.readAllLines(Paths.get(HISTORY_LOCATION), StandardCharsets.UTF_8);
				addAll(list);
			} catch (IOException e) {
				logger.error("Failed to load dev console history: " + e);
			}
		}

		private void saveHistory()
		{
			try {
				Files.write(Paths.get(HISTORY_LOCATION), this.subList(0, Math.min(HISTORY_SIZE, size())), StandardCharsets.UTF_8);
			} catch (IOException e) {
				logger.error("Failed to save dev console history: " + e);
			}
		}

		@Override
		public boolean add(String s)
		{
			boolean ret = super.add(s);
			saveHistory();
			return ret;
		}

		@Override
		public void add(int index, String element)
		{
			super.add(index, element);
			saveHistory();
		}

		@Override
		public void clear()
		{
			super.clear();
			saveHistory();
		}
	}
}
