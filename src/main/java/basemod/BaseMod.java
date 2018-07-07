package basemod;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;

import basemod.interfaces.*;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.screens.custom.CustomModeCharacterButton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.classutil.AbstractClassFilter;
import org.clapper.util.classutil.AndClassFilter;
import org.clapper.util.classutil.ClassFilter;
import org.clapper.util.classutil.ClassFinder;
import org.clapper.util.classutil.ClassInfo;
import org.clapper.util.classutil.FieldInfo;
import org.clapper.util.classutil.InterfaceOnlyClassFilter;
import org.clapper.util.classutil.NotClassFilter;
import org.clapper.util.classutil.RegexClassFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Version;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.characters.Ironclad;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.AchievementStrings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.localization.CreditStrings;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.KeywordStrings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.OrbStrings;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.ScoreBonusStrings;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import basemod.abstracts.CustomCard;
import basemod.abstracts.CustomUnlockBundle;
import basemod.abstracts.DynamicVariable;
import basemod.helpers.RelicType;
import basemod.helpers.dynamicvariables.BlockVariable;
import basemod.helpers.dynamicvariables.DamageVariable;
import basemod.helpers.dynamicvariables.MagicNumberVariable;
import basemod.screens.ModalChoiceScreen;

@SpireInitializer
public class BaseMod {
	public static final Logger logger = LogManager.getLogger(BaseMod.class.getName());

	private static final int BADGES_PER_ROW = 16;
	private static final float BADGES_X = 640.0f;
	private static final float BADGES_Y = 250.0f;
	public static final float BADGE_W = 40.0f;
	public static final float BADGE_H = 40.0f;

	private static HashMap<Type, String> typeMaps;
	private static HashMap<Type, Type> typeTokens;

	private static ArrayList<ModBadge> modBadges;

	private static ArrayList<ISubscriber> toRemove;
	private static ArrayList<StartActSubscriber> startActSubscribers;
	private static ArrayList<PostCampfireSubscriber> postCampfireSubscribers;
	private static ArrayList<PostDrawSubscriber> postDrawSubscribers;
	private static ArrayList<PostExhaustSubscriber> postExhaustSubscribers;
	private static ArrayList<OnCardUseSubscriber> onCardUseSubscribers;
	private static ArrayList<PostDungeonInitializeSubscriber> postDungeonInitializeSubscribers;
	private static ArrayList<PostEnergyRechargeSubscriber> postEnergyRechargeSubscribers;
	private static ArrayList<PostInitializeSubscriber> postInitializeSubscribers;
	private static ArrayList<PreMonsterTurnSubscriber> preMonsterTurnSubscribers;
	private static ArrayList<RenderSubscriber> renderSubscribers;
	private static ArrayList<PreRenderSubscriber> preRenderSubscribers;
	private static ArrayList<PostRenderSubscriber> postRenderSubscribers;
	private static ArrayList<ModelRenderSubscriber> modelRenderSubscribers;
	private static ArrayList<PreStartGameSubscriber> preStartGameSubscribers;
	private static ArrayList<StartGameSubscriber> startGameSubscribers;
	private static ArrayList<PreUpdateSubscriber> preUpdateSubscribers;
	private static ArrayList<PostUpdateSubscriber> postUpdateSubscribers;
	private static ArrayList<PostCreateStartingDeckSubscriber> postCreateStartingDeckSubscribers;
	private static ArrayList<PostCreateStartingRelicsSubscriber> postCreateStartingRelicsSubscribers;
	private static ArrayList<PostCreateShopRelicSubscriber> postCreateShopRelicSubscribers;
	private static ArrayList<PostCreateShopPotionSubscriber> postCreateShopPotionSubscribers;
	private static ArrayList<EditCardsSubscriber> editCardsSubscribers;
	private static ArrayList<EditRelicsSubscriber> editRelicsSubscribers;
	private static ArrayList<EditCharactersSubscriber> editCharactersSubscribers;
	private static ArrayList<EditStringsSubscriber> editStringsSubscribers;
	private static ArrayList<EditKeywordsSubscriber> editKeywordsSubscribers;
	private static ArrayList<PostBattleSubscriber> postBattleSubscribers;
	private static ArrayList<SetUnlocksSubscriber> setUnlocksSubscribers;
	private static ArrayList<PostPotionUseSubscriber> postPotionUseSubscribers;
	private static ArrayList<PrePotionUseSubscriber> prePotionUseSubscribers;
	private static ArrayList<PotionGetSubscriber> potionGetSubscribers;
	private static ArrayList<RelicGetSubscriber> relicGetSubscribers;
	private static ArrayList<PostPowerApplySubscriber> postPowerApplySubscribers;
	private static ArrayList<OnPowersModifiedSubscriber> onPowersModifiedSubscribers;
	private static ArrayList<PostDeathSubscriber> postDeathSubscribers;
	private static ArrayList<OnStartBattleSubscriber> startBattleSubscribers;

	private static ArrayList<AbstractCard> redToAdd;
	private static ArrayList<String> redToRemove;
	private static ArrayList<AbstractCard> greenToAdd;
	private static ArrayList<String> greenToRemove;
	private static ArrayList<AbstractCard> colorlessToAdd;
	private static ArrayList<String> colorlessToRemove;
	private static ArrayList<AbstractCard> curseToAdd;
	private static ArrayList<String> curseToRemove;
	private static ArrayList<AbstractCard> customToAdd;
	private static ArrayList<String> customToRemove;
	private static ArrayList<String> customToRemoveColors;

	private static HashMap<String, HashMap<String, AbstractRelic>> customRelicPools;
	private static HashMap<String, ArrayList<AbstractRelic>> customRelicLists;

	private static ArrayList<String> potionsToRemove;

	@SuppressWarnings("rawtypes")
	public static HashMap<String, Class> playerClassMap;
	public static HashMap<String, String> playerTitleStringMap;
	public static HashMap<String, String> playerClassStringMap;
	public static HashMap<String, String> playerColorMap;
	public static HashMap<String, String> playerSelectTextMap;
	public static HashMap<String, String> playerSelectButtonMap;
	public static HashMap<String, String> playerPortraitMap;

	public static HashMap<String, CharStat> playerStatsMap;

	public static HashMap<String, DynamicVariable> cardDynamicVariableMap = new HashMap<>();

	@SuppressWarnings("rawtypes")
	private static HashMap<String, Class> potionClassMap;
	private static HashMap<String, Color> potionHybridColorMap;
	private static HashMap<String, Color> potionLiquidColorMap;
	private static HashMap<String, Color> potionSpotsColorMap;

	@SuppressWarnings("rawtypes")
	private static HashMap<String, Class> powerMap;

	public static ArrayList<String> encounterList;
	public static HashMap<String, String> underScoreEncounterIDs;
	public static HashMap<String, String> underScoreCardIDs;
	public static HashMap<String, String> underScorePotionIDs;
	public static HashMap<String, String> underScorePowerIDs;
	public static HashMap<String, String> underScoreEventIDs;
	public static HashMap<String, String> underScoreRelicIDs;

	private static HashMap<String, com.badlogic.gdx.graphics.Color> colorBgColorMap;
	private static HashMap<String, com.badlogic.gdx.graphics.Color> colorBackColorMap;
	private static HashMap<String, com.badlogic.gdx.graphics.Color> colorFrameColorMap;
	private static HashMap<String, com.badlogic.gdx.graphics.Color> colorFrameOutlineColorMap;
	private static HashMap<String, com.badlogic.gdx.graphics.Color> colorDescBoxColorMap;
	private static HashMap<String, com.badlogic.gdx.graphics.Color> colorTrailVfxMap;
	private static HashMap<String, com.badlogic.gdx.graphics.Color> colorGlowColorMap;
	private static HashMap<String, Integer> colorCardCountMap;
	private static HashMap<String, Integer> colorCardSeenCountMap;
	private static HashMap<String, String> colorAttackBgMap;
	private static HashMap<String, String> colorSkillBgMap;
	private static HashMap<String, String> colorPowerBgMap;
	private static HashMap<String, String> colorEnergyOrbMap;
	private static HashMap<String, String> colorAttackBgPortraitMap;
	private static HashMap<String, String> colorSkillBgPortraitMap;
	private static HashMap<String, String> colorPowerBgPortraitMap;
	private static HashMap<String, String> colorEnergyOrbPortraitMap;
	private static HashMap<String, com.badlogic.gdx.graphics.Texture> colorAttackBgTextureMap;
	private static HashMap<String, com.badlogic.gdx.graphics.Texture> colorSkillBgTextureMap;
	private static HashMap<String, com.badlogic.gdx.graphics.Texture> colorPowerBgTextureMap;
	private static HashMap<String, com.badlogic.gdx.graphics.Texture> colorEnergyOrbTextureMap;
	private static HashMap<String, com.badlogic.gdx.graphics.Texture> colorAttackBgPortraitTextureMap;
	private static HashMap<String, com.badlogic.gdx.graphics.Texture> colorSkillBgPortraitTextureMap;
	private static HashMap<String, com.badlogic.gdx.graphics.Texture> colorPowerBgPortraitTextureMap;
	private static HashMap<String, com.badlogic.gdx.graphics.Texture> colorEnergyOrbPortraitTextureMap;

	private static HashMap<AbstractPlayer.PlayerClass, HashMap<Integer, CustomUnlockBundle>> unlockBundles;

	private static OrthographicCamera animationCamera;
	private static ModelBatch batch;
	private static Environment animationEnvironment;
	private static FrameBuffer animationBuffer;
	private static Texture animationTexture;
	private static TextureRegion animationTextureRegion;

	public static final String CONFIG_FILE = "basemod-config";
	private static Object config;
	private static Class<?> spireConfig;

	/* should be final but the compiler doesn't like me */
	public static String save_path = "saves" + File.separator;

	public static DevConsole console;
	public static Gson gson;
	public static boolean modSettingsUp = false;

	// Map generation
	public static float mapPathDensityMultiplier = 1.0f;

	// Text input

	private static ModTextPanel textPanel;

	public static ModalChoiceScreen modalChoiceScreen = new ModalChoiceScreen();

	//
	// Initialization
	//

	private static Object maybeGetConfig() {
		Properties defaultProperties = new Properties();
		defaultProperties.setProperty("console-key", "`");
		Object configObject;

		try {
			spireConfig = Class.forName("com.evacipated.cardcrawl.modthespire.lib.SpireConfig");
			configObject = spireConfig.getDeclaredConstructor(String.class, String.class, Properties.class)
					.newInstance(BaseModInit.MODNAME, CONFIG_FILE, defaultProperties);
			return configObject;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			logger.info(
					"could not find and/or initialize SpireConfig - persistent config for BaseMod only available for MTS version 2.5.0+");
			spireConfig = null;
			return null;
		}
	}

	private static void maybeSaveConfig() {
		if (spireConfig == null) {
			return;
		}

		try {
			Method save = spireConfig.getDeclaredMethod("save");
			save.invoke(config);
		} catch (Exception e) {
			logger.info("could not save config");
			logger.error(e.toString());
		}
	}

	public static String maybeGetString(String key) {
		if (spireConfig == null) {
			return null;
		}

		try {
			Method getString = spireConfig.getDeclaredMethod("getString", String.class);
			return (String) getString.invoke(config, key);
		} catch (Exception e) {
			logger.info("could not get string: " + key);
			logger.error(e.toString());
			return null;
		}
	}

	public static void maybeSetString(String key, String value) {
		if (spireConfig == null) {
			return;
		}

		try {
			Method setString = spireConfig.getDeclaredMethod("setString", String.class, String.class);
			setString.invoke(config, key, value);
			maybeSaveConfig();
		} catch (Exception e) {
			logger.info("could not set string: " + key + " to value: " + value);
			logger.error(e.toString());
		}
	}

	public static Boolean maybeGetBoolean(String key) {
		if (spireConfig == null) {
			return null;
		}

		try {
			Method getBoolean = spireConfig.getDeclaredMethod("getBool", String.class);
			return (Boolean) getBoolean.invoke(config, key);
		} catch (Exception e) {
			logger.info("could not get boolean: " + key);
			logger.error(e.toString());
			return null;
		}
	}

	public static void maybeSetBoolean(String key, Boolean value) {
		if (spireConfig == null) {
			return;
		}

		try {
			Method setBoolean = spireConfig.getDeclaredMethod("setBool", String.class, boolean.class);
			setBoolean.invoke(config, key, value);
			maybeSaveConfig();
		} catch (Exception e) {
			logger.info("could not set boolean: " + key + " to value: " + value);
			logger.error(e.toString());
		}
	}

	private static void setProperties() {
		// if config can't be loaded leave things at defaults
		if (config == null) {
			return;
		}

		String consoleKey = maybeGetString("console-key");
		if (consoleKey != null) {
			DevConsole.toggleKey = Keys.valueOf(consoleKey);
		}
		Boolean consoleEnabled = maybeGetBoolean("console-enabled");
		if (consoleEnabled != null) {
			DevConsole.enabled = consoleEnabled;
		}

		// This is done because the default value for getBoolean is false but I want
		// true
		String autoCompleteEnabled = maybeGetString("autocomplete-enabled");
		if (autoCompleteEnabled != null) {
			AutoComplete.enabled = !autoCompleteEnabled.equalsIgnoreCase("false");
		}
	}

	public static boolean isBaseGameCharacter(AbstractPlayer.PlayerClass chosenClass) {
		return (chosenClass == AbstractPlayer.PlayerClass.IRONCLAD
				|| chosenClass == AbstractPlayer.PlayerClass.THE_SILENT
				|| chosenClass == AbstractPlayer.PlayerClass.DEFECT);
	}

	// initialize -
	public static void initialize() {
		System.out.println("libgdx version " + Version.VERSION);

		modBadges = new ArrayList<>();

		initializeGson();
		initializeTypeMaps();
		initializeSubscriptions();
		initializeCardLists();
		initializeCharacterMap();
		initializeColorMap();
		initializeRelicPool();
		initializeUnlocks();
		initializePotionMap();
		initializePotionList();
		initializePowerMap();
		initializeUnderscorePowerIDs();
		initializeEncounters();
		BaseModInit baseModInit = new BaseModInit();
		BaseMod.subscribeToPostInitialize(baseModInit);

		EditCharactersInit editCharactersInit = new EditCharactersInit();
		BaseMod.subscribeToPostInitialize(editCharactersInit);

		config = maybeGetConfig();
		setProperties();
		console = new DevConsole();
		textPanel = new ModTextPanel();
	}

