package basemod;

import basemod.devcommands.ConsoleCommand;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;

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
	private static final Color HIGHLIGHT_COLOR = Color.LIGHT_GRAY.cpy();

	public static boolean enabled = true;
	public static int selectKey = Keys.SHIFT_LEFT;
	public static int deleteTokenKey = Keys.LEFT;
	public static int fillKey1 = Keys.RIGHT;
	public static int fillKey2 = Keys.TAB;
	public static int selected = 0;
	public static boolean addWhitespace = true;
	private static ArrayList<String> suggestions;
	private static Stack<Pair> suggestionPairs;
	private static String[] tokens;
	private static boolean foundStart, foundEnd, noMatch;
	private static int currentID;
	private static int lastLength = 0;
	private static int whiteSpaces = 0;
	private static int lastWhiteSpaces = 0;
	private static Pattern spacePattern = Pattern.compile(DevConsole.PATTERN);

	private static boolean implementedYet = true;
	private static float drawX;
	private static float promptWidth = 0;
	private static GlyphLayout glyphs;
	
	private static final char ID_DELIMITER = ':';
	private static final String PACKAGE_DELIMITER = ".";
	private static final String SPACE_AND_ID_DELIMITER = "[ :]";

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
			String textToInsert = suggestions.get(selected + suggestionPairs.peek().start);
			// if the text to complete contains a : (ID_DELIMITER) that the user has not typed yet
			if (textToInsert.lastIndexOf(ID_DELIMITER) > DevConsole.currentText.lastIndexOf(ID_DELIMITER)) {
				// complete up to that :
				DevConsole.currentText = getTextWithoutRightmostSpaceToken()
						+ textToInsert.substring(0, textToInsert.lastIndexOf(ID_DELIMITER)) + ID_DELIMITER;
			} else if (textToInsert.endsWith(PACKAGE_DELIMITER)) {
				//this is package autocomplete, no space
				String start = getTextWithoutRightmostSpaceToken();
				if ((start + textToInsert).length() == DevConsole.currentText.length() + 1)
				{
					DevConsole.currentText = start + textToInsert;
					reset();
				}
				else
				{
					DevConsole.currentText = start
							+ textToInsert.substring(0, textToInsert.lastIndexOf(PACKAGE_DELIMITER));
				}
			} else if (textToInsert.endsWith("(")) {
				DevConsole.currentText = getTextWithoutRightmostSpaceToken() + textToInsert;
				reset();
			} else {
				// otherwise complete the whole token
				DevConsole.currentText = getTextWithoutRightmostSpaceToken() + textToInsert + (addWhitespace ? " " : "");
				reset();
			}
			suggest(false);
		}
	}

	// returns the text without the rightmost token
	private static String getTextWithoutRightmostSpaceToken() {
		int lastSpace = DevConsole.currentText.lastIndexOf(' ');
		String text = "";
		if (lastSpace != -1) {
			text = DevConsole.currentText.substring(0, lastSpace + 1);
		}
		return text;
	}
	
	private static int lastIndexOfRegex(String currentText, String tokenDelimiter) {
		int index = -1;
		Matcher matcher = Pattern.compile(tokenDelimiter).matcher(currentText);
		while (matcher.find()) {
			index = matcher.start();
		}
		return index;
	}

	public static void removeOneTokenUsingSpaceAndIdDelimiter() {
		String text = "";
		int lastChar = lastIndexOfRegex(DevConsole.currentText, SPACE_AND_ID_DELIMITER);
		int curTextLength = DevConsole.currentText.length();
		if (lastChar != -1) {
			if (!DevConsole.currentText.isEmpty()) {
				// remove single space
				if (DevConsole.currentText.charAt(curTextLength - 1) == ' ') {
					text = DevConsole.currentText.substring(0, curTextLength - 1);
				} else if (DevConsole.currentText.charAt(curTextLength - 1) == ID_DELIMITER) {
					// remove token that has : at the end
					text = getTextWithoutRightmostSpaceToken();
				} else {
					// remove last : or space delimited token
					text = DevConsole.currentText.substring(0, lastChar + 1);
				}
			}
		}
		DevConsole.currentText = text;
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
		if(DevConsole.currentText.matches(".*\\s+")) {
			tokens = (DevConsole.currentText + "d").trim().split(DevConsole.PATTERN);
			tokens[tokens.length - 1] = "";
		} else {
			tokens = DevConsole.currentText.trim().split(DevConsole.PATTERN);
		}

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

	private static void createCMDSuggestions() {
		currentID = RESET + 1;
		addWhitespace = true;
		suggestions = ConsoleCommand.suggestions(tokens);
	}

	private static void calculateDrawX() {
		drawX = DevConsole.CONSOLE_X * Settings.scale + promptWidth + DevConsole.CONSOLE_PAD_X * Settings.scale
				+ textLength();
	}

	private static float textLength() {
		if (glyphs == null || DevConsole.currentText.isEmpty()) {
			return 0;
		} else {
			glyphs.setText(DevConsole.consoleFont, getTextWithoutRightmostSpaceToken());
			return glyphs.width;
		}
	}

	private static boolean shouldRenderInfo() {
		return suggestionPairs.isEmpty() || suggestions.isEmpty() || ConsoleCommand.errormsg != null || ConsoleCommand.complete;
	}

	public static void render(SpriteBatch sb) {
		DevConsole.consoleFont.setColor(HIGHLIGHT_COLOR);
		if (shouldRenderInfo()) {
			sb.draw(DevConsole.consoleBackground, getBGX(), DevConsole.CONSOLE_Y * Settings.scale, getWidth(),
					-getHeight());
			String text = "[No Match found]";
			if (!implementedYet) {
				text = "[Not implemented yet]";
			}
			if (suggestions.isEmpty() && ConsoleCommand.isNumber) {
				text = "[Number]";
			}
			if (suggestions.isEmpty() && ConsoleCommand.duringRun) {
				text = "[Only available during a run]";
			}
			if (ConsoleCommand.errormsg != null) {
				text = "[" + ConsoleCommand.errormsg + "]";
			}
			if (ConsoleCommand.complete) {
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
				DevConsole.consoleFont.setColor(TEXT_COLOR);
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
