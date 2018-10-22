package basemod;

import basemod.helpers.ConvertHelper;
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
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class DevConsole
implements PostEnergyRechargeSubscriber, PostInitializeSubscriber, PostRenderSubscriber, PostUpdateSubscriber {
	public static final Logger logger = LogManager.getLogger(DevConsole.class.getName());

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

	private static boolean infiniteEnergy = false;
	public static boolean forceUnlocks = false;
	public static int unlockLevel = -1;

	public static boolean enabled = false;
	public static boolean visible = false;
	public static int toggleKey = Keys.GRAVE;
	public static String currentText = "";

	public static int priorKey = Keys.UP;
	public static int nextKey = Keys.DOWN;
	public static ArrayList<String> priorCommands;
	public static ArrayList<String> log;
	public static ArrayList<Boolean> prompted;
	public static int commandPos;

	public DevConsole() {
		BaseMod.subscribe(this);

		priorCommands = new ArrayList<>();
		commandPos = -1;
		log = new ArrayList<>();
		prompted = new ArrayList<>();

		AutoComplete.init();
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

		switch (tokens[0].toLowerCase()) {
		case "relic": {
			cmdRelic(tokens);
			break;
		}
		case "hand": {
			cmdHand(tokens);
			break;
		}
		case "info": {
			Settings.isInfo = !Settings.isInfo;
			break;
		}
		case "kill": {
			cmdKill(tokens);
			break;
		}
		case "gold": {
			cmdGold(tokens);
			break;
		}
		case "energy": {
			cmdEnergy(tokens);
			break;
		}
		case "deck": {
			cmdDeck(tokens);
			break;
		}
		case "draw": {
			cmdDraw(tokens);
			break;
		}
		case "fight": {
			cmdFight(tokens);
			break;
		}
		case "event": {
			cmdEvent(tokens);
			break;
		}
		case "potion": {
			cmdPotion(tokens);
			break;
		}
		case "unlock": {
			cmdUnlock(tokens);
			break;
		}
		case "power": {
			cmdPower(tokens);
			break;
		}
		case "clear": {
			cmdClear(tokens);
			break;
		}
		case "help": {
			cmdHelp();
			break;
		}
		case "hp": {
			cmdHP(tokens);
			break;
		}
		case "maxhp": {
			cmdMaxHP(tokens);
			break;
		}
		case "debug":{
			cmdDebugMode(tokens);
			break;
		}	
		default: {
			log("invalid command");
			break;
		}
		}
	}
	
	private static void cmdDebugMode(String[] tokens) {
		if(tokens.length == 2 && (tokens[1].equals("true") || tokens[1].equals("false"))) {
			try {
				Settings.isDebug = Boolean.parseBoolean(tokens[1]);
				log("Setting debug mode to: " + Settings.isDebug);
			} catch(Exception e) {
				couldNotParse();
				log("options are:");
				log("* true");
				log("* false");
			}
		}else {
			couldNotParse();
			log("options are:");
			log("* true");
			log("* false");
		}
	}

	private static void cmdClear(String[] tokens) {
		if (tokens.length < 2) {
			clearLog();
			clearCmds();
		} else if (tokens[1].equals("log")) {
			clearLog();
		} else if (tokens[1].equals("cmd")) {
			clearCmds();
		} else {
			cmdClearHelp();
		}
	}

	// clear log
	private static void clearLog() {
		while (log.size() > 0) {
			log.remove(0);
		}
		while (prompted.size() > 0) {
			prompted.remove(0);
		}
	}

	// clear command list
	private static void clearCmds() {
		while (priorCommands.size() > 0) {
			priorCommands.remove(0);
		}
	}

	private static void cmdClearHelp() {
		couldNotParse();
		log("options are:");
		log("* log");
		log("* cmd");
	}

	// print help info
	private static void cmdHelp() {
		log("options are: relic hand info kill gold energy deck...");
		log("draw fight event potion unlock power clear help hp maxhp");
	}

	private static void cmdPower(String[] tokens) {
		if (tokens.length < 2)  {
			cmdPowerHelp();
			return;
		}

		String powerID = "";
		int amount = 1;
		for (int i = 1; i < tokens.length - 1; i++) {
			powerID = powerID.concat(tokens[i]).concat(" ");
		}
		try {
			amount = Integer.parseInt(tokens[tokens.length - 1]);
		} catch (Exception e) {
			powerID = powerID.concat(tokens[tokens.length - 1]);
		}
		powerID = powerID.trim();
		
		// If the ID was written using underscores, find the original ID
		if (BaseMod.underScorePowerIDs.containsKey(powerID)) {
			powerID = BaseMod.underScorePowerIDs.get(powerID);
		}

		Class power;
		try {
			power = BaseMod.getPowerClass(powerID);
		} catch (Exception e) {
			logger.info("failed to load power " + powerID);
			log("could not load power");
			cmdPowerHelp();
			return;
		}

		try {
			new ConsoleTargetedPower(power, amount);
		} catch (Exception e) {
			log("could not make power");
			cmdPowerHelp();
		}
	}

	private static void cmdPowerHelp() {
		couldNotParse();
		log("options are:");
		log("* [id] [amt]");
	}
	
	private static String getRelicName(String[] relicNameArray) {
		String relic = String.join(" ", relicNameArray);
		if (BaseMod.underScoreRelicIDs.containsKey(relic)) {
			relic = BaseMod.underScoreRelicIDs.get(relic);
		}
		return relic;
	}

	private static void cmdRelic(String[] tokens) {
		if (AbstractDungeon.player != null) {
			if (tokens.length < 2) {
				cmdRelicHelp();
				return;
			}

			if ((tokens[1].toLowerCase().equals("remove") || tokens[1].toLowerCase().equals("r"))
					&& tokens.length > 2) {
				String[] relicNameArray = Arrays.copyOfRange(tokens, 2, tokens.length);
				String relicName = getRelicName(relicNameArray);
				AbstractDungeon.player.loseRelic(relicName);
			} else if ((tokens[1].toLowerCase().equals("add")  || tokens[1].toLowerCase().equals("a"))
					&& tokens.length > 2) {
				String[] relicNameArray = Arrays.copyOfRange(tokens, 2, tokens.length);
				String relicName = getRelicName(relicNameArray);
				AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2, Settings.HEIGHT / 2,
						RelicLibrary.getRelic(relicName).makeCopy());
			} else if (tokens[1].toLowerCase().equals("desc") && tokens.length > 2) {
				String[] relicNameArray = Arrays.copyOfRange(tokens, 2, tokens.length);
				String relicName = getRelicName(relicNameArray);
				log(RelicLibrary.getRelic(relicName).description);
			} else if (tokens[1].toLowerCase().equals("flavor") && tokens.length > 2) {
				String[] relicNameArray = Arrays.copyOfRange(tokens, 2, tokens.length);
				String relicName = getRelicName(relicNameArray);
				log(RelicLibrary.getRelic(relicName).flavorText);
			} else if (tokens[1].toLowerCase().equals("pool") && tokens.length > 2) {
				String[] relicNameArray = Arrays.copyOfRange(tokens, 2, tokens.length);
				String relicName = getRelicName(relicNameArray);
				log(RelicLibrary.getRelic(relicName).tier.toString());
			} else if (tokens[1].toLowerCase().equals("list")) {
				if (tokens.length < 3) {
					cmdRelicListHelp();
					return;
				}
				switch (tokens[2]) {
				case "starter": {
					Collections.sort(RelicLibrary.starterList);
					logger.info(RelicLibrary.starterList);
					log(RelicLibrary.starterList);
					break;
				}
				case "common": {
					Collections.sort(RelicLibrary.commonList);
					logger.info(RelicLibrary.commonList);
					log(RelicLibrary.commonList);
					break;
				}
				case "uncommon": {
					Collections.sort(RelicLibrary.uncommonList);
					logger.info(RelicLibrary.uncommonList);
					log(RelicLibrary.uncommonList);
					break;
				}
				case "rare": {
					Collections.sort(RelicLibrary.rareList);
					logger.info(RelicLibrary.rareList);
					log(RelicLibrary.rareList);
				}
				case "boss": {
					Collections.sort(RelicLibrary.bossList);
					logger.info(RelicLibrary.bossList);
					log(RelicLibrary.bossList);
					break;
				}
				case "special": {
					Collections.sort(RelicLibrary.specialList);
					logger.info(RelicLibrary.specialList);
					log(RelicLibrary.specialList);
					break;
				}
				case "shop": {
					Collections.sort(RelicLibrary.shopList);
					logger.info(RelicLibrary.shopList);
					log(RelicLibrary.shopList);
					break;
				}
				default: {
					cmdRelicListHelp();
				}
				}
			} else {
				cmdRelicHelp();
			}
		}
	}

	private static void cmdRelicHelp() {
		couldNotParse();
		log("options are:");
		log("* remove [id]");
		log("* add [id]");
		log("* desc [id]");
		log("* flavor [id]");
		log("* pool [id]");
		log("* list [type]");
	}

	private static void cmdRelicListHelp() {
		log("options are: starter common uncommon rare boss special shop");
	}

	private static void cmdHand(String[] tokens) {
		if (AbstractDungeon.player != null) {
			if (tokens.length < 3) {
				cmdHandHelp();
				return;
			}

			int countIndex = tokens.length - 1;
			while (ConvertHelper.tryParseInt(tokens[countIndex], 0) != 0) {
				countIndex--;
			}

			String[] cardNameArray = Arrays.copyOfRange(tokens, 2, countIndex + 1);
			String cardName = String.join(" ", cardNameArray);

			// If the ID was written using underscores, find the original ID
			if (BaseMod.underScoreCardIDs.containsKey(cardName)) {
				cardName = BaseMod.underScoreCardIDs.get(cardName);
			}

			if (tokens[1].toLowerCase().equals("add") || tokens[1].toLowerCase().equals("a")) {
				AbstractCard c = CardLibrary.getCard(cardName);
				if (c != null) {
					// card count
					int count = 1;
					if (tokens.length > countIndex + 1 && ConvertHelper.tryParseInt(tokens[countIndex + 1], 0) != 0) {
						count = ConvertHelper.tryParseInt(tokens[countIndex + 1], 0);
					}

					int upgradeCount = 0;
					if (tokens.length > countIndex + 2) {
						upgradeCount = ConvertHelper.tryParseInt(tokens[countIndex + 2], 0);
					}

					log("adding " + count + (count == 1 ? " copy of " : " copies of ") + cardName + " with " + upgradeCount + " upgrade(s)");

					for (int i = 0; i < count; i++) {
						AbstractCard copy = c.makeCopy();
						for (int j = 0; j < upgradeCount; j++) {
							copy.upgrade();
						}

						AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(copy, true));
					}
				} else {
					log("could not find card " + cardName);
				}
			} else if (tokens[1].toLowerCase().equals("remove") || tokens[1].toLowerCase().equals("r")) {
				// remove all cards
				if (tokens[2].equals("all")) {
					for (AbstractCard c : new ArrayList<>(AbstractDungeon.player.hand.group)) {
						AbstractDungeon.player.hand.moveToExhaustPile(c);
					}
					return;
					// remove single card
				} else {
					boolean removed = false;
					AbstractCard toRemove = null;
					for (AbstractCard c : AbstractDungeon.player.hand.group) {
						if (removed) {
							break;
						}
						if (c.cardID.equals(cardName)) {
							toRemove = c;
							removed = true;
						}
					}
					if (removed) {
						AbstractDungeon.player.hand.moveToExhaustPile(toRemove);
					}
				}
			} else if (tokens[1].toLowerCase().equals("discard") || tokens[1].toLowerCase().equals("d")) {
				if (tokens[2].equals("all")) {
					// discard all cards
					for (AbstractCard c : new ArrayList<>(AbstractDungeon.player.hand.group)) {
						AbstractDungeon.player.hand.moveToDiscardPile(c);
						c.triggerOnManualDiscard();
						GameActionManager.incrementDiscard(false);
					}
				} else {
					// discard single card
					for (AbstractCard c : AbstractDungeon.player.hand.group) {
						if (c.cardID.equals(cardName)) {
							AbstractDungeon.player.hand.moveToDiscardPile(c);
							c.triggerOnManualDiscard();
							GameActionManager.incrementDiscard(false);
							return;
						}
					}
				}
			} else if ((tokens[1].equalsIgnoreCase("set") || tokens[1].equalsIgnoreCase("s")) &&
			           (tokens[2].equalsIgnoreCase("damage") || tokens[2].equalsIgnoreCase("block") || tokens[2].equalsIgnoreCase("magic") || tokens[2].equalsIgnoreCase("cost") || tokens[2].equalsIgnoreCase("d") || tokens[2].equalsIgnoreCase("b") || tokens[2].equalsIgnoreCase("m") || tokens[2].equalsIgnoreCase("c")) && tokens.length == 5) {
				try{
					cardNameArray = Arrays.copyOfRange(tokens, 3, countIndex + 1);
					cardName = String.join(" ", cardNameArray);
					boolean all = tokens[3].equals("all");
					int v = Integer.parseInt(tokens[4]);
					for (AbstractCard c : new ArrayList<>(AbstractDungeon.player.hand.group)) {
						if (all || c.cardID.equals(cardName)) {
							if (tokens[2].equalsIgnoreCase("damage") || tokens[2].equalsIgnoreCase("d")) {
								if (c.baseDamage != v) c.upgradedDamage = true;
								c.baseDamage = v;
							}
							if (tokens[2].equalsIgnoreCase("block") || tokens[2].equalsIgnoreCase("b")) {
								if (c.baseBlock != v) c.upgradedBlock = true;
								c.baseBlock = v;
							}
							if (tokens[2].equalsIgnoreCase("magic") || tokens[2].equalsIgnoreCase("m")) {
								if (c.baseMagicNumber != v) c.upgradedMagicNumber = true;
								c.magicNumber = c.baseMagicNumber = v;
							}
							if (tokens[2].equalsIgnoreCase("cost") || tokens[2].equalsIgnoreCase("c")) {
								if (c.cost != v) c.upgradedCost = true;
								c.cost = v;
							}
							c.displayUpgrades();
							c.applyPowers();
							if (!all) break;
						}
					}
				} catch (NumberFormatException e) {
					cmdHandHelp();
				}
			} else {
				cmdHandHelp();
			}
		} else {
			log("cannot add cards when player doesn't exist");
		}
	}

	private static void cmdHandHelp() {
		couldNotParse();
		log("options are:");
		log("* add [id] {count} {upgrade amt}");
		log("* remove [id]");
		log("* remove all");
		log("* discard [id]");
		log("* discard all");
		log("* set damage [id] [amount]");
		log("* set block [id] [amount]");
		log("* set magic [id] [amount]");
		log("* set cost [id] [amount]");
	}

	private static void cmdKill(String[] tokens) {
		if (AbstractDungeon.getCurrRoom().monsters != null) {
			if (tokens.length != 2) {
				cmdKillHelp();
				return;
			}

			if (tokens[1].toLowerCase().equals("all")) {
				int monsterCount = AbstractDungeon.getCurrRoom().monsters.monsters.size();
				int[] multiDamage = new int[monsterCount];
				for (int i = 0; i < monsterCount; ++i) {
					multiDamage[i] = 999;
				}

				AbstractDungeon.actionManager.addToTop(new DamageAllEnemiesAction(AbstractDungeon.player, multiDamage,
						DamageInfo.DamageType.HP_LOSS, AbstractGameAction.AttackEffect.NONE));
			} else if (tokens[1].toLowerCase().equals("self")) {
				AbstractDungeon.actionManager
				.addToTop(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, 999));
			} else {
				cmdKillHelp();
			}
		}
	}

	private static void cmdKillHelp() {
		couldNotParse();
		log("options are:");
		log("* all");
		log("* self");
	}

	private static void cmdHP(String[] tokens) {
		if (tokens.length < 2) {
			cmdHPHelp();
			return;
		}

		if ((tokens[1].toLowerCase().equals("add") || tokens[1].toLowerCase().equals("a"))
				&& tokens.length > 2) {
			int i;
			try {
				i = Integer.parseInt(tokens[2]);
				AbstractDungeon.actionManager
				.addToTop(new HealAction(AbstractDungeon.player, AbstractDungeon.player, i));
			} catch (Exception e) {
				cmdHPHelp();
			}
		} else if ((tokens[1].toLowerCase().equals("lose") || tokens[1].toLowerCase().equals("l"))
				&& tokens.length > 2) {
			int i;
			try {
				i = Integer.parseInt(tokens[2]);
				AbstractDungeon.actionManager.addToTop(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player,
						i, AbstractGameAction.AttackEffect.NONE));
			} catch (Exception e) {
				cmdHPHelp();
			}
		} else {
			cmdHPHelp();
		}
	}

	private static void cmdHPHelp() {
		couldNotParse();
		log("options are:");
		log("* add [amt]");
		log("* lose [amt]");
	}

	private static void cmdMaxHP(String[] tokens) {
		if (tokens.length < 2) {
			cmdMaxHPHelp();
			return;
		}
		if ((tokens[1].toLowerCase().equals("add") || tokens[1].toLowerCase().equals("a"))
				&& tokens.length > 2) {
			int i;
			try {
				i = Integer.parseInt(tokens[2]);
				AbstractDungeon.player.increaseMaxHp(i, true);
			} catch (Exception e) {
				cmdMaxHPHelp();
			}
		} else if ((tokens[1].toLowerCase().equals("lose") || tokens[1].toLowerCase().equals("l"))
				&& tokens.length > 2) {
			int i;
			try {
				i = Integer.parseInt(tokens[2]);
				AbstractDungeon.player.decreaseMaxHealth(i);
			} catch (Exception e) {
				cmdMaxHPHelp();
			}
		} else {
			cmdMaxHPHelp();
		}
	}

	private static void cmdMaxHPHelp() {
		couldNotParse();
		log("options are:");
		log("* add [amt]");
		log("* lose [amt]");
	}

	private static void cmdGold(String[] tokens) {
		if (AbstractDungeon.player != null) {
			if (tokens.length != 3) {
				cmdGoldHelp();
				return;
			}

			int amount = ConvertHelper.tryParseInt(tokens[2], 0);
			if (tokens[1].toLowerCase().equals("add") || tokens[1].toLowerCase().equals("a")) {
				AbstractDungeon.player.displayGold += amount;
				AbstractDungeon.player.gainGold(amount);
			} else if (tokens[1].toLowerCase().equals("lose") || tokens[1].toLowerCase().equals("l")) {
				AbstractDungeon.player.displayGold = Math.max(AbstractDungeon.player.displayGold - amount, 0);
				AbstractDungeon.player.loseGold(amount);
			} else {
				cmdGoldHelp();
			}
		}
	}

	private static void cmdGoldHelp() {
		couldNotParse();
		log("options are:");
		log("* add [amt]");
		log("* lose [amt]");
	}

	private static void cmdEnergy(String[] tokens) {
		if (AbstractDungeon.player != null) {
			if (tokens.length < 2) {
				cmdEnergyHelp();
				return;
			}

			if (tokens[1].toLowerCase().equals("add") && tokens.length > 2) {
				AbstractDungeon.player.gainEnergy(ConvertHelper.tryParseInt(tokens[2], 0));
			} else if (tokens[1].toLowerCase().equals("lose") && tokens.length > 2) {
				AbstractDungeon.player.loseEnergy(ConvertHelper.tryParseInt(tokens[2], 0));
			} else if (tokens[1].toLowerCase().equals("inf")) {
				infiniteEnergy = !infiniteEnergy;
				if (infiniteEnergy) {
					AbstractDungeon.player.gainEnergy(9999);
				}
			} else {
				cmdEnergyHelp();
			}
		}
	}

	private static void cmdEnergyHelp() {
		couldNotParse();
		log("options are:");
		log("* add [amt]");
		log("* lose [amt]");
		log("* inf");
	}

	private static void couldNotParse() {
		log("could not parse previous command");
	}

	private static void cmdUnlock(String[] tokens) {
		if (tokens.length < 2) {
			return;
		}

		if (tokens[1].toLowerCase().equals("always")) {
			forceUnlocks = !forceUnlocks;
		} else if (tokens[1].toLowerCase().equals("level") && tokens.length > 2) {
			unlockLevel = ConvertHelper.tryParseInt(tokens[2]);
		}
	}

	private static void cmdDeck(String[] tokens) {
		if (AbstractDungeon.player != null) {
			if (tokens.length < 3) {
				cmdDeckHelp();
				return;
			}

			int countIndex = tokens.length - 1;
			while (ConvertHelper.tryParseInt(tokens[countIndex], 0) != 0) {
				countIndex--;
			}

			String[] cardNameArray = Arrays.copyOfRange(tokens, 2, countIndex + 1);
			String cardName = String.join(" ", cardNameArray);

			// If the ID was written using underscores, find the original ID
			if (BaseMod.underScoreCardIDs.containsKey(cardName)) {
				cardName = BaseMod.underScoreCardIDs.get(cardName);
			}

			if (tokens[1].toLowerCase().equals("add") || tokens[1].toLowerCase().equals("a")) {
				AbstractCard c = CardLibrary.getCard(cardName);
				if (c != null) {
					// card count
					int count = 1;
					if (tokens.length > countIndex + 1 && ConvertHelper.tryParseInt(tokens[countIndex + 1], 0) != 0) {
						count = ConvertHelper.tryParseInt(tokens[countIndex + 1], 0);
					}

					int upgradeCount = 0;
					if (tokens.length > countIndex + 2) {
						upgradeCount = ConvertHelper.tryParseInt(tokens[countIndex + 2], 0);
					}

					log("adding " + count + (count == 1 ? " copy of " : " copies of ") + cardName + " with " + upgradeCount + " upgrade(s)");

					for (int i = 0; i < count; i++) {
						AbstractCard copy = c.makeCopy();
						for (int j = 0; j < upgradeCount; j++) {
							copy.upgrade();
						}

						UnlockTracker.markCardAsSeen(copy.cardID);
						AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(copy, Settings.WIDTH / 2.0f,
								Settings.HEIGHT / 2.0f));
					}
				} else {
					log("could not find card " + cardName);
				}
			} else if (tokens[1].toLowerCase().equals("remove") || tokens[1].toLowerCase().equals("r")) {
				// remove all cards
				if (tokens[2].equals("all")) {
					for (String str : AbstractDungeon.player.masterDeck.getCardNames()) {
						AbstractDungeon.player.masterDeck.removeCard(str);
					}
					// remove single card
				} else {
					AbstractDungeon.player.masterDeck.removeCard(cardName);
				}
			} else {
				cmdDeckHelp();
			}
		} else {
			log("cannot add cards when player doesn't exist");
		}
	}

	private static void cmdDeckHelp() {
		couldNotParse();
		log("options are:");
		log("* add [id] {count} {upgrade amt}");
		log("* remove [id]");
		log("* remove all");
	}

	private static void cmdDraw(String[] tokens) {
		if (AbstractDungeon.getCurrRoom().monsters != null) {
			if (tokens.length != 2) {
				cmdDrawHelp();
				return;
			}

			AbstractDungeon.actionManager
			.addToTop(new DrawCardAction(AbstractDungeon.player, ConvertHelper.tryParseInt(tokens[1], 0)));
		} else {
			log("cannot draw when not in combat");
		}
	}

	private static void cmdDrawHelp() {
		couldNotParse();
		log("options are:");
		log("* draw [amt]");
	}

	private static void cmdFight(String[] tokens) {
		if (tokens.length < 2) {
			return;
		}

		String[] encounterArray = Arrays.copyOfRange(tokens, 1, tokens.length);
		String encounterName = String.join(" ", encounterArray);
		// If the ID was written using underscores, find the original ID
		if (BaseMod.underScoreEncounterIDs.containsKey(encounterName)) {
			encounterName = BaseMod.underScoreEncounterIDs.get(encounterName);
		}
		if (AbstractDungeon.getCurrRoom() instanceof MonsterRoom) {
			// Note: AbstractDungeon.nextRoomTransition() will remove the encounter of the current room from the monster list
			// so if we want the new encounter to be in the front afterwards for our new MonsterRoom, we should insert the encounter at position 1, not 0
			AbstractDungeon.monsterList.add(1, encounterName);
		} else {
			AbstractDungeon.monsterList.add(0, encounterName);
		}

		MapRoomNode cur = AbstractDungeon.currMapNode;
		MapRoomNode node = new MapRoomNode(cur.x, cur.y);
		node.room = new MonsterRoom();

		ArrayList<MapEdge> curEdges = cur.getEdges();
		for (MapEdge edge : curEdges) {
			node.addEdge(edge);
		}

		AbstractDungeon.nextRoom = node;
		AbstractDungeon.nextRoomTransitionStart();
	}

	private static void cmdEvent(String[] tokens) {
		if (tokens.length < 2) {
			return;
		}

		String[] eventArray = Arrays.copyOfRange(tokens, 1, tokens.length);
		String eventName = String.join(" ", eventArray);

		// If the ID was written using underscores, find the original ID
		if (BaseMod.underScoreEventIDs.containsKey(eventName)) {
			eventName = BaseMod.underScoreEventIDs.get(eventName);
		}
		
		if (EventHelper.getEvent(eventName) == null) {
			couldNotParse();
			log(eventName + " is not an event ID");
			return;
		}

		AbstractDungeon.eventList.add(0, eventName);

		MapRoomNode cur = AbstractDungeon.currMapNode;
		MapRoomNode node = new MapRoomNode(cur.x, cur.y);
		node.room = new CustomEventRoom();

		ArrayList<MapEdge> curEdges = cur.getEdges();
		for (MapEdge edge : curEdges) {
			node.addEdge(edge);
		}

		AbstractDungeon.previousScreen = null;
		AbstractDungeon.dynamicBanner.hide();
		AbstractDungeon.dungeonMapScreen.closeInstantly();
		AbstractDungeon.closeCurrentScreen();
		AbstractDungeon.topPanel.unhoverHitboxes();
		AbstractDungeon.fadeIn();
		AbstractDungeon.effectList.clear();
		AbstractDungeon.topLevelEffects.clear();
		AbstractDungeon.topLevelEffectsQueue.clear();
		AbstractDungeon.effectsQueue.clear();
		AbstractDungeon.dungeonMapScreen.dismissable = true;
		AbstractDungeon.nextRoom = node;
		AbstractDungeon.setCurrMapNode(node);
		AbstractDungeon.getCurrRoom().onPlayerEntry();
		AbstractDungeon.scene.nextRoom(node.room);
		AbstractDungeon.rs = AbstractDungeon.RenderScene.EVENT;
	}

	private static void cmdPotion(String[] tokens) {
		if (tokens.length < 2) {
			cmdPotionHelp();
			return;
		}

		int i;
		try {
			i = Integer.parseInt(tokens[1]);
		} catch (Exception e) {
			// check if we want to list potions
			if (tokens[1].equals("list")) {
				Collections.sort(PotionHelper.potions);
				log(PotionHelper.potions);
			} else {
				cmdPotionHelp();
			}
			return;
		}

		String potionID = "";
		for (int k = 2; k < tokens.length; k++) {
			potionID = potionID.concat(tokens[k]);
			if (k != tokens.length - 1) {
				potionID = potionID.concat(" ");
			}
		}
		// If the ID was written using underscores, find the original ID
		if (BaseMod.underScorePotionIDs.containsKey(potionID)) {
			potionID = BaseMod.underScorePotionIDs.get(potionID);
		}

		AbstractPotion p = null;
		if (PotionHelper.potions.contains(potionID)) {
			p = PotionHelper.getPotion(potionID);
		}
		if (PotionHelper.potions.contains(potionID + " Potion")) {
			p = PotionHelper.getPotion(potionID + " Potion");
		}

		if (p == null) {
			log("invalid potion id");
			log("use potion list to see valid ids");
			return;
		}

		AbstractDungeon.player.obtainPotion(i, p);
	}

	public static void cmdPotionHelp() {
		couldNotParse();
		log("options are:");
		log("* list");
		log("* [slot] [id]");
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

	public static void log(@SuppressWarnings("rawtypes") ArrayList list) {
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
			currentText = AutoComplete.getTextWithoutRightmostToken(true);
			if (AutoComplete.enabled) {
				AutoComplete.suggest(false);
			}
		}
	}
}