	// setupAnimationGfx -
	private static void setupAnimationGfx() {
		animationCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		animationCamera.near = 1.0f;
		animationCamera.far = 300.0f;
		animationCamera.position.z = 200.0f;
		animationCamera.update();
		batch = new ModelBatch();
		animationEnvironment = new Environment();
		animationEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
		animationBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
	}

	// initializeGson -
	private static void initializeGson() {
		logger.info("initializeGson");
		GsonBuilder gsonBuilder = new GsonBuilder();
		gson = gsonBuilder.create();
	}

	// initializeTypeTokens -
	private static void initializeTypeMaps() {
		logger.info("initializeTypeMaps");

		typeMaps = new HashMap<>();
		typeMaps.put(AchievementStrings.class, "acheivements");
		typeMaps.put(CardStrings.class, "cards");
		typeMaps.put(CharacterStrings.class, "characters");
		typeMaps.put(CreditStrings.class, "credits");
		typeMaps.put(EventStrings.class, "events");
		typeMaps.put(KeywordStrings.class, "keywords");
		typeMaps.put(MonsterStrings.class, "monsters");
		typeMaps.put(PotionStrings.class, "potions");
		typeMaps.put(PowerStrings.class, "powers");
		typeMaps.put(RelicStrings.class, "relics");
		typeMaps.put(ScoreBonusStrings.class, "scoreBonuses");
		typeMaps.put(TutorialStrings.class, "tutorials");
		typeMaps.put(UIStrings.class, "ui");
		typeMaps.put(OrbStrings.class, "orb");

		typeTokens = new HashMap<>();
		typeTokens.put(AchievementStrings.class, new TypeToken<Map<String, AchievementStrings>>() {}.getType());
		typeTokens.put(CardStrings.class, new TypeToken<Map<String, CardStrings>>() {}.getType());
		typeTokens.put(CharacterStrings.class, new TypeToken<Map<String, CharacterStrings>>() {}.getType());
		typeTokens.put(CreditStrings.class, new TypeToken<Map<String, CreditStrings>>() {}.getType());
		typeTokens.put(EventStrings.class, new TypeToken<Map<String, EventStrings>>() {}.getType());
		typeTokens.put(KeywordStrings.class, new TypeToken<Map<String, KeywordStrings>>() {}.getType());
		typeTokens.put(MonsterStrings.class, new TypeToken<Map<String, MonsterStrings>>() {}.getType());
		typeTokens.put(PotionStrings.class, new TypeToken<Map<String, PotionStrings>>() {}.getType());
		typeTokens.put(PowerStrings.class, new TypeToken<Map<String, PowerStrings>>() {}.getType());
		typeTokens.put(RelicStrings.class, new TypeToken<Map<String, RelicStrings>>() {}.getType());
		typeTokens.put(ScoreBonusStrings.class, new TypeToken<Map<String, ScoreBonusStrings>>() {}.getType());
		typeTokens.put(TutorialStrings.class, new TypeToken<Map<String, TutorialStrings>>() {}.getType());
		typeTokens.put(UIStrings.class, new TypeToken<Map<String, UIStrings>>() {}.getType());
		typeTokens.put(OrbStrings.class, new TypeToken<Map<String, OrbStrings>>() {}.getType());
	}

	// initializeSubscriptions -
	private static void initializeSubscriptions() {
		toRemove = new ArrayList<>();
		startActSubscribers = new ArrayList<>();
		postCampfireSubscribers = new ArrayList<>();
		postDrawSubscribers = new ArrayList<>();
		postExhaustSubscribers = new ArrayList<>();
		onCardUseSubscribers = new ArrayList<>();
		postDungeonInitializeSubscribers = new ArrayList<>();
		postEnergyRechargeSubscribers = new ArrayList<>();
		postInitializeSubscribers = new ArrayList<>();
		preMonsterTurnSubscribers = new ArrayList<>();
		renderSubscribers = new ArrayList<>();
		preRenderSubscribers = new ArrayList<>();
		postRenderSubscribers = new ArrayList<>();
		modelRenderSubscribers = new ArrayList<>();
		preStartGameSubscribers = new ArrayList<>();
		startGameSubscribers = new ArrayList<>();
		preUpdateSubscribers = new ArrayList<>();
		postUpdateSubscribers = new ArrayList<>();
		postCreateStartingDeckSubscribers = new ArrayList<>();
		postCreateStartingRelicsSubscribers = new ArrayList<>();
		postCreateShopRelicSubscribers = new ArrayList<>();
		postCreateShopPotionSubscribers = new ArrayList<>();
		editCardsSubscribers = new ArrayList<>();
		editRelicsSubscribers = new ArrayList<>();
		editCharactersSubscribers = new ArrayList<>();
		editStringsSubscribers = new ArrayList<>();
		editKeywordsSubscribers = new ArrayList<>();
		postBattleSubscribers = new ArrayList<>();
		setUnlocksSubscribers = new ArrayList<>();
		postPotionUseSubscribers = new ArrayList<>();
		prePotionUseSubscribers = new ArrayList<>();
		potionGetSubscribers = new ArrayList<>();
		relicGetSubscribers = new ArrayList<>();
		postPowerApplySubscribers = new ArrayList<>();
		onPowersModifiedSubscribers = new ArrayList<>();
		postDeathSubscribers = new ArrayList<>();
		startBattleSubscribers = new ArrayList<>();

	}

	// initializeCardLists -
	private static void initializeCardLists() {
		redToAdd = new ArrayList<>();
		redToRemove = new ArrayList<>();
		greenToAdd = new ArrayList<>();
		greenToRemove = new ArrayList<>();
		colorlessToAdd = new ArrayList<>();
		colorlessToRemove = new ArrayList<>();
		curseToAdd = new ArrayList<>();
		curseToRemove = new ArrayList<>();
		customToAdd = new ArrayList<>();
		customToRemove = new ArrayList<>();
		customToRemoveColors = new ArrayList<>();
	}

	// initializeCharacterMap -
	private static void initializeCharacterMap() {
		playerClassMap = new HashMap<>();
		playerTitleStringMap = new HashMap<>();
		playerClassStringMap = new HashMap<>();
		playerColorMap = new HashMap<>();
		playerSelectTextMap = new HashMap<>();
		playerSelectButtonMap = new HashMap<>();
		playerPortraitMap = new HashMap<>();
		playerStatsMap = new HashMap<>();
	}

	// initializeColorMap -
	private static void initializeColorMap() {
		colorBgColorMap = new HashMap<>();
		colorBackColorMap = new HashMap<>();
		colorFrameColorMap = new HashMap<>();
		colorFrameOutlineColorMap = new HashMap<>();
		colorDescBoxColorMap = new HashMap<>();
		colorTrailVfxMap = new HashMap<>();
		colorGlowColorMap = new HashMap<>();
		colorCardCountMap = new HashMap<>();
		colorCardSeenCountMap = new HashMap<>();
		colorAttackBgMap = new HashMap<>();
		colorSkillBgMap = new HashMap<>();
		colorPowerBgMap = new HashMap<>();
		colorEnergyOrbMap = new HashMap<>();
		colorAttackBgPortraitMap = new HashMap<>();
		colorSkillBgPortraitMap = new HashMap<>();
		colorPowerBgPortraitMap = new HashMap<>();
		colorEnergyOrbPortraitMap = new HashMap<>();
		colorAttackBgTextureMap = new HashMap<>();
		colorSkillBgTextureMap = new HashMap<>();
		colorPowerBgTextureMap = new HashMap<>();
		colorEnergyOrbTextureMap = new HashMap<>();
		colorAttackBgPortraitTextureMap = new HashMap<>();
		colorSkillBgPortraitTextureMap = new HashMap<>();
		colorPowerBgPortraitTextureMap = new HashMap<>();
		colorEnergyOrbPortraitTextureMap = new HashMap<>();
	}

	private static void initializeRelicPool() {
		customRelicPools = new HashMap<>();
		customRelicLists = new HashMap<>();
	}

	// initializeUnlocks
	private static void initializeUnlocks() {
		unlockBundles = new HashMap<>();
	}

	@SuppressWarnings("rawtypes")
	private static void initializePotionMap() {
		potionClassMap = new HashMap<>();
		potionHybridColorMap = new HashMap<>();
		potionLiquidColorMap = new HashMap<>();
		potionSpotsColorMap = new HashMap<>();
	}

	private static void initializePotionList() {
		potionsToRemove = new ArrayList<>();
	}

	// Finds potions that have IDs with spaces in them and maps those IDs with
	// underscores instead of spaces to the original id
	public static void initializeUnderscorePotionIDs() {
		logger.info("initializeUnderscorePotionIDs");
		underScorePotionIDs = new HashMap<>();

		// Not actually unchecked
		@SuppressWarnings("unchecked")
		// This has to be LocalizedStrings and not PotionHelper.potions, because
		// PotionHelper.potions is empty on the games startup
		Map<String, PotionStrings> potions = (Map<String, PotionStrings>) (ReflectionHacks
				.getPrivateStatic(LocalizedStrings.class, "potions"));
		if (potions != null) {
			for (String key : potions.keySet()) {
				if (key.contains(" ")) {
					underScorePotionIDs.put(key.replace(' ', '_'), key);
				}
			}
		}
	}

	// Finds potions that have IDs with spaces in them and maps those IDs with
	// underscores instead of spaces to the original id
	public static void initializeUnderscoreEventIDs() {
		logger.info("initializeUnderscoreEventIDs");
		underScoreEventIDs = new HashMap<>();

		// Not actually unchecked
		@SuppressWarnings("unchecked")
		Map<String, EventStrings> events = (Map<String, EventStrings>) (ReflectionHacks
				.getPrivateStatic(LocalizedStrings.class, "events"));
		if (events != null) {
			for (String key : events.keySet()) {
				if (key.contains(" ")) {
					underScoreEventIDs.put(key.replace(' ', '_'), key);
				}
			}
		}
	}

