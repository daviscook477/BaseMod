package basemod;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoComplete {

	public static class Pair {
		public int start, end;

		public Pair(int start, int end) {
			this.start = start;
			this.end = end;
		}

		public Pair cpy() {
			return new Pair(start, end);
		}

		public Pair set(int start, int end) {
			this.start = start;
			this.end = end;
			return this;
		}
	}

	private static int ID_CREATOR = -1;

	public static final int RESET = ID_CREATOR++;

	private static final int MAX_SUGGESTIONS = 5;
	private static final Color TEXT_COLOR = Color.GRAY.cpy();

	public static boolean enabled = true;
	public static int selectKey = Keys.SHIFT_LEFT;
	public static int deleteTokenKey = Keys.LEFT;
	public static int fillKey1 = Keys.RIGHT;
	public static int fillKey2 = Keys.TAB;
	public static int selected = 0;
	private static ArrayList<String> suggestions;
	private static Stack<Pair> suggestionPairs;
	private static String[] tokens;
	private static boolean foundStart, foundEnd, noMatch;
	private static boolean alreadySorted;
	private static boolean commandComplete;
	private static int currentID;
	private static int lastLength = 0;
	private static int whiteSpaces = 0;
	private static int lastWhiteSpaces = 0;
	private static Pattern spacePattern = Pattern.compile(DevConsole.PATTERN);

	private static boolean implementedYet = true;
	private static float drawX;
	private static float promptWidth = 0;
	private static GlyphLayout glyphs;

	public static void init() {
		reset();
	}

	public static void postInit() {
		glyphs = new GlyphLayout(DevConsole.consoleFont, DevConsole.PROMPT);
		promptWidth = glyphs.width;
		calculateDrawX();
		suggest(false);
	}

	public static void reset() {
		calculateDrawX();
		currentID = RESET;
		suggestions = new ArrayList<>();
		suggestionPairs = new Stack<>();
		tokens = new String[] {};
		selected = 0;
		foundStart = foundEnd = noMatch = false;
		whiteSpaces = 0;
		lastWhiteSpaces = 0;
		lastLength = 0;
	}

	/**
	 * This should only be called if there is already text (that doesn't end with a
	 * space) in the console and then suggestions should be shown (e.g. prior
	 * command or AutoComplete enabled toggled form off to on)
	 */
	public static void resetAndSuggest() {
		if (AutoComplete.enabled) {
			reset();
			// Make sure to load whitespace to avoid getting a full unfiltered list of
			// suggestions
			lastWhiteSpaces = countSpaces();
			suggest(false);
		}
	}

	public static void selectUp() {
		if (selected > 0 && !noMatch && !suggestions.isEmpty() && !suggestionPairs.isEmpty()) {
			selected--;
		}
	}

	public static void selectDown() {
		if (!noMatch && !suggestions.isEmpty() && !suggestionPairs.isEmpty()) {
			Pair pair = suggestionPairs.peek();
			if (selected < pair.end - pair.start && selected < suggestions.size() - 1) {
				selected++;
			}
		}
	}

	public static void fillInSuggestion() {
		if (!noMatch && !suggestions.isEmpty() && !suggestionPairs.isEmpty()) {
			DevConsole.currentText = getTextWithoutRightmostToken(false)
					+ suggestions.get(selected + suggestionPairs.peek().start) + " ";
			reset();
			suggest(false);
		}
	}

	// returns the text without the rightmost token
	public static String getTextWithoutRightmostToken(boolean removeSingleSpace) {
		int lastSpace = DevConsole.currentText.lastIndexOf(' ');
		String text = "";
		int offset = (removeSingleSpace && lastSpace == DevConsole.currentText.length() - 1) ? 0 : 1;
		if (lastSpace != -1) {
			text = DevConsole.currentText.substring(0, lastSpace + offset);
		}
		return text;
	}

	private static int countSpaces() {
		int spaces = 0;
		Matcher spaceMatcher = spacePattern.matcher(DevConsole.currentText);
		// Count the spaces (ignore it if it is the very first Character)
		while (spaceMatcher.find()) {
			if (spaceMatcher.start() != 0) {
				spaces++;
			}
		}
		return spaces;
	}

	public static void suggest(boolean isCharacterRemoved) {
		// To get the tokens, we first trim the current Text (removing whitespaces from
		// the start and end)
		// then we split it using a pattern that matches one or more consecutive
		// whitespaces
		// The resulting array tokens only has Strings with no whitespaces
		tokens = DevConsole.currentText.trim().split(DevConsole.PATTERN);

		whiteSpaces = countSpaces();

		createCMDSuggestions();

		if (tokenLengthChanged() || DevConsole.currentText.isEmpty() || currentID == RESET) {
			suggestionPairs.clear();
		}

		if (currentID == RESET) {
			suggestions.clear();
		}

		// if the user just deleted the last character we don't calculate the pairs
		// again but instead just use the pair before this one on the stack if it exists
		if (isCharacterRemoved && !tokenLengthChanged() && suggestionPairs.size() >= 2) {
			suggestionPairs.pop();
			selected = 0;
			if (suggestionPairs.peek().end <= -1) {
				noMatch = true;
			}
		} else {
			createPair();
		}
		calculateDrawX();
		lastWhiteSpaces = whiteSpaces;
	}

	private static boolean tokenLengthChanged() {
		return lastLength != tokens.length;
	}

	private static boolean whiteSpacesIncreased() {
		return whiteSpaces > lastWhiteSpaces;
	}

	private static void createPair() {
		if (whiteSpacesIncreased()) {
			createPair(" ");
			return;
		}
		createPair(tokens[tokens.length - 1]);
	}

	private static void createPair(String prefix) {
		selected = 0;
		Pair pair = null;
		if (!suggestionPairs.isEmpty()) {
			// if we already have pairs get the last pair to set a smaller searching scope
			pair = suggestionPairs.peek().cpy();
		} else {
			pair = new Pair(0, suggestions.size() - 1);
		}
		if (shouldShowAll(prefix)) {
			foundStart = foundEnd = true;
			noMatch = false;
			suggestionPairs.push(pair.set(0, suggestions.size() - 1));
			return;
		}
		linearSearch(pair, prefix);
	}

	private static boolean shouldShowAll(String prefix) {
		if (DevConsole.currentText.isEmpty()) {
			return true;
		} else {
			// if the prefix is empty or a space, or the last Character in the consoles text
			// is a space, return true
			return prefix == null || prefix.isEmpty() || prefix.equals(" ")
					|| DevConsole.currentText.lastIndexOf(' ') == DevConsole.currentText.length() - 1;
		}
	}

	// I tried implementing a binary search for this but that didn't work so this
	// has to suffice. If anyone can do this please help
	private static void linearSearch(Pair pair, String prefix) {
		
		String lowerCasePrefix = prefix.toLowerCase();

		foundStart = foundEnd = noMatch = false;
		int size = suggestions.size();
		// Search for the start
		while (!foundStart && !noMatch) {
			if (pair.start >= size) {
				noMatch = true;
			} else {
				// The first suggestion that starts with our prefix is our start
				if (suggestions.get(pair.start).toLowerCase().startsWith(lowerCasePrefix)) {
					foundStart = true;
				} else {
					pair.start++;
				}
			}
		}

		// Search for the End only if you found the Start
		if (foundStart) {
			pair.end = pair.start + 1;
			while (!foundEnd) {
				// The last element that starts with the prefix is the last element of
				// suggestions OR the current element doesnt start with the prefix
				// Either way the index we found is the Element directly before this element
				if (pair.end >= size || !suggestions.get(pair.end).toLowerCase().startsWith(lowerCasePrefix)) {
					foundEnd = true;
					pair.end--;
				} else {
					// if the above condition is not true inspect the next Element
					pair.end++;
				}
			}
		}

		if (noMatch) {
			// No match value. leads to search automatically returning with noMatch
			// because start is bigger than end (and size)
			pair.set(Integer.MAX_VALUE, Integer.MIN_VALUE);
		}
		suggestionPairs.push(pair);
	}

	// if you add a new command here make sure you sort it in alphabetically
	public static final String[] COMMANDS = { "clear", "debug", "deck", "draw", "energy", "event", "fight", "gold", "hand",
			"help", "hp", "info", "kill", "maxhp", "potion", "power", "relic", "unlock"};

	public static final int CMDS = ID_CREATOR++;

	public static final int INFO = ID_CREATOR++;
	public static final int HELP = ID_CREATOR++;
	
	private static Comparator<String> caseInsensitiveCompare = AutoComplete::compareCaseInsensitive;
	
	private static int compareCaseInsensitive(String s1, String s2) {
		return s1.toLowerCase().compareTo(s2.toLowerCase());
	}

	private static void createCMDSuggestions() {
		alreadySorted = false;
		implementedYet = true;
		commandComplete = false;
		if (whiteSpaces == 0) {
			alreadySorted = true;
			if (currentID != CMDS) {
				currentID = CMDS;
				suggestions.clear();
				suggestions.addAll(Arrays.asList(COMMANDS));
			}
		} else {
			switch (tokens[0].toLowerCase()) {
			case "relic": {
				createRelicSuggestions();
				break;
			}
			case "hand": {
				createHandSuggestions();
				break;
			}
			case "info": {
				currentID = INFO;
				suggestions.clear();
				alreadySorted = true;
				commandComplete = true;
				break;
			}
			case "kill": {
				createKillSuggestions();
				break;
			}
			case "gold": {
				createGoldSuggestions();
				break;
			}
			case "energy": {
				createEnergySuggestions();
				break;
			}
			case "deck": {
				createDeckSuggestions();
				break;
			}
			case "draw": {
				createDrawSuggestions();
				break;
			}
			case "fight": {
				createFightSuggestions();
				break;
			}
			case "event": {
				createEventSuggestions();
				break;
			}
			case "potion": {
				createPotionSuggestions();
				break;
			}
			case "unlock": {
				createUnlockSuggestions();
				break;
			}
			case "power": {
				createPowerSuggestions();
				break;
			}
			case "clear": {
				createClearSuggestions();
				break;
			}
			case "help": {
				currentID = HELP;
				suggestions.clear();
				alreadySorted = true;
				commandComplete = true;
				break;
			}
			case "hp": {
				createHPSuggestions();
				break;
			}
			case "maxhp": {
				createMaxHPSuggestions();
				break;
			}
			case "debug":{
				createDebugSuggestions();
				break;
			}
			default: {
				noMatch = true;
				currentID = RESET;
				break;
			}
			}
			if (!alreadySorted && currentID != RESET && !commandComplete) {
				alreadySorted = true;
				Collections.sort(suggestions, caseInsensitiveCompare);
			}
		}
	}

	private static final String[] RELIC_CMDS = { "add", "desc", "flavor", "list", "pool", "remove" };
	private static final String[] RELIC_LIST_CMDS = { "boss", "common", "rare", "shop", "special", "starter",
			"uncommon" };

	public static final int RELIC = ID_CREATOR++;
	public static final int RELIC_LIST = ID_CREATOR++;
	public static final int RELIC_IDS = ID_CREATOR++;

	private static void createRelicSuggestions() {
		if (whiteSpaces == 1) {
			alreadySorted = true;
			if (currentID == RELIC) {
				return;
			}
			currentID = RELIC;
			suggestions.clear();
			suggestions.addAll(Arrays.asList(RELIC_CMDS));
		} else if (whiteSpaces == 2) {
			if (tokens[1].equalsIgnoreCase("list")) {
				alreadySorted = true;
				if (currentID == RELIC_LIST) {
					return;
				}
				currentID = RELIC_LIST;
				suggestions.clear();
				suggestions.addAll(Arrays.asList(RELIC_LIST_CMDS));
			} else if (isRelicIDsCMD()) {
				if (currentID == RELIC_IDS) {
					alreadySorted = true;
					return;
				}
				currentID = RELIC_IDS;
				suggestions.clear();
				for (String id : BaseMod.listAllRelicIDs()) {
					suggestions.add(id.replace(' ', '_'));
				}
			} else {
				currentID = RESET;
				noMatch = true;
			}
		} else {
			commandComplete = true;
		}
	}

	private static boolean isRelicIDsCMD() {
		return tokens[1].equalsIgnoreCase("a") || tokens[1].equalsIgnoreCase("add") || tokens[1].equalsIgnoreCase("r")
				|| tokens[1].equalsIgnoreCase("remove") || tokens[1].equalsIgnoreCase("desc")
				|| tokens[1].equalsIgnoreCase("flavor") || tokens[1].equalsIgnoreCase("pool");
	}

	private static final String[] HAND_CMDS = { "add", "discard", "remove", "set" };
	private static final String[] HAND_SET_CMDS = { "block", "cost", "damage", "magic" };

	public static final int HAND = ID_CREATOR++;
	public static final int HAND_ADD = ID_CREATOR++;
	public static final int HAND_REMOVE = ID_CREATOR++;
	public static final int HAND_DISCARD = ID_CREATOR++;
	public static final int HAND_SET = ID_CREATOR++;
	public static final int HAND_SET_CARD = ID_CREATOR++;

	private static void createHandSuggestions() {
		if (whiteSpaces == 1) {
			alreadySorted = true;
			if (currentID == HAND) {
				return;
			}
			currentID = HAND;
			suggestions.clear();
			suggestions.addAll(Arrays.asList(HAND_CMDS));
		} else if (whiteSpaces == 2) {
			if (isAdd()) {
				if (currentID == HAND_ADD) {
					alreadySorted = true;
					return;
				}
				currentID = HAND_ADD;
				cardIDList(false);
			} else if (isRemove()) {
				if (currentID == HAND_REMOVE) {
					alreadySorted = true;
					return;
				}
				currentID = HAND_REMOVE;
				cardIDList(true);
			} else if (isDiscard()) {
				if (currentID == HAND_DISCARD) {
					alreadySorted = true;
					return;
				}
				currentID = HAND_DISCARD;
				cardIDList(true);
			} else if (isSet()) {
				alreadySorted = true;
				if (currentID == HAND_SET) {
					return;
				}
				currentID = HAND_SET;
				suggestions.clear();
				suggestions.addAll(Arrays.asList(HAND_SET_CMDS));
			} else {
				currentID = RESET;
				noMatch = true;
			}
		} else if (whiteSpaces == 3 && isSet()) {
			if (currentID == HAND_SET_CARD) {
				alreadySorted = true;
				return;
			}
			currentID = HAND_SET_CARD;
			cardIDList(true);
		} else if ((whiteSpaces == 3 || whiteSpaces == 4) && !isRemove() && !isDiscard()) {
			smallNumbers();
		} else {
			commandComplete = true;
		}
	}

	private static final String[] KILL_CMDS = { "all", "self" };

	public static final int KILL = ID_CREATOR++;

	private static void createKillSuggestions() {
		if (whiteSpaces == 1) {
			alreadySorted = true;
			if (currentID == KILL) {
				return;
			}
			currentID = KILL;
			suggestions.clear();
			suggestions.addAll(Arrays.asList(KILL_CMDS));
		} else {
			commandComplete = true;
		}
	}

	private static final String[] ENERGY_CMDS = { "add", "lose", "inf" };

	public static final int ENERGY = ID_CREATOR++;

	private static void createEnergySuggestions() {
		if (whiteSpaces == 1) {
			alreadySorted = true;
			if (currentID == ENERGY) {
				return;
			}
			currentID = ENERGY;
			suggestions.clear();
			suggestions.addAll(Arrays.asList(ENERGY_CMDS));
		} else if (whiteSpaces == 2 && !tokens[1].equalsIgnoreCase("inf")) {
			mediumNumbers();
		} else {
			commandComplete = true;
		}
	}

	private static final String[] GOLD_CMDS = { "add", "lose" };

	public static final int GOLD = ID_CREATOR++;

	private static void createGoldSuggestions() {
		if (whiteSpaces == 1) {
			alreadySorted = true;
			if (currentID == GOLD) {
				return;
			}
			currentID = GOLD;
			suggestions.clear();
			suggestions.addAll(Arrays.asList(GOLD_CMDS));
		} else if (whiteSpaces == 2) {
			bigNumbers();
		} else {
			commandComplete = true;
		}
	}

	private static final String[] DECK_CMDS = { "add", "remove" };

	public static final int DECK = ID_CREATOR++;
	public static final int DECK_ADD = ID_CREATOR++;
	public static final int DECK_REMOVE = ID_CREATOR++;

	private static void createDeckSuggestions() {
		if (whiteSpaces == 1) {
			alreadySorted = true;
			if (currentID == DECK) {
				return;
			}
			currentID = DECK;
			suggestions.clear();
			suggestions.addAll(Arrays.asList(DECK_CMDS));
		} else if (whiteSpaces == 2) {
			if (isAdd()) {
				if (currentID == DECK_ADD) {
					alreadySorted = true;
					return;
				}
				currentID = DECK_ADD;
				cardIDList(false);
			} else if (isRemove()) {
				if (currentID == DECK_REMOVE) {
					alreadySorted = true;
					return;
				}
				currentID = DECK_REMOVE;
				cardIDList(true);
			} else {
				currentID = RESET;
				noMatch = true;
			}
		} else if ((whiteSpaces == 3 || whiteSpaces == 4) && !isRemove()) {
			smallNumbers();
		} else {
			commandComplete = true;
		}
	}

	public static final int DRAW = ID_CREATOR++;

	private static void createDrawSuggestions() {
		if (whiteSpaces == 1) {
			smallNumbers();
		} else {
			commandComplete = true;
		}
	}

	public static final int FIGHT = ID_CREATOR++;

	private static void createFightSuggestions() {
		if (whiteSpaces == 1) {
			if (currentID == FIGHT) {
				alreadySorted = true;
				return;
			}
			currentID = FIGHT;
			suggestions.clear();
			for (String id : BaseMod.encounterList) {
				suggestions.add(id.replace(' ', '_'));
			}
		} else {
			commandComplete = true;
		}
	}

	public static final int EVENT = ID_CREATOR++;

	private static void createEventSuggestions() {
		if (whiteSpaces == 1) {
			if (currentID == EVENT) {
				alreadySorted = true;
				return;
			}
			currentID = EVENT;
			suggestions.clear();

			// Not actually unchecked
			@SuppressWarnings("unchecked")
			Map<String, EventStrings> events = (Map<String, EventStrings>) (ReflectionHacks
					.getPrivateStatic(LocalizedStrings.class, "events"));
			if (events != null) {
				for (String key : events.keySet()) {
					suggestions.add(key.replace(' ', '_'));
				}
			}
		} else {
			commandComplete = true;
		}
	}

	public static final int POTION = ID_CREATOR++;

	public static final int OBTAIN_POTION = ID_CREATOR++;

	private static void createPotionSuggestions() {
		if (whiteSpaces == 1) {
			alreadySorted = true;
			if (currentID == POTION) {
				return;
			}
			currentID = POTION;
			suggestions.clear();

			// Don't show more options than player has potion slots
			int slots = 0;
			if (AbstractDungeon.player != null) {
				slots = AbstractDungeon.player.potionSlots;
			}

			for (int i = 0; i < slots; i++) {
				suggestions.add(String.valueOf(i));
			}
			// Add the option to list Potions
			suggestions.add("list");
		} else if (whiteSpaces == 2 && !tokens[1].equalsIgnoreCase("list")) {
			if (currentID == OBTAIN_POTION) {
				alreadySorted = true;
				return;
			}
			currentID = OBTAIN_POTION;
			suggestions.clear();

			if (PotionHelper.potions != null) {
				for (String key : PotionHelper.potions) {
					suggestions.add(key.replace(' ', '_'));
				}
			}
		} else {
			commandComplete = true;
		}
	}

	private static final String[] UNLOCK_CMDS = { "always", "level" };

	public static final int UNLOCK = ID_CREATOR++;
	public static final int UNLOCK_LEVEL = ID_CREATOR++;

	private static void createUnlockSuggestions() {
		if (whiteSpaces == 1) {
			alreadySorted = true;
			if (currentID == UNLOCK) {
				return;
			}
			currentID = UNLOCK;
			suggestions.clear();
			suggestions.addAll(Arrays.asList(UNLOCK_CMDS));
		} else if (whiteSpaces == 2 && !tokens[1].equalsIgnoreCase("always")) {
			alreadySorted = true;
			if (currentID == UNLOCK_LEVEL) {
				return;
			}
			currentID = UNLOCK_LEVEL;
			suggestions.clear();
			for (int i = 0; i < 5; i++) {
				suggestions.add(String.valueOf(i));
			}
		} else {
			commandComplete = true;
		}
	}

	public static final int POWER = ID_CREATOR++;

	private static void createPowerSuggestions() {
		if (whiteSpaces == 1) {
			if (currentID == POWER) {
				alreadySorted = true;
				return;
			}
			currentID = POWER;
			suggestions.clear();
			for (String key : BaseMod.getPowerKeys()) {
				suggestions.add(key.replace(' ', '_'));
			}
		} else if (whiteSpaces == 2) {
			smallNumbers();
		} else {
			commandComplete = true;
		}
	}

	private static final String[] CLEAR_CMDS = { "cmd", "log" };

	public static final int CLEAR = ID_CREATOR++;

	private static void createClearSuggestions() {
		if (whiteSpaces == 1) {
			alreadySorted = true;
			if (currentID == CLEAR) {
				return;
			}
			currentID = CLEAR;
			suggestions.clear();
			suggestions.addAll(Arrays.asList(CLEAR_CMDS));
		} else {
			commandComplete = true;
		}
	}

	private static final String[] HP_CMDS = { "add", "lose" };

	public static final int HP = ID_CREATOR++;

	private static void createHPSuggestions() {
		if (whiteSpaces == 1) {
			alreadySorted = true;
			if (currentID == HP) {
				return;
			}
			currentID = HP;
			suggestions.clear();
			suggestions.addAll(Arrays.asList(HP_CMDS));
		} else if (whiteSpaces == 2) {
			mediumNumbers();
		} else {
			commandComplete = true;
		}
	}

	private static final String[] MAX_HP_CMDS = { "add", "lose" };

	public static final int MAX_HP = ID_CREATOR++;

	private static void createMaxHPSuggestions() {
		if (whiteSpaces == 1) {
			alreadySorted = true;
			if (currentID == MAX_HP) {
				return;
			}
			currentID = MAX_HP;
			suggestions.clear();
			suggestions.addAll(Arrays.asList(MAX_HP_CMDS));
		} else if (whiteSpaces == 2) {
			mediumNumbers();
		} else {
			commandComplete = true;
		}
	}
	
	private static final String[] DEBUG_CMDS = {"true", "false"};
	public static final int DEBUG = ID_CREATOR++;
	
	private static void createDebugSuggestions() {
		if (whiteSpaces == 1) {
			alreadySorted = true;
			if(currentID == DEBUG) {
				return;
			}
			currentID = DEBUG;
			suggestions.clear();
			suggestions.addAll(Arrays.asList(DEBUG_CMDS));
		} else {
			commandComplete = true;
		}
	}

	private static boolean isAdd() {
		return tokens[1].equalsIgnoreCase("add") || tokens[1].equalsIgnoreCase("a");
	}

	private static boolean isRemove() {
		return tokens[1].equalsIgnoreCase("remove") || tokens[1].equalsIgnoreCase("r");
	}

	private static boolean isDiscard() {
		return tokens[1].equalsIgnoreCase("discard") || tokens[1].equalsIgnoreCase("d");
	}

	private static boolean isSet() {
		return tokens[1].equalsIgnoreCase("set") || tokens[1].equalsIgnoreCase("s");
	}

	private static void cardIDList(boolean isRemove) {
		suggestions.clear();
		if (isRemove) {
			suggestions.add("all");
		}
		// Replace spaces with underscores to avoid an id being more than one token
		for (String key : CardLibrary.cards.keySet()) {
			suggestions.add(key.replace(' ', '_'));
		}
	}

	public static final int SMALL_NUMBERS = ID_CREATOR++;

	private static void smallNumbers() {
		alreadySorted = true;
		if (currentID == SMALL_NUMBERS) {
			return;
		}
		currentID = SMALL_NUMBERS;
		suggestions.clear();
		for (int i = 1; i <= 9; i++) {
			suggestions.add(String.valueOf(i));
		}
	}

	public static final int MEDIUM_NUMBERS = ID_CREATOR++;

	private static void mediumNumbers() {
		alreadySorted = true;
		if (currentID == MEDIUM_NUMBERS) {
			return;
		}
		currentID = MEDIUM_NUMBERS;
		suggestions.clear();
		for (int i = 10; i <= 90; i += 10) {
			suggestions.add(String.valueOf(i));
			suggestions.add(String.valueOf(i * 10));
		}
	}

	public static final int BIG_NUMBERS = ID_CREATOR++;

	private static void bigNumbers() {
		alreadySorted = true;
		if (currentID == BIG_NUMBERS) {
			return;
		}
		currentID = BIG_NUMBERS;
		suggestions.clear();
		for (int i = 100; i <= 900; i += 100) {
			suggestions.add(String.valueOf(i));
			suggestions.add(String.valueOf(i * 10));
		}
	}

	private static void calculateDrawX() {
		drawX = DevConsole.CONSOLE_X * Settings.scale + promptWidth + DevConsole.CONSOLE_PAD_X * Settings.scale
				+ textLength();
	}

	private static float textLength() {
		if (glyphs == null || DevConsole.currentText.isEmpty()) {
			return 0;
		} else {
			glyphs.setText(DevConsole.consoleFont, getTextWithoutRightmostToken(false));
			return glyphs.width;
		}
	}

	private static boolean shouldRenderInfo() {
		return noMatch || suggestionPairs.isEmpty() || suggestions.isEmpty() || !implementedYet || commandComplete;
	}

	public static void render(SpriteBatch sb) {
		DevConsole.consoleFont.setColor(TEXT_COLOR);
		if (shouldRenderInfo()) {
			sb.draw(DevConsole.consoleBackground, getBGX(), DevConsole.CONSOLE_Y * Settings.scale, getWidth(),
					-getHeight());
			String text = "[No Match found]";
			if (!implementedYet) {
				text = "[Not implemented yet]";
			}
			if (noMatch && (currentID == SMALL_NUMBERS || currentID == MEDIUM_NUMBERS || currentID == BIG_NUMBERS)) {
				text = "[Number]";
			}
			if (commandComplete) {
				text = "[Command is complete]";
			}
			DevConsole.consoleFont.draw(sb, text, drawX, DevConsole.CONSOLE_Y * Settings.scale);
		} else {
			Pair pair = suggestionPairs.peek();
			int amount = pair.end - selected;
			if (amount > MAX_SUGGESTIONS) {
				amount = MAX_SUGGESTIONS;
			}
			if (amount > pair.end - pair.start) {
				amount = pair.end - pair.start;
			}

			float y = (DevConsole.CONSOLE_Y * Settings.scale
					+ (float) Math.floor(DevConsole.CONSOLE_TEXT_SIZE * Settings.scale));

			// There's probably some easy Math to figure this out but somehow I can't get it
			// so this is the best I came up with
			int factor;
			for (factor = 1; factor <= amount; factor++) {
				int item = selected + pair.start + factor;
				if (item > pair.end || item >= suggestions.size()) {
					break;
				}
			}

			sb.draw(DevConsole.consoleBackground, getBGX(), DevConsole.CONSOLE_Y * Settings.scale, getWidth(),
					-getHeight() * factor);
			for (int i = 0; i <= amount; i++) {
				int item = selected + pair.start + i;
				if (item > pair.end || item >= suggestions.size()) {
					break;
				}
				y -= (float) Math.floor(DevConsole.CONSOLE_TEXT_SIZE * Settings.scale);
				DevConsole.consoleFont.draw(sb, suggestions.get(item), drawX, y);
			}
		}
		DevConsole.consoleFont.setColor(Color.WHITE);
	}

	private static float getBGX() {
		return drawX - DevConsole.CONSOLE_PAD_X * Settings.scale;
	}

	private static float getWidth() {
		return DevConsole.CONSOLE_W * Settings.scale - drawX + DevConsole.CONSOLE_X * Settings.scale
				+ DevConsole.CONSOLE_PAD_X * Settings.scale;
	}

	private static float getHeight() {
		return DevConsole.CONSOLE_TEXT_SIZE * Settings.scale;
	}
}