	private static void initializeEncounters() {
		// maybe change this to use LocalizedStrings instead (like
		// initializeUnderScorePotionIDs)
		logger.info("initializeEncounters");
		encounterList = new ArrayList<>();
		// Construct Encounters
		try {
			Field[] fields = MonsterHelper.class.getDeclaredFields();
			for (Field f : fields) {
				// All encounters are public static final Strings
				// Currently, there are no false positives because the encounters are the only
				// public static final Strings in the MonsterHelper class. If this changes, the
				// other constants have to be manually blacklisted here
				if (f.getType() == String.class) {
					int mods = f.getModifiers();
					if (Modifier.isStatic(mods) && Modifier.isPublic(mods) && Modifier.isFinal(mods)) {
						encounterList.add((String) f.get(null));
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// Finds encounters that have IDs with spaces in them and maps those IDs with
		// underscores instead of spaces to the original id
		underScoreEncounterIDs = new HashMap<>();
		for (String id : encounterList) {
			if (id.contains(" ")) {
				underScoreEncounterIDs.put(id.replace(' ', '_'), id);
			}
		}
	}

	// Finds cards that have IDs with spaces in them and maps those IDs with
	// underscores instead of spaces to the original id
	public static void initializeUnderscoreCardIDs() {
		logger.info("initializeUnderscoreCardIDs");
		underScoreCardIDs = new HashMap<>();
		for (String key : CardLibrary.cards.keySet()) {
			if (key.contains(" ")) {
				underScoreCardIDs.put(key.replace(' ', '_'), key);
			}
		}
	}

	// Finds relics that have IDs with spaces in them and maps those IDs with
	// underscores instead of spaces to the original id
	public static void initializeUnderscoreRelicIDs() {
		logger.info("initializeUnderscoreRelicIDs");
		underScoreRelicIDs = new HashMap<>();
		for (String id : listAllRelicIDs()) {
			if (id.contains(" ")) {
				underScoreRelicIDs.put(id.replace(' ', '_'), id);
			}
		}
	}

	private static void initializePowerMap() {
		logger.info("initializePowerMap");
		powerMap = new HashMap<>();

		ClassFinder finder = new ClassFinder();
		URL url = AbstractPower.class.getProtectionDomain().getCodeSource().getLocation();
		try {
			finder.add(new File(url.toURI()));

			ClassFilter filter = new AndClassFilter(new NotClassFilter(new InterfaceOnlyClassFilter()),
					new NotClassFilter(new AbstractClassFilter()),
					new RegexClassFilter("com\\.megacrit\\.cardcrawl\\.powers\\..+"));
			Collection<ClassInfo> foundClasses = new ArrayList<>();
			finder.findClasses(foundClasses, filter);

			for (ClassInfo classInfo : foundClasses) {
				if (classInfo.getClassName().contains("$")) {
					continue;
				}
				try {
					for (FieldInfo fieldInfo : classInfo.getFields()) {
						if (fieldInfo.getName().equals("POWER_ID") && fieldInfo.getValue() instanceof String) {
							powerMap.put((String) fieldInfo.getValue(),
									BaseMod.class.getClassLoader().loadClass(classInfo.getClassName()));
							break;
						}
					}
				} catch (ClassNotFoundException e) {
					System.out.println("ERROR: Failed to load power class: " + classInfo.getClassName());
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	// Finds powers that have IDs with spaces in them and maps those IDs with
	// underscores instead of spaces to the original id
	public static void initializeUnderscorePowerIDs() {
		logger.info("initializeUnderscorePowerIDs");
		underScorePowerIDs = new HashMap<>();
		for (String key : powerMap.keySet()) {
			if (key.contains(" ")) {
				underScorePowerIDs.put(key.replace(' ', '_'), key);
			}
		}
	}

	//
	// Mod badges
	//
	public static void registerModBadge(Texture t, String name, String author, String desc, ModPanel settingsPanel) {
		logger.info("registerModBadge : " + name);
		int modBadgeCount = modBadges.size();
		int col = (modBadgeCount % BADGES_PER_ROW);
		int row = (modBadgeCount / BADGES_PER_ROW);
		float x = (BADGES_X * Settings.scale) + (col * BADGE_W * Settings.scale);
		float y = (BADGES_Y * Settings.scale) - (row * BADGE_H * Settings.scale);

		ModBadge badge = new ModBadge(t, x, y, name, author, desc, settingsPanel);
		modBadges.add(badge);
	}

	//
	// UI
	//
	public static void openTextPanel(ModPanel panel, String prompt, String startingValue, String defaultValue,
			String explanationText, Consumer<ModTextPanel> cancel, Consumer<ModTextPanel> confirm) {
		CardCrawlGame.mainMenuScreen.lighten();
		CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.RENAME;
		CardCrawlGame.cancelButton.hideInstantly();
		textPanel.show(panel, startingValue, defaultValue, explanationText, cancel, confirm);
	}

	public static boolean baseGameSaveExists() {
		return (SaveAndContinue.ironcladSaveExists) || (SaveAndContinue.silentSaveExists)
				|| (SaveAndContinue.crowbotSaveExists) || (SaveAndContinue.defectSaveExists);
	}

	public static boolean moddedSaveExists() {
		System.out.println("checking if save exists");
		for (String playerClass : playerClassMap.keySet()) {
			String filepath = save_path + playerClass + ".autosave";
			System.out.println("looking for " + filepath);
			boolean fileExists = Gdx.files.local(filepath).exists();
			// delete corrupted saves
			if ((fileExists)
					&& (SaveAndContinue.loadSaveFile(AbstractPlayer.PlayerClass.valueOf(playerClass)) == null)) {
				SaveAndContinue.deleteSave(AbstractPlayer.PlayerClass.valueOf(playerClass));
			}
			if (fileExists) {
				return true;
			}
		}
		return false;
	}

	public static AbstractPlayer.PlayerClass getSaveClass() {
		for (String playerClass : playerClassMap.keySet()) {
			String filepath = save_path + playerClass + ".autosave";
			if (Gdx.files.local(filepath).exists()) {
				return AbstractPlayer.PlayerClass.valueOf(playerClass);
			}
		}
		return null;
	}

	//
	// Localization
	//

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void loadJsonStrings(Type stringType, String jsonString) {
		logger.info("loadJsonStrings: " + stringType.getTypeName());

		String typeMap = typeMaps.get(stringType);
		Type typeToken = typeTokens.get(stringType);

		String modName = BaseMod.findCallingModName();

		Map localizationStrings = (Map) ReflectionHacks.getPrivateStatic(LocalizedStrings.class, typeMap);
		Map map = new HashMap(gson.fromJson(jsonString, typeToken));
		if (stringType.equals(CardStrings.class) || stringType.equals(RelicStrings.class)) {
			Map map2 = new HashMap();
			for (Object k : map.keySet()) {
				map2.put(modName == null ? k : modName + ":" + k, map.get(k));
			}
			localizationStrings.putAll(map2);
		} else {
			localizationStrings.putAll(map);
		}
		ReflectionHacks.setPrivateStaticFinal(LocalizedStrings.class, typeMap, localizationStrings);
	}

	// loadCustomRelicStrings - loads custom RelicStrings from provided JSON
	// should be done inside the callback of an implementation of
	// EditStringsSubscriber
	public static void loadCustomStrings(@SuppressWarnings("rawtypes") Class stringType, String jsonString) {
		loadJsonStrings(stringType, jsonString);
	}

	//
	// Cards
	//

	// red add -
	public static ArrayList<AbstractCard> getRedCardsToAdd() {
		return redToAdd;
	}

	// red remove -
	public static ArrayList<String> getRedCardsToRemove() {
		return redToRemove;
	}

	// green add -
	public static ArrayList<AbstractCard> getGreenCardsToAdd() {
		return greenToAdd;
	}

	// green remove -
	public static ArrayList<String> getGreenCardsToRemove() {
		return greenToRemove;
	}

	// colorless add -
	public static ArrayList<AbstractCard> getColorlessCardsToAdd() {
		return colorlessToAdd;
	}

	// colorless remove -
	public static ArrayList<String> getColorlessCardsToRemove() {
		return colorlessToRemove;
	}

	// curse add -
	public static ArrayList<AbstractCard> getCurseCardsToAdd() {
		return curseToAdd;
	}

	// curse remove -
	public static ArrayList<String> getCurseCardsToRemove() {
		return curseToRemove;
	}

	// custom add -
	public static ArrayList<AbstractCard> getCustomCardsToAdd() {
		return customToAdd;
	}

	// custom remove -
	public static ArrayList<String> getCustomCardsToRemove() {
		return customToRemove;
	}

	// custom remove colors -
	public static ArrayList<String> getCustomCardsToRemoveColors() {
		return customToRemoveColors;
	}

	// add card
	public static void addCard(AbstractCard card) {
		switch (card.color) {
		case RED:
			redToAdd.add(card);
			break;
		case GREEN:
			greenToAdd.add(card);
			break;
		case COLORLESS:
			colorlessToAdd.add(card);
			break;
		case CURSE:
			curseToAdd.add(card);
			break;
		default:
			customToAdd.add(card);
			break;
		}
	}

	// remove card
	public static void removeCard(String card, String color) {
		switch (color) {
		case "RED":
			redToRemove.add(card);
			break;
		case "GREEN":
			greenToRemove.add(card);
			break;
		case "COLORLESS":
			colorlessToRemove.add(card);
			break;
		case "CURSE":
			curseToRemove.add(card);
			break;
		default:
			customToRemove.add(card);
			customToRemoveColors.add(color);
			break;
		}
	}

	public static void addDynamicVariable(DynamicVariable dv) {
		cardDynamicVariableMap.put(dv.key(), dv);
	}

	/**
	 * Modifies the damage done by a card by seeing if the card is a CustomCard and
	 * if so, going ahead and calling the damage modification method default
	 * implementation leaves the damage the same
	 * 
	 * @param player
	 *            the player casting this card
	 * @param mo
	 *            the monster this card is targetting (may be null for multiTarget)
	 * @param c
	 *            the card being cast
	 * @param tmp
	 *            the current damage amount
	 * @return the modified damage amount
	 */
	public static float calculateCardDamage(AbstractPlayer player, AbstractMonster mo, AbstractCard c, float tmp) {
		if (c instanceof CustomCard) {
			float newVal = ((CustomCard) c).calculateModifiedCardDamage(player, mo, tmp);
			if ((int) newVal != c.baseDamage) {
				c.isDamageModified = true;
			}
			return newVal;
		} else {
			return tmp;
		}
	}

	/*
	 * same as above but without the monster
	 */
	public static float calculateCardDamage(AbstractPlayer player, AbstractCard c, float tmp) {
		return calculateCardDamage(player, null, c, tmp);
	}

	//
	// Relics
	//
	// these adders and removers prevent modders from having to deal with
	// RelicLibrary's need to keep track
	// of counts and multiple lists by abstracting it away to simple addRelic
	// and removeRelic calls
	//

	// add relic -
	public static void addRelic(AbstractRelic relic, RelicType type) {
		switch (type) {
		case SHARED:
			RelicLibrary.add(relic);
			break;
		case RED:
			RelicLibrary.addRed(relic);
			break;
		case GREEN:
			RelicLibrary.addGreen(relic);
			break;
		default:
			logger.info("tried to add relic of unsupported type: " + relic + " " + type);
		}
	}

	// remove relic -
	@SuppressWarnings("unchecked")
	public static void removeRelic(AbstractRelic relic, RelicType type) {
		// note that this has to use reflection hacks to change the private
		// variables in RelicLibrary to successfully remove the relics
		//
		// this could be accomplished without reflection hacks by creating a
		// @SpirePatch to enable relic removal functionality
		//
		// as of right now I'm not sure which method is preferable
		// removeCard is using the @SpirePatch method
		switch (type) {
		case SHARED:
			HashMap<String, AbstractRelic> sharedRelics = (HashMap<String, AbstractRelic>) ReflectionHacks
					.getPrivateStatic(RelicLibrary.class, "sharedRelics");
			if (sharedRelics.containsKey(relic.relicId)) {
				sharedRelics.remove(relic.relicId);
				RelicLibrary.totalRelicCount--;
				removeRelicFromTierList(relic);
			}
			break;
		case RED:
			HashMap<String, AbstractRelic> redRelics = (HashMap<String, AbstractRelic>) ReflectionHacks
					.getPrivateStatic(RelicLibrary.class, "redRelics");
			if (redRelics.containsKey(relic.relicId)) {
				redRelics.remove(relic.relicId);
				RelicLibrary.totalRelicCount--;
				removeRelicFromTierList(relic);
			}
			break;
		case GREEN:
			HashMap<String, AbstractRelic> greenRelics = (HashMap<String, AbstractRelic>) ReflectionHacks
					.getPrivateStatic(RelicLibrary.class, "greenRelics");
			if (greenRelics.containsKey(relic.relicId)) {
				greenRelics.remove(relic.relicId);
				RelicLibrary.totalRelicCount--;
				removeRelicFromTierList(relic);
			}
			break;
		default:
			logger.info("tried to remove relic of unsupported type: " + relic + " " + type);
		}
	}

	// addRelicToCustomPool -
	public static void addRelicToCustomPool(AbstractRelic relic, String color) {
		if (customRelicPools.containsKey(color)) {
			if (UnlockTracker.isRelicSeen(relic.relicId)) {
				RelicLibrary.seenRelics++;
			}
			relic.isSeen = UnlockTracker.isRelicSeen(relic.relicId);
			customRelicPools.get(color).put(relic.relicId, relic);
			RelicLibrary.addToTierList(relic);
			customRelicLists.get(color).add(relic);
		} else {
			logger.error("could not add relic to non existent custom pool: " + color);
		}
	}

	// getRelicsInCustomPool -
	public static HashMap<String, AbstractRelic> getRelicsInCustomPool(String color) {
		return customRelicPools.get(color);
	}

	// getAllCustomRelics -
	public static HashMap<String, HashMap<String, AbstractRelic>> getAllCustomRelics() {
		return customRelicPools;
	}

	// getCustomRelic -
	public static AbstractRelic getCustomRelic(String key) {
		for (Map.Entry<String, HashMap<String, AbstractRelic>> map : BaseMod.getAllCustomRelics().entrySet()) {
			if (map.getValue().containsKey(key)) {
				return map.getValue().get(key);
			}
		}
		return new Circlet();
	}

	private static void removeRelicFromTierList(AbstractRelic relic) {
		switch (relic.tier) {
		case STARTER:
			RelicLibrary.starterList.remove(relic);
			break;
		case COMMON:
			RelicLibrary.commonList.remove(relic);
			break;
		case UNCOMMON:
			RelicLibrary.uncommonList.remove(relic);
			break;
		case RARE:
			RelicLibrary.rareList.remove(relic);
			break;
		case SHOP:
			RelicLibrary.shopList.remove(relic);
			break;
		case SPECIAL:
			RelicLibrary.specialList.remove(relic);
			break;
		case BOSS:
			RelicLibrary.bossList.remove(relic);
			break;
		case DEPRECATED:
			logger.info(relic.relicId + " is deprecated.");
			break;
		default:
			logger.info(relic.relicId + "is undefined tier.");
		}
	}

	// force remove relic -
	public static void removeRelic(AbstractRelic relic) {
		removeRelic(relic, RelicType.SHARED);
		removeRelic(relic, RelicType.RED);
		removeRelic(relic, RelicType.GREEN);
	}

	// lists the IDs of all Relics from all pools. The casts are actually not
	// unchecked
	@SuppressWarnings("unchecked")
	public static ArrayList<String> listAllRelicIDs() {
		ArrayList<String> relicIDs = new ArrayList<>();

		HashMap<String, AbstractRelic> sharedRelics = (HashMap<String, AbstractRelic>) ReflectionHacks
				.getPrivateStatic(RelicLibrary.class, "sharedRelics");
		if (sharedRelics != null) {
			relicIDs.addAll(sharedRelics.keySet());
		}
		HashMap<String, AbstractRelic> redRelics = (HashMap<String, AbstractRelic>) ReflectionHacks
				.getPrivateStatic(RelicLibrary.class, "redRelics");
		if (redRelics != null) {
			relicIDs.addAll(redRelics.keySet());
		}
		HashMap<String, AbstractRelic> greenRelics = (HashMap<String, AbstractRelic>) ReflectionHacks
				.getPrivateStatic(RelicLibrary.class, "greenRelics");
		if (greenRelics != null) {
			relicIDs.addAll(greenRelics.keySet());
		}
		HashMap<String, AbstractRelic> blueRelics = (HashMap<String, AbstractRelic>) ReflectionHacks
				.getPrivateStatic(RelicLibrary.class, "blueRelics");
		if (blueRelics != null) {
			relicIDs.addAll(blueRelics.keySet());
		}
		if (getAllCustomRelics() != null) {
			for (Entry<String, HashMap<String, AbstractRelic>> e : getAllCustomRelics().entrySet()) {
				if (e != null && e.getValue() != null) {
					relicIDs.addAll(e.getValue().keySet());
				}
			}
		}
		return relicIDs;
	}

	//
	// Keywords
	//

	public static void addKeyword(String[] names, String description) {
		String parent = names[0];

		for (String name : names) {
			GameDictionary.keywords.put(name, description);
			GameDictionary.parentWord.put(name, parent);
		}
	}

	//
	// Unlocks
	//

	// add new unlock bundle
	public static void addUnlockBundle(CustomUnlockBundle bundle, AbstractPlayer.PlayerClass c, int unlockLevel) {
		if (!unlockBundles.containsKey(c)) {
			HashMap<Integer, CustomUnlockBundle> newBundles = new HashMap<>();
			newBundles.put(unlockLevel, bundle);
			unlockBundles.put(c, newBundles);
		} else {
			HashMap<Integer, CustomUnlockBundle> bundles = unlockBundles.get(c);
			bundles.put(unlockLevel, bundle);
		}
	}

	// remove old unlock bundle
	public static void removeUnlockBundle(AbstractPlayer.PlayerClass c, int unlockLevel) {
		if (unlockBundles.containsKey(c)) {
			unlockBundles.get(c).remove(unlockLevel);
		}
	}

	// get unlock bundle for class and level
	public static CustomUnlockBundle getUnlockBundleFor(AbstractPlayer.PlayerClass c, int unlockLevel) {
		HashMap<Integer, CustomUnlockBundle> levelMap = unlockBundles.get(c);
		if (levelMap == null) {
			return null;
		}
		return levelMap.get(unlockLevel);
	}

	//
	// Characters
	//

	// add character - the String characterID *must* be the exact same as what
	// you put in the PlayerClass enum
	public static void addCharacter(@SuppressWarnings("rawtypes") Class characterClass, String titleString,
			String classString, String color, String selectText, String selectButton, String portrait,
			String characterID) {
		playerClassMap.put(characterID, characterClass);
		playerTitleStringMap.put(characterID, titleString);
		playerClassStringMap.put(characterID, classString);
		playerColorMap.put(characterID, color);
		playerSelectTextMap.put(characterID, selectText);
		playerSelectButtonMap.put(characterID, selectButton);
		playerPortraitMap.put(characterID, portrait);
	}

	// I have no idea if this implementation comes even remotely close to
	// working
	public static void removeCharacter(String characterID) {
		playerClassMap.remove(characterID);
	}

	// convert a playerClass String (fake ENUM) into the actual player class
	// used by CardCrawlGame when creating the player for the game
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static AbstractPlayer createCharacter(String playerClass, String playerName) {
		Class playerClassAsClass = playerClassMap.get(playerClass);
		Constructor ctor;
		try {
			ctor = playerClassAsClass.getConstructor(String.class, AbstractPlayer.PlayerClass.class);
		} catch (NoSuchMethodException | SecurityException e) {
			// if we fail to get the constructor using java reflections just
			// start the run as the Ironclad
			logger.error("could not get constructor for " + playerClassAsClass.getName());
			logger.info("running as the Ironclad instead");
			CardCrawlGame.chosenCharacter = AbstractPlayer.PlayerClass.IRONCLAD;
			return new Ironclad(playerName, AbstractPlayer.PlayerClass.IRONCLAD);
		}
		AbstractPlayer thePlayer;
		try {
			thePlayer = (AbstractPlayer) ctor
					.newInstance(new Object[] { playerName, PlayerClass.valueOf(playerClass) });
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// if we fail to instantiate using java reflections just start the
			// run as the Ironclad
			logger.error("could not instantiate " + playerClassAsClass.getName() + " with the constructor "
					+ ctor.getName());
			logger.info("running as the Ironclad instead");
			logger.error("error was: " + e.getMessage());
			e.printStackTrace();
			CardCrawlGame.chosenCharacter = AbstractPlayer.PlayerClass.IRONCLAD;
			return new Ironclad(playerName, AbstractPlayer.PlayerClass.IRONCLAD);
		}
		return thePlayer;
	}

	// convert a playerClass String (fake ENUM) into the actual title string for
	// that class
	// used by AbstractPlayer when getting the title string for the player
	public static String getTitle(String playerClass) {
		return playerTitleStringMap.get(playerClass);
	}

	// convert a playerClass String (fake ENUM) into the actual starting deck
	// for that class
	@SuppressWarnings("unchecked")
	public static ArrayList<String> getStartingDeck(String playerClass) {
		@SuppressWarnings("rawtypes")
		Class playerClassAsClass = playerClassMap.get(playerClass);
		Method getStartingDeck;
		try {
			getStartingDeck = playerClassAsClass.getMethod("getStartingDeck");
		} catch (NoSuchMethodException | SecurityException e1) {
			// if we fail to get the getStartingDeck method using java
			// reflections just start with the Ironclad deck
			logger.error("could not get starting deck method for " + playerClassAsClass.getName());
			return Ironclad.getStartingDeck();
		}
		Object startingDeckObj;
		try {
			startingDeckObj = getStartingDeck.invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// if we fail to get the starting deck using java reflections just
			// start with the Ironclad deck
			logger.error("could not get starting deck for " + playerClassAsClass.getName());
			return Ironclad.getStartingDeck();
		}
		return (ArrayList<String>) startingDeckObj;
	}

	// convert a playerClass String (fake ENUM) into the actual starting relics
	// for that class
	@SuppressWarnings("unchecked")
	public static ArrayList<String> getStartingRelics(String playerClass) {
		@SuppressWarnings("rawtypes")
		Class playerClassAsClass = playerClassMap.get(playerClass);
		Method getStartingRelics;
		try {
			getStartingRelics = playerClassAsClass.getMethod("getStartingRelics");
		} catch (NoSuchMethodException | SecurityException e1) {
			// if we fail to get the getStartingRelics method using java
			// reflections just start with the Ironclad relics
			logger.error("could not get starting relic method for " + playerClassAsClass.getName());
			return Ironclad.getStartingRelics();
		}
		Object startingRelicsObj;
		try {
			startingRelicsObj = getStartingRelics.invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// if we fail to get the starting relics using java reflections just
			// start with the Ironclad relics
			logger.error("could not get starting deck for " + playerClassAsClass.getName());
			return Ironclad.getStartingRelics();
		}
		return (ArrayList<String>) startingRelicsObj;
	}

	public static TextureAtlas.AtlasRegion getCardSmallEnergy() {
		if (AbstractDungeon.player == null) {
			return AbstractCard.orb_red;
		}
		switch (AbstractDungeon.player.chosenClass) {
		case IRONCLAD:
			return AbstractCard.orb_red;
		case THE_SILENT:
			return AbstractCard.orb_green;
		case DEFECT:
			return AbstractCard.orb_blue;
		default:
			// TODO: mod orbs
			// TODO: Not requiring TextureAtlas.AtlasRegion
			return AbstractCard.orb_red;
		}
	}

	public static TextureAtlas.AtlasRegion getCardSmallEnergy(AbstractCard card) {
		switch (card.color) {
		case RED:
			return AbstractCard.orb_red;
		case GREEN:
			return AbstractCard.orb_green;
		case BLUE:
			return AbstractCard.orb_blue;
		default:
			return getCardSmallEnergy();
		}
	}

	// convert a playerClass String (fake ENUM) into the actual class ID for
	// that class
	public static String getClass(String playerClass) {
		return playerClassStringMap.get(playerClass);
	}

	// convert a playerClass String (fake ENUM) into the actual color String
	// (fake ENUM) for that class
	public static String getColor(String playerClass) {
		return playerColorMap.get(playerClass);
	}

	// convert a playerClass String (fake ENUM) into the actual player class
	@SuppressWarnings("rawtypes")
	public static Class getPlayerClass(String playerClass) {
		return playerClassMap.get(playerClass);
	}

	// convert a playerClass String (fake ENUM) into the player select button
	public static String getPlayerButton(String playerClass) {
		return playerSelectButtonMap.get(playerClass);
	}

	// convert a playerClass String (fake ENUM) into the player portrait
	public static String getPlayerPortrait(String playerClass) {
		return playerPortraitMap.get(playerClass);
	}

	// generate character options for CharacterSelectScreen based on added
	// players
	public static ArrayList<CharacterOption> generateCharacterOptions() {
		ArrayList<CharacterOption> options = new ArrayList<>();
		for (String character : playerClassMap.keySet()) {
			// the game by default prepends "images/ui/charSelect" to the image
			// load request
			// so we override that with "../../.."
			CharacterOption option = new CharacterOption(playerSelectTextMap.get(character),
					AbstractPlayer.PlayerClass.valueOf(character),
					// note that these will fail so we patch this in
					// basemode.patches.com.megacrit.cardcrawl.screens.charSelect.CharacterOption.CtorSwitch
					playerSelectButtonMap.get(character), playerPortraitMap.get(character));
			options.add(option);
		}
		return options;
	}

	// generate character options for CustomModeScreen based on added players
	public static ArrayList<CustomModeCharacterButton> generateCustomCharacterOptions() {
		ArrayList<CustomModeCharacterButton> options = new ArrayList<>();
		for (String character : playerClassMap.keySet()) {
			options.add(new CustomModeCharacterButton(AbstractPlayer.PlayerClass.valueOf(character), false));
		}
		return options;
	}

	// generate stats for StatsScreen based on added players
	public static ArrayList<CharStat> generateCharacterStats() {
		ArrayList<CharStat> stats = new ArrayList<>();
		for (String character : playerClassMap.keySet()) {
			CharStat stat = playerStatsMap.get(character);
			if (stat == null) {
				stat = new CharStat(AbstractPlayer.PlayerClass.valueOf(character));
				playerStatsMap.put(character, stat);
			}
			stats.add(stat);
		}
		return stats;
	}

	//
	// Colors
	//

	// add a color -
	public static void addColor(String color, com.badlogic.gdx.graphics.Color bgColor,
			com.badlogic.gdx.graphics.Color backColor, com.badlogic.gdx.graphics.Color frameColor,
			com.badlogic.gdx.graphics.Color frameOutlineColor, com.badlogic.gdx.graphics.Color descBoxColor,
			com.badlogic.gdx.graphics.Color trailVfxColor, com.badlogic.gdx.graphics.Color glowColor, String attackBg,
			String skillBg, String powerBg, String energyOrb, String attackBgPortrait, String skillBgPortrait,
			String powerBgPortrait, String energyOrbPortrait) {
		colorBgColorMap.put(color, bgColor);
		colorBackColorMap.put(color, backColor);
		colorFrameColorMap.put(color, frameColor);
		colorFrameOutlineColorMap.put(color, frameOutlineColor);
		colorDescBoxColorMap.put(color, descBoxColor);
		colorTrailVfxMap.put(color, trailVfxColor);
		colorGlowColorMap.put(color, glowColor);
		colorCardCountMap.put(color, 0);
		colorCardSeenCountMap.put(color, 0);
		colorAttackBgMap.put(color, attackBg);
		colorSkillBgMap.put(color, skillBg);
		colorPowerBgMap.put(color, powerBg);
		colorEnergyOrbMap.put(color, energyOrb);
		colorAttackBgPortraitMap.put(color, attackBgPortrait);
		colorSkillBgPortraitMap.put(color, skillBgPortrait);
		colorPowerBgPortraitMap.put(color, powerBgPortrait);
		colorEnergyOrbPortraitMap.put(color, energyOrbPortrait);

		customRelicPools.put(color, new HashMap<>());
		customRelicLists.put(color, new ArrayList<>());
	}

	// remove a custom color -
	// removing existing colors not currently supported
	public static void removeColor(String color) {
		colorBgColorMap.remove(color);
		colorBackColorMap.remove(color);
		colorFrameColorMap.remove(color);
		colorFrameOutlineColorMap.remove(color);
		colorDescBoxColorMap.remove(color);
		colorTrailVfxMap.remove(color);
		colorGlowColorMap.remove(color);
		colorCardCountMap.remove(color);
		colorCardSeenCountMap.remove(color);
		colorAttackBgMap.remove(color);
		colorSkillBgMap.remove(color);
		colorPowerBgMap.remove(color);
		colorEnergyOrbMap.remove(color);
		colorAttackBgPortraitMap.remove(color);
		colorSkillBgPortraitMap.remove(color);
		colorPowerBgPortraitMap.remove(color);
		colorEnergyOrbPortraitMap.remove(color);

		customRelicPools.remove(color);
		customRelicLists.remove(color, new ArrayList<>());
	}

	// convert a color String (fake ENUM) into a background color
	public static com.badlogic.gdx.graphics.Color getBgColor(String color) {
		return colorBgColorMap.get(color);
	}

	// convert a color String (fake ENUM) into a back color
	public static com.badlogic.gdx.graphics.Color getBackColor(String color) {
		return colorBackColorMap.get(color);
	}

	// convert a color String (fake ENUM) into a frame color
	public static com.badlogic.gdx.graphics.Color getFrameColor(String color) {
		return colorFrameColorMap.get(color);
	}

	// convert a color String (fake ENUM) into a frame outline color
	public static com.badlogic.gdx.graphics.Color getFrameOutlineColor(String color) {
		return colorFrameOutlineColorMap.get(color);
	}

	// convert a color String (fake ENUM) into a desc box color
	public static com.badlogic.gdx.graphics.Color getDescBoxColor(String color) {
		return colorDescBoxColorMap.get(color);
	}

	// convert a color String (fake ENUM) into a trail vfx color
	public static com.badlogic.gdx.graphics.Color getTrailVfxColor(String color) {
		return colorTrailVfxMap.get(color);
	}

	// increment the card count for a color String (fake ENUM)
	public static void incrementCardCount(String color) {
		Integer count = colorCardCountMap.get(color);
		System.out.println("incrementing card count for " + color + " to " + colorCardCountMap.get(color));
		if (count != null) {
			colorCardCountMap.put(color, count + 1);
		} else {
			colorCardCountMap.put(color, 0);
		}
	}

	// decrement the card count for a color String (fake ENUM)
	public static void decrementCardCount(String color) {
		Integer count = colorCardCountMap.get(color);
		if (count != null) {
			colorCardCountMap.put(color, count - 1);
			if (colorCardCountMap.get(color) < 0) {
				colorCardCountMap.remove(color);
			}
		}
	}

	// get card count for a color String (fake ENUM)
	public static Integer getCardCount(String color) {
		return colorCardCountMap.get(color);
	}

	// increment the seen card count for a color String (fake ENUM)
	public static void incrementSeenCardCount(String color) {
		Integer count = colorCardSeenCountMap.get(color);
		if (count != null) {
			colorCardSeenCountMap.put(color, count + 1);
		} else {
			colorCardSeenCountMap.put(color, 0);
		}
	}

	// get seen card count for a color String (fake ENUM)
	public static Integer getSeenCardCount(String color) {
		return colorCardSeenCountMap.get(color);
	}

	// convert a color String (fake ENUM) into a glow color
	public static com.badlogic.gdx.graphics.Color getGlowColor(String color) {
		return colorGlowColorMap.get(color);
	}

	// convert a color String (fake ENUM) into an attack background texture path
	public static String getAttackBg(String color) {
		return colorAttackBgMap.get(color);
	}

	// convert a color String (fake ENUM) into an skill background texture path
	public static String getSkillBg(String color) {
		return colorSkillBgMap.get(color);
	}

	// convert a color String (fake ENUM) into an power background texture path
	public static String getPowerBg(String color) {
		return colorPowerBgMap.get(color);
	}

	// convert a color String (fake ENUM) into an energy texture path
	public static String getEnergyOrb(String color) {
		return colorEnergyOrbMap.get(color);
	}

	// convert a color String (fake ENUM) into an attack background portrait
	// texture path
	public static String getAttackBgPortrait(String color) {
		return colorAttackBgPortraitMap.get(color);
	}

	// convert a color String (fake ENUM) into an skill background portrait
	// texture path
	public static String getSkillBgPortrait(String color) {
		return colorSkillBgPortraitMap.get(color);
	}

	// convert a color String (fake ENUM) into an power background portrait
	// texture path
	public static String getPowerBgPortrait(String color) {
		return colorPowerBgPortraitMap.get(color);
	}

	// convert a color String (fake ENUM) into an energy portrait texture path
	public static String getEnergyOrbPortrait(String color) {
		return colorEnergyOrbPortraitMap.get(color);
	}

	// convert a color String (fake ENUM) into an attack background texture
	public static com.badlogic.gdx.graphics.Texture getAttackBgTexture(String color) {
		return colorAttackBgTextureMap.get(color);
	}

	// convert a color String (fake ENUM) into an skill background texture
	public static com.badlogic.gdx.graphics.Texture getSkillBgTexture(String color) {
		return colorSkillBgTextureMap.get(color);
	}

	// convert a color String (fake ENUM) into an power background texture
	public static com.badlogic.gdx.graphics.Texture getPowerBgTexture(String color) {
		return colorPowerBgTextureMap.get(color);
	}

	// convert a color String (fake ENUM) into an energy texture
	public static com.badlogic.gdx.graphics.Texture getEnergyOrbTexture(String color) {
		return colorEnergyOrbTextureMap.get(color);
	}

	// convert a color String (fake ENUM) into an attack background texture
	public static com.badlogic.gdx.graphics.Texture getAttackBgPortraitTexture(String color) {
		return colorAttackBgPortraitTextureMap.get(color);
	}

	// convert a color String (fake ENUM) into an skill background texture
	public static com.badlogic.gdx.graphics.Texture getSkillBgPortraitTexture(String color) {
		return colorSkillBgPortraitTextureMap.get(color);
	}

	// convert a color String (fake ENUM) into an power background texture
	public static com.badlogic.gdx.graphics.Texture getPowerBgPortraitTexture(String color) {
		return colorPowerBgPortraitTextureMap.get(color);
	}

	// convert a color String (fake ENUM) into an energy texture
	public static com.badlogic.gdx.graphics.Texture getEnergyOrbPortraitTexture(String color) {
		return colorEnergyOrbPortraitTextureMap.get(color);
	}

	// save a attack background texture for a color String (fake ENUM)
	public static void saveAttackBgTexture(String color, com.badlogic.gdx.graphics.Texture tex) {
		colorAttackBgTextureMap.put(color, tex);
	}

	// save a skill background texture for a color String (fake ENUM)
	public static void saveSkillBgTexture(String color, com.badlogic.gdx.graphics.Texture tex) {
		colorSkillBgTextureMap.put(color, tex);
	}

	// save a power background texture for a color String (fake ENUM)
	public static void savePowerBgTexture(String color, com.badlogic.gdx.graphics.Texture tex) {
		colorPowerBgTextureMap.put(color, tex);
	}

	// save an energy orb texture for a color String (fake ENUM)
	public static void saveEnergyOrbTexture(String color, com.badlogic.gdx.graphics.Texture tex) {
		colorEnergyOrbTextureMap.put(color, tex);
	}

	// save a attack background texture for a color String (fake ENUM)
	public static void saveAttackBgPortraitTexture(String color, com.badlogic.gdx.graphics.Texture tex) {
		colorAttackBgPortraitTextureMap.put(color, tex);
	}

	// save a skill background texture for a color String (fake ENUM)
	public static void saveSkillBgPortraitTexture(String color, com.badlogic.gdx.graphics.Texture tex) {
		colorSkillBgPortraitTextureMap.put(color, tex);
	}

	// save a power background texture for a color String (fake ENUM)
	public static void savePowerBgPortraitTexture(String color, com.badlogic.gdx.graphics.Texture tex) {
		colorPowerBgPortraitTextureMap.put(color, tex);
	}

	// save an energy orb texture for a color String (fake ENUM)
	public static void saveEnergyOrbPortraitTexture(String color, com.badlogic.gdx.graphics.Texture tex) {
		colorEnergyOrbPortraitTextureMap.put(color, tex);
	}

	//
	// Potions
	//

	public static ArrayList<String> getPotionsToRemove() {
		return potionsToRemove;
	}

	public static void removePotion(String potionID) {
		potionsToRemove.add(potionID);
	}

	// add the Potion to the map (fake ENUM)
	@SuppressWarnings("rawtypes")
	public static void addPotion(Class potionClass, Color liquidColor, Color hybridColor, Color spotsColor,
			String potionID) {
		potionClassMap.put(potionID, potionClass);
		potionLiquidColorMap.put(potionID, liquidColor);
		potionHybridColorMap.put(potionID, hybridColor);
		potionSpotsColorMap.put(potionID, spotsColor);
	}

	// (fake ENUM) return Class corresponding to potionID
	@SuppressWarnings("rawtypes")
	public static Class getPotionClass(String potionID) {
		return potionClassMap.get(potionID);
	}

	// (fake ENUM) return Colors corresponding to potionID
	public static Color getPotionLiquidColor(String potionID) {
		return potionLiquidColorMap.get(potionID);
	}

	public static Color getPotionHybridColor(String potionID) {
		return potionHybridColorMap.get(potionID);
	}

	public static Color getPotionSpotsColor(String potionID) {
		return potionSpotsColorMap.get(potionID);
	}

	// get all entry in fake ENUM
	public static Set<String> getPotionIDs() {
		return potionClassMap.keySet();
	}

	//
	// Powers
	//

	public static void addPower(@SuppressWarnings("rawtypes") Class powerClass, String potionID) {
		powerMap.put(potionID, powerClass);
	}

	@SuppressWarnings("rawtypes")
	public static Class getPowerClass(String powerID) {
		return powerMap.get(powerID);
	}

	public static Set<String> getPowerKeys() {
		return powerMap.keySet();
	}

	//
	// Publishers
	//

	// publishStartAct -
	public static void publishStartAct() {
		logger.info("publishStartAct");
		for (StartActSubscriber sub : startActSubscribers) {
			sub.receiveStartAct();
		}
		unsubscribeLaterHelper(StartActSubscriber.class);
	}

	// publishPostCampfire - false allows an additional option to be selected
	public static boolean publishPostCampfire() {
		logger.info("publishPostCampfire");

		boolean campfireDone = true;

		for (PostCampfireSubscriber sub : postCampfireSubscribers) {
			if (!sub.receivePostCampfire()) {
				campfireDone = false;
			}
		}
		unsubscribeLaterHelper(PostCampfireSubscriber.class);

		return campfireDone;
	}

	// publishPostDraw -
	public static void publishPostDraw(AbstractCard c) {
		logger.info("publishPostDraw");
		for (PostDrawSubscriber sub : postDrawSubscribers) {
			sub.receivePostDraw(c);
		}
		unsubscribeLaterHelper(PostDrawSubscriber.class);
	}

	// publishPostExhaust -
	public static void publishPostExhaust(AbstractCard c) {
		logger.info("publishPostExhaust");
		for (PostExhaustSubscriber sub : postExhaustSubscribers) {
			sub.receivePostExhaust(c);
		}
		unsubscribeLaterHelper(PostExhaustSubscriber.class);
	}

	// publishPostDungeonInitialize -
	public static void publishPostDungeonInitialize() {
		logger.info("publishPostDungeonInitialize");

		for (PostDungeonInitializeSubscriber sub : postDungeonInitializeSubscribers) {
			sub.receivePostDungeonInitialize();
		}
		unsubscribeLaterHelper(PostDungeonInitializeSubscriber.class);
	}

	// publishPostEnergyRecharge -
	public static void publishPostEnergyRecharge() {
		logger.info("publishPostEnergyRecharge");
		for (PostEnergyRechargeSubscriber sub : postEnergyRechargeSubscribers) {
			sub.receivePostEnergyRecharge();
		}
		unsubscribeLaterHelper(PostEnergyRechargeSubscriber.class);
	}

	// publishPostInitialize -
	public static void publishPostInitialize() {
		logger.info("publishPostInitialize");

		// setup the necessary bits for custom animations to work
		setupAnimationGfx();

		// Publish
		for (PostInitializeSubscriber sub : postInitializeSubscribers) {
			sub.receivePostInitialize();
		}
		unsubscribeLaterHelper(PostInitializeSubscriber.class);
	}

	// publishPreMonsterTurn - false skips monster turn
	public static boolean publishPreMonsterTurn(AbstractMonster m) {
		logger.info("publishPreMonsterTurn");

		boolean takeTurn = true;

		for (PreMonsterTurnSubscriber sub : preMonsterTurnSubscribers) {
			if (!sub.receivePreMonsterTurn(m)) {
				takeTurn = false;
			}
		}
		unsubscribeLaterHelper(PreMonsterTurnSubscriber.class);

		return takeTurn;
	}

	// publishRender -
	public static void publishRender(SpriteBatch sb) {
		for (RenderSubscriber sub : renderSubscribers) {
			sub.receiveRender(sb);
		}
		unsubscribeLaterHelper(RenderSubscriber.class);
	}

	// publishAnimationRender -
	public static void publishAnimationRender(SpriteBatch sb) {
		if (modelRenderSubscribers.size() > 0) {
			// custom animations
			sb.end();
			CardCrawlGame.psb.begin();
			CardCrawlGame.psb.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
			CardCrawlGame.psb.draw(animationTextureRegion, 0, 0);
			CardCrawlGame.psb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			CardCrawlGame.psb.end();
			sb.begin();
		}
	}

	// publishPreRender -
	public static void publishPreRender(OrthographicCamera camera) {
		for (PreRenderSubscriber sub : preRenderSubscribers) {
			sub.receiveCameraRender(camera);
		}

		if (modelRenderSubscribers.size() > 0) {
			// custom animations
			animationBuffer.begin();
			Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin(animationCamera);

			for (ModelRenderSubscriber sub : modelRenderSubscribers) {
				sub.receiveModelRender(batch, animationEnvironment);
			}

			batch.end();
			animationBuffer.end();
			animationTexture = animationBuffer.getColorBufferTexture();
			animationTextureRegion = new TextureRegion(animationTexture);
			animationTextureRegion.flip(false, true);
		}

		unsubscribeLaterHelper(PreRenderSubscriber.class);
		unsubscribeLaterHelper(ModelRenderSubscriber.class);
	}

	// publishPostRender -
	public static void publishPostRender(SpriteBatch sb) {
		for (PostRenderSubscriber sub : postRenderSubscribers) {
			sub.receivePostRender(sb);
		}
		unsubscribeLaterHelper(PostRenderSubscriber.class);
	}

	// publishPreStartGame -
	public static void publishPreStartGame() {
		logger.info("publishPreStartGame");

		// Publish
		for (PreStartGameSubscriber sub : preStartGameSubscribers) {
			sub.receivePreStartGame();
		}
		unsubscribeLaterHelper(PreStartGameSubscriber.class);
	}

	public static void publishStartGame() {
		logger.info("publishStartGame");

		for (StartGameSubscriber sub : startGameSubscribers) {
			sub.receiveStartGame();
		}

		logger.info("mapDensityMultiplier: " + mapPathDensityMultiplier);

		unsubscribeLaterHelper(StartGameSubscriber.class);
	}

	// publishPreUpdate -
	public static void publishPreUpdate() {
		for (PreUpdateSubscriber sub : preUpdateSubscribers) {
			sub.receivePreUpdate();
		}
		unsubscribeLaterHelper(PreUpdateSubscriber.class);
	}

	// publishPostUpdate -
	public static void publishPostUpdate() {
		for (PostUpdateSubscriber sub : postUpdateSubscribers) {
			sub.receivePostUpdate();
		}
		unsubscribeLaterHelper(PostUpdateSubscriber.class);
	}

	// publishPostCreateStartingDeck -
	public static void publishPostCreateStartingDeck(PlayerClass chosenClass, ArrayList<String> cards) {
		logger.info("postCreateStartingDeck for: " + chosenClass);

		boolean clearDefault = false;
		ArrayList<String> cardsToAdd = new ArrayList<>();

		for (PostCreateStartingDeckSubscriber sub : postCreateStartingDeckSubscribers) {
			logger.info("postCreateStartingDeck modifying starting deck for: " + sub);
			switch (chosenClass) {
			case IRONCLAD:
				if (sub instanceof PostCreateIroncladStartingDeckSubscriber) {
					if (sub.receivePostCreateStartingDeck(cardsToAdd)) {
						clearDefault = true;
					}
				}
				break;
			case THE_SILENT:
				if (sub instanceof PostCreateSilentStartingDeckSubscriber) {
					if (sub.receivePostCreateStartingDeck(cardsToAdd)) {
						clearDefault = true;
					}
				}
				break;
			default:
				break;
			}
		}

		StringBuilder logString = new StringBuilder("postCreateStartingDeck adding [ ");
		for (String card : cardsToAdd) {
			logString.append(card).append(" ");
		}
		logString.append("]");
		logger.info(logString.toString());

		if (clearDefault) {
			logger.info("postCreateStartingDeck clearing initial deck");
			cards.clear();
		}
		cards.addAll(cardsToAdd);
		unsubscribeLaterHelper(PostCreateStartingDeckSubscriber.class);
	}

	// publishPostCreateStartingRelics -
	public static void publishPostCreateStartingRelics(PlayerClass chosenClass, ArrayList<String> relics) {
		logger.info("postCreateStartingRelics for: " + chosenClass);

		boolean clearDefault = false;
		ArrayList<String> relicsToAdd = new ArrayList<>();

		for (PostCreateStartingRelicsSubscriber sub : postCreateStartingRelicsSubscribers) {
			logger.info("postCreateStartingRelics modifying starting relics for: " + sub);
			switch (chosenClass) {
			case IRONCLAD:
				if (sub instanceof PostCreateIroncladStartingRelicsSubscriber) {
					if (sub.receivePostCreateStartingRelics(relicsToAdd)) {
						clearDefault = true;
					}
				}
				break;
			case THE_SILENT:
				if (sub instanceof PostCreateSilentStartingRelicsSubscriber) {
					if (sub.receivePostCreateStartingRelics(relicsToAdd)) {
						clearDefault = true;
					}
				}
				break;
			default:
				break;
			}
		}

		StringBuilder logString = new StringBuilder("postCreateStartingRelics adding [ ");
		for (String relic : relicsToAdd) {
			logString.append(relic).append(" ");
		}
		logString.append("]");
		logger.info(logString.toString());

		// mark as seen
		for (String relic : relicsToAdd) {
			UnlockTracker.markRelicAsSeen(relic);
		}

		// the default setup for adding starting relics does not do
		// equip triggers on the relics so we circumvent that by
		// adding relics ourself on dungeon initialize and force
		// the equip trigger
		subscribeToPostDungeonInitialize(() -> {
			int relicIndex = AbstractDungeon.player.relics.size();
			int relicRemoveIndex = relicsToAdd.size() - 1;
			while (relicsToAdd.size() > 0) {
				System.out.println("Attempting to add: " + relicsToAdd.get(relicRemoveIndex));
				AbstractRelic relic = RelicLibrary.getRelic(relicsToAdd.remove(relicRemoveIndex));
				System.out.println("Found relic is: " + relic);
				AbstractRelic relicCopy = relic.makeCopy();
				relicCopy.instantObtain(AbstractDungeon.player, relicIndex, true);
				relicRemoveIndex--;
				relicIndex++;
			}
		});

		if (clearDefault) {
			logger.info("postCreateStartingRelics clearing initial relics");
			relics.clear();
		}

		AbstractDungeon.relicsToRemoveOnStart.addAll(relicsToAdd);
		unsubscribeLaterHelper(PostCreateStartingRelicsSubscriber.class);
	}

	// publishPostCreateShopRelic -
	public static void publishPostCreateShopRelics(ArrayList<StoreRelic> relics, ShopScreen screenInstance) {
		logger.info("postCreateShopRelics for: " + relics);

		for (PostCreateShopRelicSubscriber sub : postCreateShopRelicSubscribers) {
			sub.receiveCreateShopRelics(relics, screenInstance);
		}
		unsubscribeLaterHelper(PostCreateShopRelicSubscriber.class);
	}

	// publishPostCreateShopPotion -
	public static void publishPostCreateShopPotions(ArrayList<StorePotion> potions, ShopScreen screenInstance) {
		logger.info("postCreateShopPotions for: " + potions);

		for (PostCreateShopPotionSubscriber sub : postCreateShopPotionSubscribers) {
			sub.receiveCreateShopPotions(potions, screenInstance);
		}
		unsubscribeLaterHelper(PostCreateShopPotionSubscriber.class);
	}

	// publishEditCards -
	public static void publishEditCards() {
		logger.info("begin editing cards");

		BaseMod.addDynamicVariable(new DamageVariable());
		BaseMod.addDynamicVariable(new BlockVariable());
		BaseMod.addDynamicVariable(new MagicNumberVariable());

		for (EditCardsSubscriber sub : editCardsSubscribers) {
			sub.receiveEditCards();
		}
		unsubscribeLaterHelper(EditCardsSubscriber.class);
	}

	// publishEditRelics -
	public static void publishEditRelics() {
		logger.info("begin editing relics");

		for (EditRelicsSubscriber sub : editRelicsSubscribers) {
			sub.receiveEditRelics();
		}
		unsubscribeLaterHelper(EditRelicsSubscriber.class);
	}

	// publishEditCharacters -
	public static void publishEditCharacters() {
		logger.info("begin editing characters");

		for (EditCharactersSubscriber sub : editCharactersSubscribers) {
			sub.receiveEditCharacters();
		}
		unsubscribeLaterHelper(EditCharactersSubscriber.class);
	}

	// publishEditStrings -
	public static void publishEditStrings() {
		logger.info("begin editing localization strings");

		for (EditStringsSubscriber sub : editStringsSubscribers) {
			sub.receiveEditStrings();
		}
		unsubscribeLaterHelper(EditStringsSubscriber.class);
	}

	// publishPostBattle -
	public static void publishPostBattle(AbstractRoom battleRoom) {
		logger.info("publish post combat");

		for (PostBattleSubscriber sub : postBattleSubscribers) {
			sub.receivePostBattle(battleRoom);
		}
		unsubscribeLaterHelper(PostBattleSubscriber.class);
	}

	public static void publishStartBattle(MonsterRoom monsterRoom){
		logger.info("publish start battle");

		for (OnStartBattleSubscriber sub : startBattleSubscribers) {
			sub.receiveOnBattleStart(monsterRoom);
		}
		unsubscribeLaterHelper(OnStartBattleSubscriber.class);
	}

	// publishPostRefresh -
	public static void publishPostRefresh() {
		logger.info("publish post refresh - refreshing unlocks");

		for (SetUnlocksSubscriber sub : setUnlocksSubscribers) {
			sub.receiveSetUnlocks();
		}
		unsubscribeLaterHelper(SetUnlocksSubscriber.class);
	}

	// publishOnCardUse -
	public static void publishOnCardUse(AbstractCard c) {
		logger.info("publish on card use");

		for (OnCardUseSubscriber sub : onCardUseSubscribers) {
			sub.receiveCardUsed(c);
		}
		unsubscribeLaterHelper(OnCardUseSubscriber.class);
	}

	// publishPostUsePotion -
	public static void publishPostPotionUse(AbstractPotion p) {
		logger.info("publish on post potion use");
		for (PostPotionUseSubscriber sub : postPotionUseSubscribers) {
			sub.receivePostPotionUse(p);
		}
		unsubscribeLaterHelper(PostPotionUseSubscriber.class);
	}

	// publishPostPotionUse -
	public static void publishPrePotionUse(AbstractPotion p) {
		logger.info("publish on pre potion use");

		for (PrePotionUseSubscriber sub : prePotionUseSubscribers) {
			sub.receivePrePotionUse(p);
		}
		unsubscribeLaterHelper(PrePotionUseSubscriber.class);
	}

	// publishPotionGet -
	public static void publishPotionGet(AbstractPotion p) {
		logger.info("publish on potion get");

		for (PotionGetSubscriber sub : potionGetSubscribers) {
			sub.receivePotionGet(p);
		}
		unsubscribeLaterHelper(PotionGetSubscriber.class);
	}

	// publishRelicGet -
	public static void publishRelicGet(AbstractRelic r) {
		logger.info("publish on relic get");
		for (RelicGetSubscriber sub : relicGetSubscribers) {
			sub.receiveRelicGet(r);
		}
		unsubscribeLaterHelper(RelicGetSubscriber.class);
	}

	// publishPostPowerApply
	public static void publishPostPowerApply(AbstractPower p, AbstractCreature target, AbstractCreature source) {
		logger.info("publish on post power apply");

		for (PostPowerApplySubscriber sub : postPowerApplySubscribers) {
			sub.receivePostPowerApplySubscriber(p, target, source);
		}
		unsubscribeLaterHelper(PostPowerApplySubscriber.class);
	}

	// publishEditKeywords
	public static void publishEditKeywords() {
		logger.info("editting keywords");

		addKeyword(new String[] { "[E]" }, GameDictionary.TEXT[0]);

		for (EditKeywordsSubscriber sub : editKeywordsSubscribers) {
			sub.receiveEditKeywords();
		}
		unsubscribeLaterHelper(EditKeywordsSubscriber.class);
	}

	// publishOnPowersModified
	public static void publishOnPowersModified() {
		logger.info("powers modified");

		for (OnPowersModifiedSubscriber sub : onPowersModifiedSubscribers) {
			sub.receivePowersModified();
		}
		unsubscribeLaterHelper(OnPowersModifiedSubscriber.class);
	}

	// publishPostDeath - Is triggered on death and victory
	public static void publishPostDeath() {
		logger.info("publishPostDeath");

		for (PostDeathSubscriber sub : postDeathSubscribers) {
			sub.receivePostDeath();
		}
		unsubscribeLaterHelper(PostDeathSubscriber.class);
	}

	//
	// Subscription handlers
	//

	// unsubscribes all elements of toRemove that are of type removalClass
	private static void unsubscribeLaterHelper(Class<? extends ISubscriber> removalClass) {
		for (ISubscriber sub : toRemove) {
			if (removalClass.isInstance(sub)) {
				unsubscribe(sub, removalClass);
			}
		}
	}

	// not actually unchecked because we do an isInstance check at runtime
	@SuppressWarnings("unchecked")
	private static <T> void subscribeIfInstance(ArrayList<T> list, ISubscriber sub, Class<T> clazz) {
		if (clazz.isInstance(sub)) {
			list.add((T) sub);
		}
	}

	// not actually unchecked because we do an isInstance check at runtime
	@SuppressWarnings("unchecked")
	private static <T> void unsubscribeIfInstance(ArrayList<T> list, ISubscriber sub, Class<T> clazz) {
		if (clazz.isInstance(sub)) {
			list.remove(sub);
		}
	}

	// subscribe -
	// will subscribe to all lists this sub implements
	public static void subscribe(ISubscriber sub) {
		subscribeIfInstance(startActSubscribers, sub, StartActSubscriber.class);
		subscribeIfInstance(postCampfireSubscribers, sub, PostCampfireSubscriber.class);
		subscribeIfInstance(postDrawSubscribers, sub, PostDrawSubscriber.class);
		subscribeIfInstance(postExhaustSubscribers, sub, PostExhaustSubscriber.class);
		subscribeIfInstance(onCardUseSubscribers, sub, OnCardUseSubscriber.class);
		subscribeIfInstance(postDungeonInitializeSubscribers, sub, PostDungeonInitializeSubscriber.class);
		subscribeIfInstance(postEnergyRechargeSubscribers, sub, PostEnergyRechargeSubscriber.class);
		subscribeIfInstance(postInitializeSubscribers, sub, PostInitializeSubscriber.class);
		subscribeIfInstance(preMonsterTurnSubscribers, sub, PreMonsterTurnSubscriber.class);
		subscribeIfInstance(renderSubscribers, sub, RenderSubscriber.class);
		subscribeIfInstance(preRenderSubscribers, sub, PreRenderSubscriber.class);
		subscribeIfInstance(postRenderSubscribers, sub, PostRenderSubscriber.class);
		subscribeIfInstance(modelRenderSubscribers, sub, ModelRenderSubscriber.class);
		subscribeIfInstance(preStartGameSubscribers, sub, PreStartGameSubscriber.class);
		subscribeIfInstance(startGameSubscribers, sub, StartGameSubscriber.class);
		subscribeIfInstance(preUpdateSubscribers, sub, PreUpdateSubscriber.class);
		subscribeIfInstance(postUpdateSubscribers, sub, PostUpdateSubscriber.class);
		subscribeIfInstance(postCreateStartingDeckSubscribers, sub, PostCreateStartingDeckSubscriber.class);
		subscribeIfInstance(postCreateStartingRelicsSubscribers, sub, PostCreateStartingRelicsSubscriber.class);
		subscribeIfInstance(postCreateShopRelicSubscribers, sub, PostCreateShopRelicSubscriber.class);
		subscribeIfInstance(postCreateShopPotionSubscribers, sub, PostCreateShopPotionSubscriber.class);
		subscribeIfInstance(editCardsSubscribers, sub, EditCardsSubscriber.class);
		subscribeIfInstance(editRelicsSubscribers, sub, EditRelicsSubscriber.class);
		subscribeIfInstance(editCharactersSubscribers, sub, EditCharactersSubscriber.class);
		subscribeIfInstance(editStringsSubscribers, sub, EditStringsSubscriber.class);
		subscribeIfInstance(editKeywordsSubscribers, sub, EditKeywordsSubscriber.class);
		subscribeIfInstance(postBattleSubscribers, sub, PostBattleSubscriber.class);
		subscribeIfInstance(setUnlocksSubscribers, sub, SetUnlocksSubscriber.class);
		subscribeIfInstance(postPotionUseSubscribers, sub, PostPotionUseSubscriber.class);
		subscribeIfInstance(prePotionUseSubscribers, sub, PrePotionUseSubscriber.class);
		subscribeIfInstance(potionGetSubscribers, sub, PotionGetSubscriber.class);
		subscribeIfInstance(relicGetSubscribers, sub, RelicGetSubscriber.class);
		subscribeIfInstance(postPowerApplySubscribers, sub, PostPowerApplySubscriber.class);
		subscribeIfInstance(onPowersModifiedSubscribers, sub, OnPowersModifiedSubscriber.class);
		subscribeIfInstance(postDeathSubscribers, sub, PostDeathSubscriber.class);
		subscribeIfInstance(startBattleSubscribers, sub, OnStartBattleSubscriber.class);
	}

	// subscribe -
	// only subscribers to a specific list
	public static void subscribe(ISubscriber sub, Class<? extends ISubscriber> additionClass) {
		if (additionClass.equals(StartActSubscriber.class)) {
			startActSubscribers.add((StartActSubscriber) sub);
		} else if (additionClass.equals(PostCampfireSubscriber.class)) {
			postCampfireSubscribers.add((PostCampfireSubscriber) sub);
		} else if (additionClass.equals(PostDrawSubscriber.class)) {
			postDrawSubscribers.add((PostDrawSubscriber) sub);
		} else if (additionClass.equals(PostExhaustSubscriber.class)) {
			postExhaustSubscribers.add((PostExhaustSubscriber) sub);
		} else if (additionClass.equals(OnCardUseSubscriber.class)) {
			onCardUseSubscribers.add((OnCardUseSubscriber) sub);
		} else if (additionClass.equals(PostDungeonInitializeSubscriber.class)) {
			postDungeonInitializeSubscribers.add((PostDungeonInitializeSubscriber) sub);
		} else if (additionClass.equals(PostEnergyRechargeSubscriber.class)) {
			postEnergyRechargeSubscribers.add((PostEnergyRechargeSubscriber) sub);
		} else if (additionClass.equals(PostInitializeSubscriber.class)) {
			postInitializeSubscribers.add((PostInitializeSubscriber) sub);
		} else if (additionClass.equals(PreMonsterTurnSubscriber.class)) {
			preMonsterTurnSubscribers.add((PreMonsterTurnSubscriber) sub);
		} else if (additionClass.equals(RenderSubscriber.class)) {
			renderSubscribers.add((RenderSubscriber) sub);
		} else if (additionClass.equals(PreRenderSubscriber.class)) {
			preRenderSubscribers.add((PreRenderSubscriber) sub);
		} else if (additionClass.equals(PostRenderSubscriber.class)) {
			postRenderSubscribers.add((PostRenderSubscriber) sub);
		} else if (additionClass.equals(ModelRenderSubscriber.class)) {
			modelRenderSubscribers.add((ModelRenderSubscriber) sub);
		} else if (additionClass.equals(PreStartGameSubscriber.class)) {
			preStartGameSubscribers.add((PreStartGameSubscriber) sub);
		} else if (additionClass.equals(StartGameSubscriber.class)) {
			startGameSubscribers.add((StartGameSubscriber) sub);
		} else if (additionClass.equals(PreUpdateSubscriber.class)) {
			preUpdateSubscribers.add((PreUpdateSubscriber) sub);
		} else if (additionClass.equals(PostUpdateSubscriber.class)) {
			postUpdateSubscribers.add((PostUpdateSubscriber) sub);
		} else if (additionClass.equals(PostCreateStartingDeckSubscriber.class)) {
			postCreateStartingDeckSubscribers.add((PostCreateStartingDeckSubscriber) sub);
		} else if (additionClass.equals(PostCreateStartingRelicsSubscriber.class)) {
			postCreateStartingRelicsSubscribers.add((PostCreateStartingRelicsSubscriber) sub);
		} else if (additionClass.equals(PostCreateShopRelicSubscriber.class)) {
			postCreateShopRelicSubscribers.add((PostCreateShopRelicSubscriber) sub);
		} else if (additionClass.equals(PostCreateShopPotionSubscriber.class)) {
			postCreateShopPotionSubscribers.add((PostCreateShopPotionSubscriber) sub);
		} else if (additionClass.equals(EditCardsSubscriber.class)) {
			editCardsSubscribers.add((EditCardsSubscriber) sub);
		} else if (additionClass.equals(EditRelicsSubscriber.class)) {
			editRelicsSubscribers.add((EditRelicsSubscriber) sub);
		} else if (additionClass.equals(EditCharactersSubscriber.class)) {
			editCharactersSubscribers.add((EditCharactersSubscriber) sub);
		} else if (additionClass.equals(EditStringsSubscriber.class)) {
			editStringsSubscribers.add((EditStringsSubscriber) sub);
		} else if (additionClass.equals(EditKeywordsSubscriber.class)) {
			editKeywordsSubscribers.add((EditKeywordsSubscriber) sub);
		} else if (additionClass.equals(PostBattleSubscriber.class)) {
			postBattleSubscribers.add((PostBattleSubscriber) sub);
		} else if (additionClass.equals(SetUnlocksSubscriber.class)) {
			setUnlocksSubscribers.add((SetUnlocksSubscriber) sub);
		} else if (additionClass.equals(PostPotionUseSubscriber.class)) {
			postPotionUseSubscribers.add((PostPotionUseSubscriber) sub);
		} else if (additionClass.equals(PrePotionUseSubscriber.class)) {
			prePotionUseSubscribers.add((PrePotionUseSubscriber) sub);
		} else if (additionClass.equals(PotionGetSubscriber.class)) {
			potionGetSubscribers.add((PotionGetSubscriber) sub);
		} else if (additionClass.equals(RelicGetSubscriber.class)) {
			relicGetSubscribers.add((RelicGetSubscriber) sub);
		} else if (additionClass.equals(PostPowerApplySubscriber.class)) {
			postPowerApplySubscribers.add((PostPowerApplySubscriber) sub);
		} else if (additionClass.equals(OnPowersModifiedSubscriber.class)) {
			onPowersModifiedSubscribers.add((OnPowersModifiedSubscriber) sub);
		} else if (additionClass.equals(PostDeathSubscriber.class)) {
			postDeathSubscribers.add((PostDeathSubscriber) sub);
		} else if (additionClass.equals(OnStartBattleSubscriber.class)) {
			startBattleSubscribers.add((OnStartBattleSubscriber) sub);
		}
	}

	// unsubscribe -
	// will unsubscribe from all lists this sub implements
	public static void unsubscribe(ISubscriber sub) {
		unsubscribeIfInstance(startActSubscribers, sub, StartActSubscriber.class);
		unsubscribeIfInstance(postCampfireSubscribers, sub, PostCampfireSubscriber.class);
		unsubscribeIfInstance(postDrawSubscribers, sub, PostDrawSubscriber.class);
		unsubscribeIfInstance(postExhaustSubscribers, sub, PostExhaustSubscriber.class);
		unsubscribeIfInstance(onCardUseSubscribers, sub, OnCardUseSubscriber.class);
		unsubscribeIfInstance(postDungeonInitializeSubscribers, sub, PostDungeonInitializeSubscriber.class);
		unsubscribeIfInstance(postEnergyRechargeSubscribers, sub, PostEnergyRechargeSubscriber.class);
		unsubscribeIfInstance(postInitializeSubscribers, sub, PostInitializeSubscriber.class);
		unsubscribeIfInstance(preMonsterTurnSubscribers, sub, PreMonsterTurnSubscriber.class);
		unsubscribeIfInstance(renderSubscribers, sub, RenderSubscriber.class);
		unsubscribeIfInstance(preRenderSubscribers, sub, PreRenderSubscriber.class);
		unsubscribeIfInstance(postRenderSubscribers, sub, PostRenderSubscriber.class);
		unsubscribeIfInstance(modelRenderSubscribers, sub, ModelRenderSubscriber.class);
		unsubscribeIfInstance(preStartGameSubscribers, sub, PreStartGameSubscriber.class);
		unsubscribeIfInstance(startGameSubscribers, sub, StartGameSubscriber.class);
		unsubscribeIfInstance(preUpdateSubscribers, sub, PreUpdateSubscriber.class);
		unsubscribeIfInstance(postUpdateSubscribers, sub, PostUpdateSubscriber.class);
		unsubscribeIfInstance(postCreateStartingDeckSubscribers, sub, PostCreateStartingDeckSubscriber.class);
		unsubscribeIfInstance(postCreateStartingRelicsSubscribers, sub, PostCreateStartingRelicsSubscriber.class);
		unsubscribeIfInstance(postCreateShopRelicSubscribers, sub, PostCreateShopRelicSubscriber.class);
		unsubscribeIfInstance(postCreateShopPotionSubscribers, sub, PostCreateShopPotionSubscriber.class);
		unsubscribeIfInstance(editCardsSubscribers, sub, EditCardsSubscriber.class);
		unsubscribeIfInstance(editRelicsSubscribers, sub, EditRelicsSubscriber.class);
		unsubscribeIfInstance(editCharactersSubscribers, sub, EditCharactersSubscriber.class);
		unsubscribeIfInstance(editStringsSubscribers, sub, EditStringsSubscriber.class);
		unsubscribeIfInstance(editKeywordsSubscribers, sub, EditKeywordsSubscriber.class);
		unsubscribeIfInstance(postBattleSubscribers, sub, PostBattleSubscriber.class);
		unsubscribeIfInstance(setUnlocksSubscribers, sub, SetUnlocksSubscriber.class);
		unsubscribeIfInstance(postPotionUseSubscribers, sub, PostPotionUseSubscriber.class);
		unsubscribeIfInstance(prePotionUseSubscribers, sub, PrePotionUseSubscriber.class);
		unsubscribeIfInstance(potionGetSubscribers, sub, PotionGetSubscriber.class);
		unsubscribeIfInstance(relicGetSubscribers, sub, RelicGetSubscriber.class);
		unsubscribeIfInstance(postPowerApplySubscribers, sub, PostPowerApplySubscriber.class);
		unsubscribeIfInstance(onPowersModifiedSubscribers, sub, OnPowersModifiedSubscriber.class);
		unsubscribeIfInstance(postDeathSubscribers, sub, PostDeathSubscriber.class);
		unsubscribeIfInstance(startBattleSubscribers, sub, OnStartBattleSubscriber.class);
	}

	// unsubscribe -
	// only unsubscribe from a specific list
	public static void unsubscribe(ISubscriber sub, Class<? extends ISubscriber> removalClass) {
		if (removalClass.equals(StartActSubscriber.class)) {
			startActSubscribers.remove(sub);
		} else if (removalClass.equals(PostCampfireSubscriber.class)) {
			postCampfireSubscribers.remove(sub);
		} else if (removalClass.equals(PostDrawSubscriber.class)) {
			postDrawSubscribers.remove(sub);
		} else if (removalClass.equals(PostExhaustSubscriber.class)) {
			postExhaustSubscribers.remove(sub);
		} else if (removalClass.equals(OnCardUseSubscriber.class)) {
			onCardUseSubscribers.remove(sub);
		} else if (removalClass.equals(PostDungeonInitializeSubscriber.class)) {
			postDungeonInitializeSubscribers.remove(sub);
		} else if (removalClass.equals(PostEnergyRechargeSubscriber.class)) {
			postEnergyRechargeSubscribers.remove(sub);
		} else if (removalClass.equals(PostInitializeSubscriber.class)) {
			postInitializeSubscribers.remove(sub);
		} else if (removalClass.equals(PreMonsterTurnSubscriber.class)) {
			preMonsterTurnSubscribers.remove(sub);
		} else if (removalClass.equals(RenderSubscriber.class)) {
			renderSubscribers.remove(sub);
		} else if (removalClass.equals(PreRenderSubscriber.class)) {
			preRenderSubscribers.remove(sub);
		} else if (removalClass.equals(PostRenderSubscriber.class)) {
			postRenderSubscribers.remove(sub);
		} else if (removalClass.equals(ModelRenderSubscriber.class)) {
			modelRenderSubscribers.remove(sub);
		} else if (removalClass.equals(PreStartGameSubscriber.class)) {
			preStartGameSubscribers.remove(sub);
		} else if (removalClass.equals(StartGameSubscriber.class)) {
			startGameSubscribers.remove(sub);
		} else if (removalClass.equals(PreUpdateSubscriber.class)) {
			preUpdateSubscribers.remove(sub);
		} else if (removalClass.equals(PostUpdateSubscriber.class)) {
			postUpdateSubscribers.remove(sub);
		} else if (removalClass.equals(PostCreateStartingDeckSubscriber.class)) {
			postCreateStartingDeckSubscribers.remove(sub);
		} else if (removalClass.equals(PostCreateStartingRelicsSubscriber.class)) {
			postCreateStartingRelicsSubscribers.remove(sub);
		} else if (removalClass.equals(PostCreateShopRelicSubscriber.class)) {
			postCreateShopRelicSubscribers.remove(sub);
		} else if (removalClass.equals(PostCreateShopPotionSubscriber.class)) {
			postCreateShopPotionSubscribers.remove(sub);
		} else if (removalClass.equals(EditCardsSubscriber.class)) {
			editCardsSubscribers.remove(sub);
		} else if (removalClass.equals(EditRelicsSubscriber.class)) {
			editRelicsSubscribers.remove(sub);
		} else if (removalClass.equals(EditCharactersSubscriber.class)) {
			editCharactersSubscribers.remove(sub);
		} else if (removalClass.equals(EditStringsSubscriber.class)) {
			editStringsSubscribers.remove(sub);
		} else if (removalClass.equals(EditKeywordsSubscriber.class)) {
			editKeywordsSubscribers.remove(sub);
		} else if (removalClass.equals(PostBattleSubscriber.class)) {
			postBattleSubscribers.remove(sub);
		} else if (removalClass.equals(SetUnlocksSubscriber.class)) {
			setUnlocksSubscribers.remove(sub);
		} else if (removalClass.equals(PostPotionUseSubscriber.class)) {
			postPotionUseSubscribers.remove(sub);
		} else if (removalClass.equals(PrePotionUseSubscriber.class)) {
			prePotionUseSubscribers.remove(sub);
		} else if (removalClass.equals(PotionGetSubscriber.class)) {
			potionGetSubscribers.remove(sub);
		} else if (removalClass.equals(RelicGetSubscriber.class)) {
			relicGetSubscribers.remove(sub);
		} else if (removalClass.equals(PostPowerApplySubscriber.class)) {
			postPowerApplySubscribers.remove(sub);
		} else if (removalClass.equals(OnPowersModifiedSubscriber.class)) {
			onPowersModifiedSubscribers.remove(sub);
		} else if (removalClass.equals(PostDeathSubscriber.class)) {
			postDeathSubscribers.remove(sub);
		} else if (removalClass.equals(OnStartBattleSubscriber.class)) {
			startBattleSubscribers.remove(sub);
		}
	}

	// unsubscribeLater -
	public static void unsubscribeLater(ISubscriber sub) {
		toRemove.add(sub);
	}

	public static String convertToModID(String id) {
		String modName = BaseMod.findCallingModName();
		return convertToModID(modName, id);
	}

	public static String convertToModID(String modID, String id) {
		if (modID == null && (id.startsWith("slaythespire:") || id.startsWith("sts:") || id.startsWith(":"))) {
			return id.substring(id.indexOf(':') + 1);
		} else if (modID != null && !id.startsWith(modID + ":")) {
			id = modID + ":" + id;
		}
		return id;
	}

	public static boolean hasModID(String id) {
		for (ModInfo info : Loader.MODINFOS) {
			String modID = null;
			if (info.ID != null && !info.ID.isEmpty()) {
				modID = info.ID;
			} else {
				modID = info.Name;
			}
			if (id.startsWith(modID + ":")) {
				return true;
			}
		}
		return false;
	}

	public static String findCallingModName() {
		return null;
	}

	// subscribeToStartAct -
	@Deprecated
	public static void subscribeToStartAct(StartActSubscriber sub) {
		startActSubscribers.add(sub);
	}

	// unsubscribeFromStartAct -
	@Deprecated
	public static void unsubscribeFromStartAct(StartActSubscriber sub) {
		startActSubscribers.remove(sub);
	}

	// subscribeToPostCampfire -
	@Deprecated
	public static void subscribeToPostCampfire(PostCampfireSubscriber sub) {
		postCampfireSubscribers.add(sub);
	}

	// unsubscribeFromPostCampfire -
	@Deprecated
	public static void unsubscribeFromPostCampfire(PostCampfireSubscriber sub) {
		postCampfireSubscribers.remove(sub);
	}

	// subscribeToPostDraw -
	@Deprecated
	public static void subscribeToPostDraw(PostDrawSubscriber sub) {
		postDrawSubscribers.add(sub);
	}

	// unsubscribeFromPostDraw -
	@Deprecated
	public static void unsubscribeFromPostDraw(PostDrawSubscriber sub) {
		postDrawSubscribers.remove(sub);
	}

	// subscribeToPostExhaust -
	@Deprecated
	public static void subscribeToPostExhaust(PostExhaustSubscriber sub) {
		postExhaustSubscribers.add(sub);
	}

	// unsubscribeFromPostExhaust -
	@Deprecated
	public static void unsubscribeFromPostExhaust(PostExhaustSubscriber sub) {
		postExhaustSubscribers.remove(sub);
	}

	// subscribeToPostDungeonInitialize -
	@Deprecated
	public static void subscribeToPostDungeonInitialize(PostDungeonInitializeSubscriber sub) {
		postDungeonInitializeSubscribers.add(sub);
	}

	// unsubcribeFromPostDungeonInitialize -
	@Deprecated
	public static void unsubscribeFromPostDungeonInitialize(PostDungeonInitializeSubscriber sub) {
		postDungeonInitializeSubscribers.remove(sub);
	}

	// subscribeToPostEnergyRecharge -
	@Deprecated
	public static void subscribeToPostEnergyRecharge(PostEnergyRechargeSubscriber sub) {
		postEnergyRechargeSubscribers.add(sub);
	}

	// unsubscribeFromPostEnergyRecharge -
	@Deprecated
	public static void unsubscribeFromPostEnergyRecharge(PostEnergyRechargeSubscriber sub) {
		postEnergyRechargeSubscribers.remove(sub);
	}

	// subscribeToPreMonsterTurn -
	@Deprecated
	public static void subscribeToPreMonsterTurn(PreMonsterTurnSubscriber sub) {
		preMonsterTurnSubscribers.add(sub);
	}

	// unsubscribeFromPreMonsterTurn -
	@Deprecated
	public static void unsubscribeFromPreMonsterTurn(PreMonsterTurnSubscriber sub) {
		preMonsterTurnSubscribers.remove(sub);
	}

	// subscribeToPostInitialize -
	@Deprecated
	public static void subscribeToPostInitialize(PostInitializeSubscriber sub) {
		postInitializeSubscribers.add(sub);
	}

	// unsubscribeFromPostRender -
	@Deprecated
	public static void unsubscribeFromPostInitialize(PostInitializeSubscriber sub) {
		postInitializeSubscribers.remove(sub);
	}

	// subscribeToRender -
	@Deprecated
	public static void subscribeToRender(RenderSubscriber sub) {
		renderSubscribers.add(sub);
	}

	// unsubscribeFromRender -
	@Deprecated
	public static void unsubscribeFromRender(RenderSubscriber sub) {
		renderSubscribers.remove(sub);
	}

	// subscribeToPreRender -
	@Deprecated
	public static void subscribeToPreRender(PreRenderSubscriber sub) {
		preRenderSubscribers.add(sub);
	}

	// unsubscribeFromPreRender -
	@Deprecated
	public static void unsubscribeFromPreRender(PreRenderSubscriber sub) {
		preRenderSubscribers.remove(sub);
	}

	// subscribeToPostRender -
	@Deprecated
	public static void subscribeToPostRender(PostRenderSubscriber sub) {
		postRenderSubscribers.add(sub);
	}

	// unsubscribeFromPostRender -
	@Deprecated
	public static void unsubscribeFromPostRender(PostRenderSubscriber sub) {
		postRenderSubscribers.remove(sub);
	}

	// subscribeToModelRender -
	@Deprecated
	public static void subscribeToModelRender(ModelRenderSubscriber sub) {
		modelRenderSubscribers.add(sub);
	}

	// unsubscribeFromModelRender -
	@Deprecated
	public static void unsubscribeFromModelRender(ModelRenderSubscriber sub) {
		modelRenderSubscribers.remove(sub);
	}

	// subscribeToStartGame -
	@Deprecated
	public static void subscribeToStartGame(StartGameSubscriber sub) {
		startGameSubscribers.add(sub);
	}

	// unsubscribeFromStartGame -
	@Deprecated
	public static void unsubscribeFromStartGame(StartGameSubscriber sub) {
		startGameSubscribers.remove(sub);
	}

	// subscribeToPreStartGame -
	@Deprecated
	public static void subscribeToPreStartGame(PreStartGameSubscriber sub) {
		preStartGameSubscribers.add(sub);
	}

	// unsubscribeFromPreStartGame -
	@Deprecated
	public static void unsubscribeFromPreStartGame(PreStartGameSubscriber sub) {
		preStartGameSubscribers.remove(sub);
	}

	// subscribeToPreUpdate -
	@Deprecated
	public static void subscribeToPreUpdate(PreUpdateSubscriber sub) {
		preUpdateSubscribers.add(sub);
	}

	// unsubscribeFromPreUpdate -
	@Deprecated
	public static void unsubscribeFromPreUpdate(PreUpdateSubscriber sub) {
		preUpdateSubscribers.remove(sub);
	}

	// subscribeToPostUpdate -
	@Deprecated
	public static void subscribeToPostUpdate(PostUpdateSubscriber sub) {
		postUpdateSubscribers.add(sub);
	}

	// unsubscribeFromUpdate -
	@Deprecated
	public static void unsubscribeFromPostUpdate(PostUpdateSubscriber sub) {
		postUpdateSubscribers.remove(sub);
	}

	// subscribeToPostCreateStartingDeck -
	@Deprecated
	public static void subscribeToPostCreateStartingDeck(PostCreateStartingDeckSubscriber sub) {
		postCreateStartingDeckSubscribers.add(sub);
	}

	// unsubscribeToPostCreateStartingDeck -
	@Deprecated
	public static void unsubscribeToPostCreateStartingDeck(PostCreateStartingDeckSubscriber sub) {
		postCreateStartingDeckSubscribers.remove(sub);
	}

	// subscribeToPostCreateStartingRelics -
	@Deprecated
	public static void subscribeToPostCreateStartingRelics(PostCreateStartingRelicsSubscriber sub) {
		postCreateStartingRelicsSubscribers.add(sub);
	}

	// unsubscribeToPostCreateStartingRelics -
	@Deprecated
	public static void unsubscribeToPostCreateStartingRelics(PostCreateStartingRelicsSubscriber sub) {
		postCreateStartingRelicsSubscribers.remove(sub);
	}

	// subscribeToPostCreateShopRelic -
	@Deprecated
	public static void subscribeToPostCreateShopRelic(PostCreateShopRelicSubscriber sub) {
		postCreateShopRelicSubscribers.add(sub);
	}

	// unsubscribeToPostCreateShopRelic
	@Deprecated
	public static void unsubscribeToPostCreateShopRelic(PostCreateShopRelicSubscriber sub) {
		postCreateShopRelicSubscribers.remove(sub);
	}

	// subscribeToPostCreateShopPotion -
	@Deprecated
	public static void subscribeToPostCreateShopPotion(PostCreateShopPotionSubscriber sub) {
		postCreateShopPotionSubscribers.add(sub);
	}

	// unsubscribeToPostCreateShopRelic
	@Deprecated
	public static void unsubscribeToPostCreateShopPotion(PostCreateShopPotionSubscriber sub) {
		postCreateShopPotionSubscribers.remove(sub);
	}

	// subscribeToEditCards -
	@Deprecated
	public static void subscribeToEditCards(EditCardsSubscriber sub) {
		editCardsSubscribers.add(sub);
	}

	// unsubscribeToEditCards -
	@Deprecated
	public static void unsubscribeToEditCards(EditCardsSubscriber sub) {
		editCardsSubscribers.remove(sub);
	}

	// subscribeToEditRelics -
	@Deprecated
	public static void subscribeToEditRelics(EditRelicsSubscriber sub) {
		editRelicsSubscribers.add(sub);
	}

	// unsubscribeToEditRelics -
	@Deprecated
	public static void unsubscribeToEditRelics(EditRelicsSubscriber sub) {
		editRelicsSubscribers.remove(sub);
	}

	// subscribeToEditCharacters -
	@Deprecated
	public static void subscribeToEditCharacters(EditCharactersSubscriber sub) {
		editCharactersSubscribers.add(sub);
	}

	// unsubscribeToEditCharacters -
	@Deprecated
	public static void unsubscribeToEditCharacters(EditCharactersSubscriber sub) {
		editCharactersSubscribers.remove(sub);
	}

	// subscribeToEditStrings
	@Deprecated
	public static void subscribeToEditStrings(EditStringsSubscriber sub) {
		editStringsSubscribers.add(sub);
	}

	// unsubscribeToEditStrings
	@Deprecated
	public static void unsubscribeToEditStrings(EditStringsSubscriber sub) {
		editStringsSubscribers.remove(sub);
	}

	// subscribeToPostBattle
	@Deprecated
	public static void subscribeToPostBattle(PostBattleSubscriber sub) {
		postBattleSubscribers.add(sub);
	}

	// unsubscribeToPostBattle
	@Deprecated
	public static void unsubscribeFromPostBattle(PostBattleSubscriber sub) {
		postBattleSubscribers.remove(sub);
	}

	// subscribeToSetUnlocks
	@Deprecated
	public static void subscribeToSetUnlocks(SetUnlocksSubscriber sub) {
		setUnlocksSubscribers.add(sub);
	}

	// unsubscribeFromSetUnlocks
	@Deprecated
	public static void unsubscribeFromSetUnlocks(SetUnlocksSubscriber sub) {
		setUnlocksSubscribers.remove(sub);
	}

	// subscribeToOnCardUse
	@Deprecated
	public static void subscribeToOnCardUse(OnCardUseSubscriber sub) {
		onCardUseSubscribers.add(sub);
	}

	// unsubscribeFromOnCardUse
	@Deprecated
	public static void unsubscribeFromOnCardUse(OnCardUseSubscriber sub) {
		onCardUseSubscribers.remove(sub);
	}

	// subscribeToPostPotionUse
	@Deprecated
	public static void subscribeToPostPotionUse(PostPotionUseSubscriber sub) {
		postPotionUseSubscribers.add(sub);
	}

	// unsubscribeFromOnPostPotionUse
	@Deprecated
	public static void unsubscribeFromPostPotionUse(PostPotionUseSubscriber sub) {
		postPotionUseSubscribers.remove(sub);
	}

	// subscribeToprePotionUse
	@Deprecated
	public static void subscribeToPrePotionUse(PrePotionUseSubscriber sub) {
		prePotionUseSubscribers.add(sub);
	}

	// unsubscribeFromOnprePotionUse
	@Deprecated
	public static void unsubscribeFromPrePotionUse(PrePotionUseSubscriber sub) {
		prePotionUseSubscribers.remove(sub);
	}

	// subscribeToPotionGet
	@Deprecated
	public static void subscribeToPotionGet(PotionGetSubscriber sub) {
		potionGetSubscribers.add(sub);
	}

	// unsubscribeToPotionGet
	@Deprecated
	public static void unsubscribeFromPotionGet(PotionGetSubscriber sub) {
		potionGetSubscribers.remove(sub);
	}

	// subscribeToRelicGet
	@Deprecated
	public static void subscribeToRelicGet(RelicGetSubscriber sub) {
		relicGetSubscribers.add(sub);
	}

	// unsubscribeToRelicGet
	@Deprecated
	public static void unsubscribeFromRelicGet(RelicGetSubscriber sub) {
		relicGetSubscribers.remove(sub);
	}

	// subscribeToPostPowerApply
	@Deprecated
	public static void subscribeToPostPowerApply(PostPowerApplySubscriber sub) {
		postPowerApplySubscribers.add(sub);
	}

	// unsubscribeToPostPowerApply
	@Deprecated
	public static void unsubscribeToPostPowerApply(PostPowerApplySubscriber sub) {
		postPowerApplySubscribers.remove(sub);
	}

	// subscribeToEditKeywords
	@Deprecated
	public static void subscribeToEditKeywords(EditKeywordsSubscriber sub) {
		editKeywordsSubscribers.add(sub);
	}

	// unsubscribeFromEditKeywords
	@Deprecated
	public static void unsubscribeFromEditKeywords(EditKeywordsSubscriber sub) {
		editKeywordsSubscribers.remove(sub);
	}

	// subscribeToOnPowersModified
	@Deprecated
	public static void subscribeToOnPowersModified(OnPowersModifiedSubscriber sub) {
		onPowersModifiedSubscribers.add(sub);
	}

	// unsubscribeFromOnPowersModified
	@Deprecated
	public static void unsubscribeFromOnPowersModified(OnPowersModifiedSubscriber sub) {
		onPowersModifiedSubscribers.remove(sub);
	}
}