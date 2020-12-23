package basemod;

import basemod.abstracts.*;
import basemod.eventUtil.AddEventParams;
import basemod.eventUtil.EventUtils;
import basemod.helpers.RelicType;
import basemod.helpers.dynamicvariables.BlockVariable;
import basemod.helpers.dynamicvariables.DamageVariable;
import basemod.helpers.dynamicvariables.MagicNumberVariable;
import basemod.interfaces.*;
import basemod.patches.com.megacrit.cardcrawl.helpers.TopPanel.TopPanelHelper;
import basemod.patches.com.megacrit.cardcrawl.screens.select.GridCardSelectScreen.GridCardSelectScreenFields;
import basemod.patches.com.megacrit.cardcrawl.unlock.UnlockTracker.CountModdedUnlockCards;
import basemod.patches.whatmod.WhatMod;
import basemod.screens.ModalChoiceScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Version;
import com.badlogic.gdx.files.FileHandle;
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
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.megacrit.cardcrawl.audio.Sfx;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.RedCards;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardSave;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import com.megacrit.cardcrawl.screens.custom.CustomModeCharacterButton;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;
import com.megacrit.cardcrawl.unlock.AbstractUnlock;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.classutil.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SpireInitializer
public class BaseMod {
	public static final Logger logger = LogManager.getLogger(BaseMod.class.getName());

	private static final int BADGES_PER_ROW = 16;
	private static final float BADGES_X = 640.0f;
	private static final float BADGES_Y = 250.0f;
	public static final float BADGE_W = 40.0f;
	public static final float BADGE_H = 40.0f;

	public static final int DEFAULT_MAX_HAND_SIZE = 10;
	@SuppressWarnings("unused")
	public static int MAX_HAND_SIZE = DEFAULT_MAX_HAND_SIZE;

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
	private static ArrayList<PostDungeonUpdateSubscriber> postDungeonUpdateSubscribers;
	private static ArrayList<PreDungeonUpdateSubscriber> preDungeonUpdateSubscribers;
	private static ArrayList<PostPlayerUpdateSubscriber> postPlayerUpdateSubscribers;
	private static ArrayList<PrePlayerUpdateSubscriber> prePlayerUpdateSubscribers;
	private static ArrayList<PostCreateStartingDeckSubscriber> postCreateStartingDeckSubscribers;
	private static ArrayList<PostCreateStartingRelicsSubscriber> postCreateStartingRelicsSubscribers;
	private static ArrayList<PostCreateShopRelicSubscriber> postCreateShopRelicSubscribers;
	private static ArrayList<PostCreateShopPotionSubscriber> postCreateShopPotionSubscribers;
	private static ArrayList<EditCardsSubscriber> editCardsSubscribers;
	private static ArrayList<EditRelicsSubscriber> editRelicsSubscribers;
	private static ArrayList<EditCharactersSubscriber> editCharactersSubscribers;
	private static ArrayList<EditStringsSubscriber> editStringsSubscribers;
	private static ArrayList<AddAudioSubscriber> addAudioSubscribers;
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
	private static ArrayList<AddCustomModeModsSubscriber> addCustomModeModsSubscribers;
	private static ArrayList<MaxHPChangeSubscriber> maxHPChangeSubscribers;
	private static ArrayList<PreRoomRenderSubscriber> preRoomRenderSubscribers;
	private static ArrayList<OnPlayerLoseBlockSubscriber> onPlayerLoseBlockSubscribers;
	private static ArrayList<OnPlayerDamagedSubscriber> onPlayerDamagedSubscribers;
	private static ArrayList<OnCreateDescriptionSubscriber> onCreateDescriptionSubscribers;

	private static ArrayList<AbstractCard> redToAdd;
	private static ArrayList<String> redToRemove;
	private static ArrayList<AbstractCard> greenToAdd;
	private static ArrayList<String> greenToRemove;
	private static ArrayList<AbstractCard> blueToAdd;
	private static ArrayList<String> blueToRemove;
	private static ArrayList<AbstractCard> purpleToAdd;
	private static ArrayList<String> purpleToRemove;
	private static ArrayList<AbstractCard> colorlessToAdd;
	private static ArrayList<String> colorlessToRemove;
	private static ArrayList<AbstractCard> curseToAdd;
	private static ArrayList<String> curseToRemove;
	private static ArrayList<AbstractCard> customToAdd;
	private static ArrayList<String> customToRemove;
	private static ArrayList<AbstractCard.CardColor> customToRemoveColors;

	private static HashMap<AbstractCard.CardColor, HashMap<String, AbstractRelic>> customRelicPools;
	private static HashMap<AbstractCard.CardColor, ArrayList<AbstractRelic>> customRelicLists;
	private static HashMap<String, Pair<Predicate<AbstractCard>, AbstractRelic>> customBottleRelics;

	private static ArrayList<String> potionsToRemove;

	private static int lastBaseCharacterIndex = -1;

	public static HashMap<PlayerClass, String> playerSelectButtonMap;
	public static HashMap<PlayerClass, String> customModeCharacterButtonMap;
	public static HashMap<PlayerClass, String> playerPortraitMap;

	public static HashMap<String, DynamicVariable> cardDynamicVariableMap = new HashMap<>();

	private static HashMap<String, Class<? extends AbstractPotion>> potionClassMap;
	private static HashMap<String, Color> potionHybridColorMap;
	private static HashMap<String, Color> potionLiquidColorMap;
	private static HashMap<String, Color> potionSpotsColorMap;
	private static HashMap<String, AbstractPlayer.PlayerClass> potionPlayerClassMap;

	private static HashMap<String, Class<? extends AbstractPower>> powerMap;

	private static HashMap<String, Sfx> audioToAdd;

	private static HashMap<String, String> keywordProperNames;
	private static HashMap<String, String> keywordUniqueNames;
	private static HashMap<String, String> keywordUniquePrefixes;

	public static ArrayList<String> encounterList;
	public static HashMap<String, String> underScoreEncounterIDs;
	public static HashMap<String, String> underScoreCardIDs;
	public static HashMap<String, String> underScorePotionIDs;
	public static HashMap<String, String> underScorePowerIDs;
	public static HashMap<String, String> underScoreEventIDs;
	public static HashMap<String, String> underScoreRelicIDs;

	private static HashMap<AbstractCard.CardColor, com.badlogic.gdx.graphics.Color> colorBgColorMap;
	private static HashMap<AbstractCard.CardColor, com.badlogic.gdx.graphics.Color> colorBackColorMap;
	private static HashMap<AbstractCard.CardColor, com.badlogic.gdx.graphics.Color> colorFrameColorMap;
	private static HashMap<AbstractCard.CardColor, com.badlogic.gdx.graphics.Color> colorFrameOutlineColorMap;
	private static HashMap<AbstractCard.CardColor, com.badlogic.gdx.graphics.Color> colorDescBoxColorMap;
	private static HashMap<AbstractCard.CardColor, com.badlogic.gdx.graphics.Color> colorTrailVfxMap;
	private static HashMap<AbstractCard.CardColor, com.badlogic.gdx.graphics.Color> colorGlowColorMap;
	private static HashMap<AbstractCard.CardColor, Integer> colorCardCountMap;
	private static HashMap<AbstractCard.CardColor, Integer> colorCardSeenCountMap;
	private static HashMap<AbstractCard.CardColor, String> colorAttackBgMap;
	private static HashMap<AbstractCard.CardColor, String> colorSkillBgMap;
	private static HashMap<AbstractCard.CardColor, String> colorPowerBgMap;
	private static HashMap<AbstractCard.CardColor, String> colorEnergyOrbMap;
	private static HashMap<AbstractCard.CardColor, String> colorCardEnergyOrbMap;
	private static HashMap<AbstractCard.CardColor, String> colorAttackBgPortraitMap;
	private static HashMap<AbstractCard.CardColor, String> colorSkillBgPortraitMap;
	private static HashMap<AbstractCard.CardColor, String> colorPowerBgPortraitMap;
	private static HashMap<AbstractCard.CardColor, String> colorEnergyOrbPortraitMap;
	private static HashMap<AbstractCard.CardColor, com.badlogic.gdx.graphics.Texture> colorAttackBgTextureMap;
	private static HashMap<AbstractCard.CardColor, com.badlogic.gdx.graphics.Texture> colorSkillBgTextureMap;
	private static HashMap<AbstractCard.CardColor, com.badlogic.gdx.graphics.Texture> colorPowerBgTextureMap;
	private static HashMap<AbstractCard.CardColor, com.badlogic.gdx.graphics.Texture> colorEnergyOrbTextureMap;
	private static HashMap<AbstractCard.CardColor, com.badlogic.gdx.graphics.Texture> colorAttackBgPortraitTextureMap;
	private static HashMap<AbstractCard.CardColor, com.badlogic.gdx.graphics.Texture> colorSkillBgPortraitTextureMap;
	private static HashMap<AbstractCard.CardColor, com.badlogic.gdx.graphics.Texture> colorPowerBgPortraitTextureMap;
	private static HashMap<AbstractCard.CardColor, com.badlogic.gdx.graphics.Texture> colorEnergyOrbPortraitTextureMap;
	private static HashMap<AbstractCard.CardColor, TextureAtlas.AtlasRegion> colorCardEnergyOrbAtlasRegionMap;

	private static HashMap<AbstractPlayer.PlayerClass, HashMap<Integer, CustomUnlockBundle>> unlockBundles;
	private static HashMap<AbstractPlayer.PlayerClass, ArrayList<String>> unlockCards;
	private static HashMap<AbstractPlayer.PlayerClass, Integer> maxUnlockLevel;

	private static HashMap<String, CustomSavableRaw> customSaveFields = new HashMap<>();

	private static OrthographicCamera animationCamera;
	private static ModelBatch batch;
	private static Environment animationEnvironment;
	private static FrameBuffer animationBuffer;
	private static Texture animationTexture;
	private static TextureRegion animationTextureRegion;

	public static final String CONFIG_FILE = "basemod-config";
	private static SpireConfig config;

	public static final String save_path = "saves" + File.separator;

	public static DevConsole console;
	public static Gson gson;
	public static boolean modSettingsUp = false;

	// Map generation
	public static float mapPathDensityMultiplier = 1.0f;

	public static ModalChoiceScreen modalChoiceScreen = new ModalChoiceScreen();

	//
	// Initialization
	//

	private static SpireConfig makeConfig() {
		Properties defaultProperties = new Properties();
		defaultProperties.setProperty("console-key", "`");
		defaultProperties.setProperty("autocomplete-enabled", Boolean.toString(true));
		defaultProperties.setProperty("whatmod-enabled", Boolean.toString(true));

		try {
			SpireConfig retConfig = new SpireConfig(BaseModInit.MODNAME, CONFIG_FILE, defaultProperties);
			return retConfig;
		} catch (IOException e) {
			return null;
		}
	}

	private static String getString(String key) {
		return config.getString(key);
	}

	static void setString(String key, String value) {
		config.setString(key, value);
		try {
			config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Boolean getBoolean(String key) {
		return config.getBool(key);
	}

	static void setBoolean(String key, Boolean value) {
		config.setBool(key, value);
		try {
			config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void setProperties() {
		// if config can't be loaded leave things at defaults
		if (config == null) {
			return;
		}

		String consoleKey = getString("console-key");
		if (consoleKey != null) {
			DevConsole.toggleKey = Keys.valueOf(consoleKey);
		}
		Boolean consoleEnabled = getBoolean("console-enabled");
		if (consoleEnabled != null) {
			DevConsole.enabled = consoleEnabled;
		}

		Boolean autoCompleteEnabled = getBoolean("autocomplete-enabled");
		if (autoCompleteEnabled != null) {
			AutoComplete.enabled = autoCompleteEnabled;
		}

		Boolean whatmodEnabled = getBoolean("whatmod-enabled");
		if (whatmodEnabled != null) {
			WhatMod.enabled = whatmodEnabled;
		}
	}

	public static boolean isBaseGameCharacter(AbstractPlayer c) {
		return isBaseGameCharacter(c.chosenClass);
	}

	public static boolean isBaseGameCharacter(AbstractPlayer.PlayerClass chosenClass) {
		int index = -1;
		for (AbstractPlayer player : CardCrawlGame.characterManager.getAllCharacters()) {
			++index;
			if (player.chosenClass == chosenClass) {
				break;
			}
		}
		return index <= lastBaseCharacterIndex;
	}

	public static boolean isBaseGameCardColor(AbstractCard.CardColor color) {
		return color.compareTo(AbstractCard.CardColor.CURSE) <= 0;
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

		audioToAdd = new HashMap<>();

		keywordProperNames = new HashMap<>();
		keywordUniqueNames = new HashMap<>();
		keywordUniquePrefixes = new HashMap<>();
		BaseModInit baseModInit = new BaseModInit();
		BaseMod.subscribe(baseModInit);

		config = makeConfig();
		setProperties();
		console = new DevConsole();
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
		typeTokens = new HashMap<>();

		for (Field f : LocalizedStrings.class.getDeclaredFields()) {
			Type type = f.getGenericType();
			if (type instanceof ParameterizedType) {
				ParameterizedType pType = (ParameterizedType) type;
				Type[] typeArgs = pType.getActualTypeArguments();
				if (typeArgs.length == 2
						&& typeArgs[0].equals(String.class)
						&& typeArgs[1].getTypeName().startsWith("com.megacrit.cardcrawl.localization.")
						&& typeArgs[1].getTypeName().endsWith("Strings")) {

					logger.info("Registered " + typeArgs[1].getTypeName().replace("com.megacrit.cardcrawl.localization.", ""));
					typeMaps.put(typeArgs[1], f.getName());
					ParameterizedType p = com.google.gson.internal.$Gson$Types.newParameterizedTypeWithOwner(null, Map.class, String.class, typeArgs[1]);
					typeTokens.put(typeArgs[1], p);
				}
			}
		}
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
		postDungeonUpdateSubscribers = new ArrayList<>();
		preDungeonUpdateSubscribers = new ArrayList<>();
		postPlayerUpdateSubscribers = new ArrayList<>();
		prePlayerUpdateSubscribers = new ArrayList<>();
		postCreateStartingDeckSubscribers = new ArrayList<>();
		postCreateStartingRelicsSubscribers = new ArrayList<>();
		postCreateShopRelicSubscribers = new ArrayList<>();
		postCreateShopPotionSubscribers = new ArrayList<>();
		editCardsSubscribers = new ArrayList<>();
		editRelicsSubscribers = new ArrayList<>();
		editCharactersSubscribers = new ArrayList<>();
		editStringsSubscribers = new ArrayList<>();
		addAudioSubscribers = new ArrayList<>();
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
		addCustomModeModsSubscribers = new ArrayList<>();
		maxHPChangeSubscribers = new ArrayList<>();
		preRoomRenderSubscribers = new ArrayList<>();
		onPlayerLoseBlockSubscribers = new ArrayList<>();
		onPlayerDamagedSubscribers = new ArrayList<>();
		onCreateDescriptionSubscribers = new ArrayList<>();
	}

	// initializeCardLists -
	private static void initializeCardLists() {
		redToAdd = new ArrayList<>();
		redToRemove = new ArrayList<>();
		greenToAdd = new ArrayList<>();
		greenToRemove = new ArrayList<>();
		blueToAdd = new ArrayList<>();
		blueToRemove = new ArrayList<>();
		purpleToAdd = new ArrayList<>();
		purpleToRemove = new ArrayList<>();
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
		playerSelectButtonMap = new HashMap<>();
		customModeCharacterButtonMap = new HashMap<>();
		playerPortraitMap = new HashMap<>();
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
		colorCardEnergyOrbMap = new HashMap<>();
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
		colorCardEnergyOrbAtlasRegionMap = new HashMap<>();
	}

	private static void initializeRelicPool() {
		customRelicPools = new HashMap<>();
		customRelicLists = new HashMap<>();
		customBottleRelics = new HashMap<>();
	}

	// initializeUnlocks
	private static void initializeUnlocks() {
		unlockBundles = new HashMap<>();
		unlockCards = new HashMap<>();
		maxUnlockLevel = new HashMap<>();
	}

	private static void initializePotionMap() {
		potionClassMap = new HashMap<>();
		potionHybridColorMap = new HashMap<>();
		potionLiquidColorMap = new HashMap<>();
		potionSpotsColorMap = new HashMap<>();
		potionPlayerClassMap = new HashMap<>();
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

	// Finds events that have IDs with spaces in them and maps those IDs with
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

	// Add Sfx in audio map to SoundMaster
	private static void addAudioToSoundMaster(SoundMaster __instance) {
		@SuppressWarnings("unchecked")
		HashMap<String, Sfx> map = (HashMap<String, Sfx>) ReflectionHacks.getPrivate(__instance, SoundMaster.class, "map");

		if (map != null) {
			map.putAll(audioToAdd);
			logger.info("Added " + audioToAdd.size() + " sounds");
			audioToAdd.clear();
		} else {
			logger.warn("Unexpectedly failed to add sounds.");
		}


		ReflectionHacks.setPrivate(__instance, SoundMaster.class, "map", map);
	}

	static void initializeEncounters() {
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
		try {
			ClassPool pool = Loader.getClassPool();
			CtClass ctCls = pool.get(AbstractPower.class.getName());
			String url = ctCls.getURL().getFile();
			int i = url.lastIndexOf('!');
			url = url.substring(0, i);
			URL locationURL = new URL(url);
			finder.add(new File(locationURL.toURI()));

			ClassFilter filter = new AndClassFilter(
					new NotClassFilter(new InterfaceOnlyClassFilter()),
					new NotClassFilter(new AbstractClassFilter()),
					new RegexClassFilter("com\\.megacrit\\.cardcrawl\\.powers\\..+")
			);
			Collection<ClassInfo> foundClasses = new ArrayList<>();
			finder.findClasses(foundClasses, filter);

			for (ClassInfo classInfo : foundClasses) {
				if (classInfo.getClassName().contains("$")) {
					continue;
				}
				try {
					if (!CloneablePowerInterface.class.isAssignableFrom(BaseMod.class.getClassLoader().loadClass(classInfo.getClassName()))) {
						logger.warn(String.format("Power (%s) isn't Cloneable", classInfo.getClassName()));
					}
					for (FieldInfo fieldInfo : classInfo.getFields()) {
						if (fieldInfo.getName().equals("POWER_ID") && fieldInfo.getValue() instanceof String) {
							powerMap.put((String) fieldInfo.getValue(),
									(Class<? extends AbstractPower>) BaseMod.class.getClassLoader().loadClass(classInfo.getClassName()));
							break;
						}
					}
				} catch (ClassNotFoundException e) {
					System.out.println("ERROR: Failed to load power class: " + classInfo.getClassName());
				}
			}
		} catch (URISyntaxException | MalformedURLException | NotFoundException e) {
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

	public static String colorString(String input, String colorValue) {
		StringBuilder retVal = new StringBuilder();
		Scanner s = new Scanner(input);
		while (s.hasNext()) {
			retVal.append("[").append(colorValue).append("]").append(s.next());
			retVal.append(" ");
		}
		s.close();
		return retVal.toString().trim();
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
	public static void loadCustomStrings(Class<?> stringType, String jsonString) {
		loadJsonStrings(stringType, jsonString);
	}

	public static void loadCustomStringsFile(Class<?> stringType, String filepath) {
		loadJsonStrings(stringType, Gdx.files.internal(filepath).readString(String.valueOf(StandardCharsets.UTF_8)));
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

	// blue add -
	public static ArrayList<AbstractCard> getBlueCardsToAdd() {
		return blueToAdd;
	}

	// blue remove -
	public static ArrayList<String> getBlueCardsToRemove() {
		return blueToRemove;
	}

	// purple add -
	public static ArrayList<AbstractCard> getPurpleCardsToAdd() {
		return purpleToAdd;
	}

	// purple remove -
	public static ArrayList<String> getPurpleCardsToRemove() {
		return purpleToRemove;
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
	public static ArrayList<AbstractCard.CardColor> getCustomCardsToRemoveColors() {
		return customToRemoveColors;
	}

	// add audio to add
	public static void addAudio(String audioKey, String file)
	{
		FileHandle sfxFile = Gdx.files.internal(file); // Ensure audio is valid file

		if (sfxFile != null && sfxFile.exists()) {
			Sfx audioSfx = new Sfx(file, false);
			audioToAdd.put(audioKey, audioSfx);
		} else {
			logger.warn("Audio file: " + file + " was not found.");
		}
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
			case BLUE:
				blueToAdd.add(card);
				break;
			case PURPLE:
				purpleToAdd.add(card);
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
	public static void removeCard(String card, AbstractCard.CardColor color) {
		switch (color) {
			case RED:
				redToRemove.add(card);
				break;
			case GREEN:
				greenToRemove.add(card);
				break;
			case BLUE:
				blueToRemove.add(card);
				break;
			case PURPLE:
				purpleToRemove.add(card);
			case COLORLESS:
				colorlessToRemove.add(card);
				break;
			case CURSE:
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
			case BLUE:
				RelicLibrary.addBlue(relic);
				break;
			case PURPLE:
				RelicLibrary.addPurple(relic);
				break;
			default:
				logger.info("tried to add relic of unsupported type: " + relic + " " + type);
				return;
		}

		if (relic instanceof CustomBottleRelic) {
			registerBottleRelic(((CustomBottleRelic) relic).isOnCard(), relic);
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
			case BLUE:
				HashMap<String, AbstractRelic> blueRelics = (HashMap<String, AbstractRelic>) ReflectionHacks
						.getPrivateStatic(RelicLibrary.class, "blueRelics");
				if (blueRelics.containsKey(relic.relicId)) {
					blueRelics.remove(relic.relicId);
					RelicLibrary.totalRelicCount--;
					removeRelicFromTierList(relic);
				}
				break;
			case PURPLE:
				HashMap<String, AbstractRelic> purpleRelics = (HashMap<String, AbstractRelic>) ReflectionHacks
						.getPrivateStatic(RelicLibrary.class, "purpleRelics");
				if (purpleRelics.containsKey(relic.relicId)) {
					purpleRelics.remove(relic.relicId);
					RelicLibrary.totalRelicCount--;
					removeRelicFromTierList(relic);
				}
				break;
			default:
				logger.info("tried to remove relic of unsupported type: " + relic + " " + type);
		}
	}

	// remove custom relic -
	@SuppressWarnings("unchecked")
	public static void removeRelicFromCustomPool(AbstractRelic relic, AbstractCard.CardColor color) {
		if (relic == null) return;
		if (customRelicPools.containsKey(color)){
			if (customRelicPools.get(color).containsKey(relic.relicId)) {
				customRelicPools.get(color).remove(relic.relicId);
				--RelicLibrary.totalRelicCount;
				removeRelicFromTierList(relic);
			}
		}
		if (customRelicLists.containsKey(color)){
			if (customRelicLists.get(color).contains(relic)){
				customRelicLists.get(color).remove(relic);
			}
		}
	}

	public static void registerBottleRelic(Predicate<AbstractCard> isOnCard, AbstractRelic relic)
	{
		customBottleRelics.put(relic.relicId, new Pair<>(isOnCard, relic));
	}

	public static void registerBottleRelic(SpireField<Boolean> isOnCard, AbstractRelic relic)
	{
		customBottleRelics.put(relic.relicId, new Pair<>(isOnCard::get, relic));
	}

	// addRelicToCustomPool -
	public static void addRelicToCustomPool(AbstractRelic relic, AbstractCard.CardColor color) {
		if (customRelicPools.containsKey(color)) {
			if (UnlockTracker.isRelicSeen(relic.relicId)) {
				RelicLibrary.seenRelics++;
			}
			relic.isSeen = UnlockTracker.isRelicSeen(relic.relicId);
			customRelicPools.get(color).put(relic.relicId, relic);
			RelicLibrary.addToTierList(relic);
			customRelicLists.get(color).add(relic);

			if (relic instanceof CustomBottleRelic) {
				registerBottleRelic(((CustomBottleRelic) relic).isOnCard(), relic);
			}
		} else {
			logger.error("could not add relic to non existent custom pool: " + color);
		}
	}

	// getRelicsInCustomPool -
	public static HashMap<String, AbstractRelic> getRelicsInCustomPool(AbstractCard.CardColor color) {
		return customRelicPools.get(color);
	}

	// getAllCustomRelics -
	public static HashMap<AbstractCard.CardColor, HashMap<String, AbstractRelic>> getAllCustomRelics() {
		return customRelicPools;
	}

	// getCustomRelic -
	public static AbstractRelic getCustomRelic(String key) {
		for (HashMap<String, AbstractRelic> map : BaseMod.getAllCustomRelics().values()) {
			if (map.containsKey(key)) {
				return map.get(key);
			}
		}
		return new Circlet();
	}

	public static Collection<Pair<Predicate<AbstractCard>, AbstractRelic>> getBottledRelicList()
	{
		return customBottleRelics.values();
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
		removeRelic(relic, RelicType.BLUE);
		removeRelic(relic, RelicType.PURPLE);
	}

	// lists the IDs of all Relics from all pools. The casts are actually not
	// unchecked
	@SuppressWarnings("unchecked")
	public static ArrayList<String> listAllRelicIDs() {
		// Set to prevent duplicates
		HashSet<String> relicIDs = new HashSet<>();

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
		HashMap<String, AbstractRelic> purpleRelics = (HashMap<String, AbstractRelic>) ReflectionHacks
				.getPrivateStatic(RelicLibrary.class, "purpleRelics");
		if (purpleRelics != null) {
			relicIDs.addAll(purpleRelics.keySet());
		}
		if (getAllCustomRelics() != null) {
			for (HashMap<String, AbstractRelic> e : getAllCustomRelics().values()) {
				if (e != null) {
					relicIDs.addAll(e.keySet());
				}
			}
		}
		// But return ArrayList to maintain backwards compatibility
		return new ArrayList<>(relicIDs);
	}


	private static HashMap<RewardItem.RewardType, LoadCustomReward> customRewardOnLoadConsumers = new HashMap<>();
	private static HashMap<RewardItem.RewardType, SaveCustomReward> customRewardOnSaveConsumers = new HashMap<>();

	public static void registerCustomReward(CustomReward.RewardType type, LoadCustomReward onLoad, SaveCustomReward onSave) {
		customRewardOnLoadConsumers.put(type, onLoad);
		customRewardOnSaveConsumers.put(type, onSave);
	}

	public static CustomReward loadCustomRewardFromSave(RewardSave rewardSave) {
		return customRewardOnLoadConsumers.get(RewardItem.RewardType.valueOf(rewardSave.type)).onLoad(rewardSave);
	}

	public static RewardSave saveCustomReward(CustomReward reward) {
		return customRewardOnSaveConsumers.get(reward.type).onSave(reward);
	}

	public static boolean customRewardTypeExists(RewardItem.RewardType type){
		return customRewardOnSaveConsumers.containsKey(type) && customRewardOnLoadConsumers.containsKey(type);
	}


	public interface LoadCustomReward {
		CustomReward onLoad(RewardSave rewardSave);
	}

	public interface SaveCustomReward {
		RewardSave onSave(CustomReward reward);
	}

	//
	// Events
	//

	// Additional documentation can be found in EventUtils.

	public static void addEvent(String eventID, Class<? extends AbstractEvent> eventClass) {
		addEvent(new AddEventParams.Builder(eventID, eventClass).create());
	}

	public static void addEvent(String eventID, Class<? extends AbstractEvent> eventClass, String dungeonID) {
		addEvent(
				new AddEventParams.Builder(eventID, eventClass)
						.dungeonID(dungeonID)
						.create()
		);
	}

	public static void addEvent(AddEventParams params) {
		EventUtils.registerEvent(
				params.eventID,
				params.eventClass,
				params.playerClass,
				params.dungeonIDs.toArray(new String[0]),
				params.spawnCondition,
				params.overrideEventID,
				params.bonusCondition,
				params.eventType
		);
	}

	//implemented to avoid issues if someone uses them.
	@Deprecated //Labeled as deprecated because changes here will have no impact on what events appear.
	public static HashMap<String, Class<? extends AbstractEvent>> getEventList(String dungeonID) {
		return EventUtils.getDungeonEvents(dungeonID);
	}

	public static Class<? extends AbstractEvent> getEvent(String eventID) {
		return EventUtils.getEventClass(eventID);
	}

	//
	// Top Panel
	//

	public static void addTopPanelItem(TopPanelItem topPanelItem) {
		TopPanelHelper.topPanelGroup.addPanelItem(topPanelItem);
	}

	public static void removeTopPanelItem(TopPanelItem topPanelItem){
		TopPanelHelper.topPanelGroup.removePanelItem(topPanelItem);
	}

	//
	// Monsters
	//

	// Key: Encounter ID
	// Value: Encounter nice name
	private static HashMap<String, String> customMonsterNames = new HashMap<>();
	// Key: Encounter ID
	private static HashMap<String, GetMonsterGroup> customMonsters = new HashMap<>();
	// Key: Dungeon ID
	// Value: Encounter ID
	private static HashMap<String, List<MonsterInfo>> customMonsterEncounters = new HashMap<>();
	// Key: Dungeon ID
	// Value: Encounter ID
	private static HashMap<String, List<MonsterInfo>> customStrongMonsterEncounters = new HashMap<>();
	// Key: Dungeon ID
	// Value: Encounter ID
	private static HashMap<String, List<MonsterInfo>> customEliteEncounters = new HashMap<>();

	public interface GetMonsterGroup {
		MonsterGroup get();
	}

	public interface GetMonster {
		AbstractMonster get();
	}

	private static String autoCalculateMonsterName(GetMonsterGroup group)
	{
		StringBuilder ret = new StringBuilder();

		if (AbstractDungeon.monsterRng == null) {
			Settings.seed = 0L;
			AbstractDungeon.generateSeeds();
		}

		MonsterGroup monsters = group.get();
		boolean first = true;
		for (AbstractMonster monster : monsters.monsters) {
			if (!first) {
				ret.append(", ");
			}
			first = false;
			ret.append(monster.name);
		}

		return ret.toString();
	}

	public static void addMonster(String encounterID, GetMonster monster) {
		addMonster(encounterID, () -> new MonsterGroup(monster.get()));
	}

	public static void addMonster(String encounterID, String name, GetMonster monster) {
		addMonster(encounterID, name, () -> new MonsterGroup(monster.get()));
	}

	public static void addMonster(String encounterID, GetMonsterGroup group) {
		addMonster(encounterID, autoCalculateMonsterName(group), group);
	}

	public static void addMonster(String encounterID, String name, GetMonsterGroup group) {
		customMonsters.put(encounterID, group);
		customMonsterNames.put(encounterID, name);
		encounterList.add(encounterID);
		if (encounterID.contains(" ")) {
			underScoreEncounterIDs.put(encounterID.replace(' ', '_'), encounterID);
		}
	}

	public static MonsterGroup getMonster(String encounterID) {
		GetMonsterGroup getter = customMonsters.get(encounterID);
		if (getter == null) {
			return null;
		}
		return getter.get();
	}

	public static String getMonsterName(String encounterID) {
		return customMonsterNames.getOrDefault(encounterID, "");
	}

	public static boolean customMonsterExists(String encounterID) {
		return customMonsters.containsKey(encounterID);
	}

	public static void addEliteEncounter(String dungeonID, MonsterInfo encounter) {
		if (!customEliteEncounters.containsKey(dungeonID)) {
			customEliteEncounters.put(dungeonID, new ArrayList<>());
		}
		customEliteEncounters.get(dungeonID).add(encounter);
	}

	public static void addStrongMonsterEncounter(String dungeonID, MonsterInfo encounter) {
		if (!customStrongMonsterEncounters.containsKey(dungeonID)) {
			customStrongMonsterEncounters.put(dungeonID, new ArrayList<>());
		}
		customStrongMonsterEncounters.get(dungeonID).add(encounter);
	}

	public static void addMonsterEncounter(String dungeonID, MonsterInfo encounter) {
		if (!customMonsterEncounters.containsKey(dungeonID)) {
			customMonsterEncounters.put(dungeonID, new ArrayList<>());
		}
		customMonsterEncounters.get(dungeonID).add(encounter);
	}

	public static List<MonsterInfo> getEliteEncounters(String dungeonID) {
		if (customEliteEncounters.containsKey(dungeonID)) {
			return customEliteEncounters.get(dungeonID);
		}
		return new ArrayList<>();
	}

	public static List<MonsterInfo> getStrongMonsterEncounters(String dungeonID) {
		if (customStrongMonsterEncounters.containsKey(dungeonID)) {
			return customStrongMonsterEncounters.get(dungeonID);
		}
		return new ArrayList<>();
	}

	public static List<MonsterInfo> getMonsterEncounters(String dungeonID) {
		if (customMonsterEncounters.containsKey(dungeonID)) {
			return customMonsterEncounters.get(dungeonID);
		}
		return new ArrayList<>();
	}

	//
	// Bosses
	//

	private static HashMap<String, List<BossInfo>> customBosses = new HashMap<>();

	public static class BossInfo {
		public final String id;
		private final String bossMap;
		private final String bossMapOutline;

		private BossInfo(String id, String mapIcon, String mapIconOutline) {
			this.id = id;
			bossMap = mapIcon;
			bossMapOutline = mapIconOutline;
		}

		public Texture loadBossMap() {
			return ImageMaster.loadImage(bossMap);
		}

		public Texture loadBossMapOutline() {
			return ImageMaster.loadImage(bossMapOutline);
		}
	}

	public static void addBoss(String dungeon, String bossID, String mapIcon, String mapIconOutline) {
		if (!customBosses.containsKey(dungeon)) {
			customBosses.put(dungeon, new ArrayList<>());
		}
		BossInfo info = new BossInfo(bossID, mapIcon, mapIconOutline);
		customBosses.get(dungeon).add(info);
	}

	public static List<String> getBossIDs(String dungeonID) {
		if (customBosses.containsKey(dungeonID)) {
			return customBosses.get(dungeonID).stream()
					.map(info -> info.id)
					.collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	public static BossInfo getBossInfo(String bossID) {
		if (bossID == null) {
			return null;
		}

		for (List<BossInfo> infos : customBosses.values()) {
			for (BossInfo info : infos) {
				if (bossID.equals(info.id)) {
					return info;
				}
			}
		}
		return null;
	}

	//
	// Keywords
	//

	public static void addKeyword(String[] names, String description) {
		addKeyword(null, names, description);
	}

	public static void addKeyword(String proper, String[] names, String description) {
		addKeyword(null, proper, names, description);
	}

	public static void addKeyword(String modID, String proper, String[] names, String description) {
		if (modID != null && !modID.isEmpty()) {
			if (!modID.endsWith(":")) {
				modID = modID + ":";
			}
			String uniqueParent = names[0];
			for (int i=0; i<names.length; ++i) {
				names[i] = modID + names[i];
			}
			for (String name : names) {
				keywordUniqueNames.put(name, uniqueParent);
				keywordUniquePrefixes.put(name, modID);
			}
		}
		String parent = names[0];

		if (proper != null) {
			keywordProperNames.put(parent, proper);
		}

		for (String name : names) {
			GameDictionary.keywords.put(name, description);
			GameDictionary.parentWord.put(name, parent);
		}
	}

	public static String getKeywordProper(String keyword)
	{
		return keywordProperNames.get(keyword);
	}

	public static String getKeywordUnique(String keyword) {
		return keywordUniqueNames.get(keyword);
	}

	public static boolean keywordIsUnique(String keyword) {
		return keywordUniqueNames.containsKey(keyword);
	}

	public static String getKeywordPrefix(String keyword) {
		return keywordUniquePrefixes.get(keyword);
	}

	public static String getKeywordTitle(String keyword) {
		keyword = GameDictionary.parentWord.get(keyword);
		String title = getKeywordProper(keyword);
		if (title != null) {
			return title;
		}
		return TipHelper.capitalize(keyword);
	}

	public static String getKeywordDescription(String keyword) {
		keyword = GameDictionary.parentWord.get(keyword);
		return GameDictionary.keywords.get(keyword);
	}

	//
	// Unlocks
	//

	// add new unlock bundle
	public static void addUnlockBundle(CustomUnlockBundle bundle, AbstractPlayer.PlayerClass c, int unlockLevel) {
		if (bundle == null) {
			return;
		}

		if (!unlockBundles.containsKey(c)) {
			HashMap<Integer, CustomUnlockBundle> newBundles = new HashMap<>();
			newBundles.put(unlockLevel, bundle);
			unlockBundles.put(c, newBundles);
		} else {
			HashMap<Integer, CustomUnlockBundle> bundles = unlockBundles.get(c);
			bundles.put(unlockLevel, bundle);
		}

		if (bundle.unlockType == AbstractUnlock.UnlockType.CARD)
		{
			if (!unlockCards.containsKey(c))
				unlockCards.put(c, new ArrayList<>());

			for (String s : bundle.getUnlockIDs())
			{
				if (!unlockCards.get(c).contains(s))
					unlockCards.get(c).add(s);
			}
		}

		if (maxUnlockLevel.containsKey(c)) {
			maxUnlockLevel.put(c, Math.max(maxUnlockLevel.get(c), unlockLevel));
		}
		else {
			maxUnlockLevel.put(c, unlockLevel);
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

	// get list of unlocks (cards)
	public static ArrayList<String> getUnlockCards(AbstractPlayer.PlayerClass c)
	{
		return unlockCards.get(c);
	}

	// get the number of unlock levels for class
	public static int getMaxUnlockLevel(AbstractPlayer p)
	{
		return getMaxUnlockLevel(p.chosenClass);
	}

	public static int getMaxUnlockLevel(AbstractPlayer.PlayerClass c)
	{
		if (maxUnlockLevel.containsKey(c)) {
			return maxUnlockLevel.get(c);
		}
		return 0;
	}

	//
	// Characters
	//

	public static AbstractPlayer findCharacter(AbstractPlayer.PlayerClass playerClass) {
		for (AbstractPlayer character : CardCrawlGame.characterManager.getAllCharacters()) {
			if (character.chosenClass == playerClass) {
				return character;
			}
		}
		return null;
	}

	public static List<AbstractPlayer> getModdedCharacters() {
		return CardCrawlGame.characterManager.getAllCharacters().subList(lastBaseCharacterIndex+1, CardCrawlGame.characterManager.getAllCharacters().size());
	}

	// add character - the String characterID *must* be the exact same as what
	// you put in the PlayerClass enum
	public static void addCharacter(AbstractPlayer character,
									String selectButtonPath,
									String portraitPath,
									PlayerClass characterID,
									String customModeButtonPath) {
		CardCrawlGame.characterManager.getAllCharacters().add(character);

		playerSelectButtonMap.put(characterID, selectButtonPath);
		customModeCharacterButtonMap.put(characterID, customModeButtonPath);
		playerPortraitMap.put(characterID, portraitPath);
	}

	public static void addCharacter(AbstractPlayer character,
									String selectButtonPath,
									String portraitPath,
									PlayerClass characterID) {
		addCharacter(character, selectButtonPath, portraitPath, characterID, null);
	}

	public static TextureAtlas.AtlasRegion getCardSmallEnergy() {
		if (AbstractDungeon.player == null) {
			return AbstractCard.orb_red;
		}
		return AbstractDungeon.player.getOrb();
	}

	public static TextureAtlas.AtlasRegion getCardSmallEnergy(AbstractCard card) {
		switch (card.color) {
			case RED:
				return AbstractCard.orb_red;
			case GREEN:
				return AbstractCard.orb_green;
			case BLUE:
				return AbstractCard.orb_blue;
			case PURPLE:
				return AbstractCard.orb_purple;
			case COLORLESS:
				return getCardSmallEnergy(); // for colorless cards, use the player color
			default:
				return getCardEnergyOrbAtlasRegion(card.color);
		}
	}

	// convert a playerClass into the player select button
	public static String getPlayerButton(PlayerClass playerClass) {
		return playerSelectButtonMap.get(playerClass);
	}

	// convert a playerClass into the custom mode player select button
	public static String getCustomModePlayerButton(PlayerClass playerClass) {
		return customModeCharacterButtonMap.get(playerClass);
	}

	// convert a playerClass into the player portrait
	public static String getPlayerPortrait(PlayerClass playerClass) {
		return playerPortraitMap.get(playerClass);
	}

	// generate character options for CharacterSelectScreen based on added
	// players
	public static ArrayList<CharacterOption> generateCharacterOptions() {
		ArrayList<CharacterOption> options = new ArrayList<>();
		for (AbstractPlayer character : getModdedCharacters()) {
			CharacterOption option = new CharacterOption(
					character.getLocalizedCharacterName(),
					CardCrawlGame.characterManager.recreateCharacter(character.chosenClass),
					ImageMaster.loadImage(playerSelectButtonMap.get(character.chosenClass)),
					ImageMaster.loadImage(playerPortraitMap.get(character.chosenClass))
			);
			options.add(option);
		}
		// Sort alphabetically by character name
		options.sort(Comparator.comparing(o -> o.name));
		return options;
	}

	// generate character options for CustomModeScreen based on added players
	public static ArrayList<CustomModeCharacterButton> generateCustomCharacterOptions() {
		ArrayList<CustomModeCharacterButton> options = new ArrayList<>();
		for (AbstractPlayer character : getModdedCharacters()) {
			options.add(new CustomModeCharacterButton(CardCrawlGame.characterManager.setChosenCharacter(character.chosenClass), false));
		}
		return options;
	}

	//
	// Colors
	//

	// add a color -
	public static void addColor(AbstractCard.CardColor color, Color everythingColor,
								String attackBg, String skillBg, String powerBg, String energyOrb,
								String attackBgPortrait, String skillBgPortrait, String powerBgPortrait, String energyOrbPortrait,
								String cardEnergyOrb) {
		addColor(color, everythingColor, everythingColor, everythingColor, everythingColor, everythingColor, everythingColor, everythingColor, attackBg, skillBg, powerBg, energyOrb, attackBgPortrait, skillBgPortrait, powerBgPortrait, energyOrbPortrait, cardEnergyOrb);
	}
	public static void addColor(AbstractCard.CardColor color, com.badlogic.gdx.graphics.Color bgColor,
								com.badlogic.gdx.graphics.Color backColor, com.badlogic.gdx.graphics.Color frameColor,
								com.badlogic.gdx.graphics.Color frameOutlineColor, com.badlogic.gdx.graphics.Color descBoxColor,
								com.badlogic.gdx.graphics.Color trailVfxColor, com.badlogic.gdx.graphics.Color glowColor,
								String attackBg, String skillBg, String powerBg, String energyOrb,
								String attackBgPortrait, String skillBgPortrait, String powerBgPortrait, String energyOrbPortrait) {
		addColor(color, bgColor, backColor, frameColor, frameOutlineColor, descBoxColor, trailVfxColor, glowColor, attackBg, skillBg, powerBg, energyOrb, attackBgPortrait, skillBgPortrait, powerBgPortrait, energyOrbPortrait, null);
	}
	public static void addColor(AbstractCard.CardColor color, com.badlogic.gdx.graphics.Color bgColor,
								com.badlogic.gdx.graphics.Color backColor, com.badlogic.gdx.graphics.Color frameColor,
								com.badlogic.gdx.graphics.Color frameOutlineColor, com.badlogic.gdx.graphics.Color descBoxColor,
								com.badlogic.gdx.graphics.Color trailVfxColor, com.badlogic.gdx.graphics.Color glowColor,
								String attackBg, String skillBg, String powerBg, String energyOrb,
								String attackBgPortrait, String skillBgPortrait, String powerBgPortrait, String energyOrbPortrait,
								String cardEnergyOrb) {
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
		colorCardEnergyOrbMap.put(color, cardEnergyOrb);

		customRelicPools.put(color, new HashMap<>());
		customRelicLists.put(color, new ArrayList<>());
	}

	// remove a custom color -
	// removing existing colors not currently supported
	public static void removeColor(AbstractCard.CardColor color) {
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
		colorCardEnergyOrbMap.remove(color);
		colorAttackBgPortraitMap.remove(color);
		colorSkillBgPortraitMap.remove(color);
		colorPowerBgPortraitMap.remove(color);
		colorEnergyOrbPortraitMap.remove(color);

		customRelicPools.remove(color);
		customRelicLists.remove(color, new ArrayList<AbstractRelic>());
	}

	public static List<AbstractCard.CardColor> getCardColors() {
		return new ArrayList<>(colorTrailVfxMap.keySet());
	}

	// convert a color into a background color
	public static com.badlogic.gdx.graphics.Color getBgColor(AbstractCard.CardColor color) {
		return colorBgColorMap.get(color);
	}

	// convert a color into a back color
	public static com.badlogic.gdx.graphics.Color getBackColor(AbstractCard.CardColor color) {
		return colorBackColorMap.get(color);
	}

	// convert a color into a frame color
	public static com.badlogic.gdx.graphics.Color getFrameColor(AbstractCard.CardColor color) {
		return colorFrameColorMap.get(color);
	}

	// convert a color into a frame outline color
	public static com.badlogic.gdx.graphics.Color getFrameOutlineColor(AbstractCard.CardColor color) {
		return colorFrameOutlineColorMap.get(color);
	}

	// convert a color into a desc box color
	public static com.badlogic.gdx.graphics.Color getDescBoxColor(AbstractCard.CardColor color) {
		return colorDescBoxColorMap.get(color);
	}

	// convert a color into a trail vfx color
	public static com.badlogic.gdx.graphics.Color getTrailVfxColor(AbstractCard.CardColor color) {
		return colorTrailVfxMap.get(color);
	}

	// increment the card count for a color
	public static void incrementCardCount(AbstractCard.CardColor color) {
		Integer count = colorCardCountMap.get(color);
		//System.out.println("incrementing card count for " + color + " to " + colorCardCountMap.get(color));
		if (count != null) {
			colorCardCountMap.put(color, count + 1);
		} else {
			colorCardCountMap.put(color, 0);
		}
	}

	// decrement the card count for a color
	public static void decrementCardCount(AbstractCard.CardColor color) {
		Integer count = colorCardCountMap.get(color);
		if (count != null) {
			colorCardCountMap.put(color, count - 1);
			if (colorCardCountMap.get(color) < 0) {
				colorCardCountMap.remove(color);
			}
		}
	}

	// get card count for a color
	public static int getCardCount(AbstractCard.CardColor color) {
		Integer count = colorCardCountMap.get(color);
		if (count == null) {
			return -1;
		}
		return count;
	}

	// increment the seen card count for a color
	public static void incrementSeenCardCount(AbstractCard.CardColor color) {
		Integer count = colorCardSeenCountMap.get(color);
		if (count != null) {
			colorCardSeenCountMap.put(color, count + 1);
		} else {
			colorCardSeenCountMap.put(color, 0);
		}
	}

	// get seen card count for a color
	public static int getSeenCardCount(AbstractCard.CardColor color) {
		Integer count = colorCardSeenCountMap.get(color);
		if (count == null) {
			return -1;
		}
		return count;
	}

	// convert a color into a glow color
	public static com.badlogic.gdx.graphics.Color getGlowColor(AbstractCard.CardColor color) {
		return colorGlowColorMap.get(color);
	}

	// convert a color into an attack background texture path
	public static String getAttackBg(AbstractCard.CardColor color) {
		return colorAttackBgMap.get(color);
	}

	// convert a color into an skill background texture path
	public static String getSkillBg(AbstractCard.CardColor color) {
		return colorSkillBgMap.get(color);
	}

	// convert a color into an power background texture path
	public static String getPowerBg(AbstractCard.CardColor color) {
		return colorPowerBgMap.get(color);
	}

	// convert a color into an energy texture path
	public static String getEnergyOrb(AbstractCard.CardColor color) {
		return colorEnergyOrbMap.get(color);
	}

	// convert a color into an attack background portrait texture path
	public static String getAttackBgPortrait(AbstractCard.CardColor color) {
		return colorAttackBgPortraitMap.get(color);
	}

	// convert a color into an skill background portrait texture path
	public static String getSkillBgPortrait(AbstractCard.CardColor color) {
		return colorSkillBgPortraitMap.get(color);
	}

	// convert a color into an power background portrait texture path
	public static String getPowerBgPortrait(AbstractCard.CardColor color) {
		return colorPowerBgPortraitMap.get(color);
	}

	// convert a color into an energy portrait texture path
	public static String getEnergyOrbPortrait(AbstractCard.CardColor color) {
		return colorEnergyOrbPortraitMap.get(color);
	}

	// convert a color into an attack background texture
	public static com.badlogic.gdx.graphics.Texture getAttackBgTexture(AbstractCard.CardColor color) {
		return colorAttackBgTextureMap.get(color);
	}

	// convert a color into an skill background texture
	public static com.badlogic.gdx.graphics.Texture getSkillBgTexture(AbstractCard.CardColor color) {
		return colorSkillBgTextureMap.get(color);
	}

	// convert a color into an power background texture
	public static com.badlogic.gdx.graphics.Texture getPowerBgTexture(AbstractCard.CardColor color) {
		return colorPowerBgTextureMap.get(color);
	}

	// convert a color into an energy texture
	public static com.badlogic.gdx.graphics.Texture getEnergyOrbTexture(AbstractCard.CardColor color) {
		return colorEnergyOrbTextureMap.get(color);
	}

	// convert a color into an energy texture path
	public static TextureAtlas.AtlasRegion getCardEnergyOrbAtlasRegion(AbstractCard.CardColor color) {
		TextureAtlas.AtlasRegion orb = colorCardEnergyOrbAtlasRegionMap.get(color);
		if (orb != null) return orb;
		String orbFile = colorCardEnergyOrbMap.get(color);
		if (orbFile != null) {
			Texture orbTexture = ImageMaster.loadImage(orbFile);
			int tw = orbTexture.getWidth();
			int th = orbTexture.getHeight();
			orb = new TextureAtlas.AtlasRegion(orbTexture, 0, 0, tw, th);
			colorCardEnergyOrbAtlasRegionMap.put(color, orb);
			return orb;
		} else {
			return AbstractCard.orb_red;
		}
	}

	// convert a color into an attack background texture
	public static com.badlogic.gdx.graphics.Texture getAttackBgPortraitTexture(AbstractCard.CardColor color) {
		return colorAttackBgPortraitTextureMap.get(color);
	}

	// convert a color into an skill background texture
	public static com.badlogic.gdx.graphics.Texture getSkillBgPortraitTexture(AbstractCard.CardColor color) {
		return colorSkillBgPortraitTextureMap.get(color);
	}

	// convert a color into an power background texture
	public static com.badlogic.gdx.graphics.Texture getPowerBgPortraitTexture(AbstractCard.CardColor color) {
		return colorPowerBgPortraitTextureMap.get(color);
	}

	// convert a color into an energy texture
	public static com.badlogic.gdx.graphics.Texture getEnergyOrbPortraitTexture(AbstractCard.CardColor color) {
		return colorEnergyOrbPortraitTextureMap.get(color);
	}

	// save a attack background texture for a color
	public static void saveAttackBgTexture(AbstractCard.CardColor color, com.badlogic.gdx.graphics.Texture tex) {
		colorAttackBgTextureMap.put(color, tex);
	}

	// save a skill background texture for a color
	public static void saveSkillBgTexture(AbstractCard.CardColor color, com.badlogic.gdx.graphics.Texture tex) {
		colorSkillBgTextureMap.put(color, tex);
	}

	// save a power background texture for a color
	public static void savePowerBgTexture(AbstractCard.CardColor color, com.badlogic.gdx.graphics.Texture tex) {
		colorPowerBgTextureMap.put(color, tex);
	}

	// save an energy orb texture for a color
	public static void saveEnergyOrbTexture(AbstractCard.CardColor color, com.badlogic.gdx.graphics.Texture tex) {
		colorEnergyOrbTextureMap.put(color, tex);
	}

	// save a attack background texture for a color
	public static void saveAttackBgPortraitTexture(AbstractCard.CardColor color, com.badlogic.gdx.graphics.Texture tex) {
		colorAttackBgPortraitTextureMap.put(color, tex);
	}

	// save a skill background texture for a color
	public static void saveSkillBgPortraitTexture(AbstractCard.CardColor color, com.badlogic.gdx.graphics.Texture tex) {
		colorSkillBgPortraitTextureMap.put(color, tex);
	}

	// save a power background texture for a color
	public static void savePowerBgPortraitTexture(AbstractCard.CardColor color, com.badlogic.gdx.graphics.Texture tex) {
		colorPowerBgPortraitTextureMap.put(color, tex);
	}

	// save an energy orb texture for a color
	public static void saveEnergyOrbPortraitTexture(AbstractCard.CardColor color, com.badlogic.gdx.graphics.Texture tex) {
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

	// add the Potion to the map
	public static void addPotion(Class<? extends AbstractPotion> potionClass, Color liquidColor, Color hybridColor, Color spotsColor, String potionID) {
		addPotion(potionClass, liquidColor, hybridColor, spotsColor, potionID, null);
	}
	public static void addPotion(Class<? extends AbstractPotion> potionClass, Color liquidColor, Color hybridColor, Color spotsColor, String potionID, AbstractPlayer.PlayerClass playerClass) {
		potionClassMap.put(potionID, potionClass);
		potionLiquidColorMap.put(potionID, liquidColor);
		potionHybridColorMap.put(potionID, hybridColor);
		potionSpotsColorMap.put(potionID, spotsColor);
		potionPlayerClassMap.put(potionID, playerClass);
	}

	// return Class corresponding to potionID
	public static Class<? extends AbstractPotion> getPotionClass(String potionID) {
		return potionClassMap.get(potionID);
	}

	// return Colors corresponding to potionID
	public static Color getPotionLiquidColor(String potionID) {
		return potionLiquidColorMap.get(potionID);
	}

	public static Color getPotionHybridColor(String potionID) {
		return potionHybridColorMap.get(potionID);
	}

	public static Color getPotionSpotsColor(String potionID) {
		return potionSpotsColorMap.get(potionID);
	}

	public static AbstractPlayer.PlayerClass getPotionPlayerClass(String potionID) {
		return potionPlayerClassMap.get(potionID);
	}

	// get all potion IDs
	public static Set<String> getPotionIDs() {
		return potionClassMap.keySet();
	}

	//
	// Powers
	//

	public static void addPower(Class<? extends AbstractPower> powerClass, String powerID) {
		powerMap.put(powerID, powerClass);
		if (powerID.contains(" ")) {
			underScorePowerIDs.put(powerID.replace(' ', '_'), powerID);
		}
	}

	public static Class<? extends AbstractPower> getPowerClass(String powerID) {
		return powerMap.get(powerID);
	}

	public static Set<String> getPowerKeys() {
		return powerMap.keySet();
	}

	//
	// Save files
	//

	public static <T> void addSaveField(String key, CustomSavableRaw saveField) {
		customSaveFields.put(key, saveField);
	}

	public static Map<String, CustomSavableRaw> getSaveFields() {
		return customSaveFields;
	}

	//
	// Rich Presence
	//

	public static void setRichPresence(String msg) {
		if (CardCrawlGame.publisherIntegration == null) {
			return;
		}
		if (!(CardCrawlGame.publisherIntegration instanceof SteamIntegration)) {
			return;
		}

		try {
			Method m = SteamIntegration.class.getDeclaredMethod("setRichPresenceData", String.class, String.class);
			m.setAccessible(true);

			m.invoke(CardCrawlGame.publisherIntegration, "status", msg);
			m.invoke(CardCrawlGame.publisherIntegration, "steam_display", "#Status");
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
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

		MAX_HAND_SIZE = DEFAULT_MAX_HAND_SIZE;
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

	// publishPostDungeonUpdate -
	public static void publishPostDungeonUpdate() {
		for(PostDungeonUpdateSubscriber sub : postDungeonUpdateSubscribers) {
			sub.receivePostDungeonUpdate();
		}
		unsubscribeLaterHelper(PostDungeonUpdateSubscriber.class);
	}

	// publishPreDungeonUpdate -
	public static void publishPreDungeonUpdate() {
		for(PreDungeonUpdateSubscriber sub : preDungeonUpdateSubscribers) {
			sub.receivePreDungeonUpdate();
		}
		unsubscribeLaterHelper(PreDungeonUpdateSubscriber.class);
	}

	// publishPostPlayerUpdate -
	public static void publishPostPlayerUpdate() {
		for(PostPlayerUpdateSubscriber sub : postPlayerUpdateSubscribers) {
			sub.receivePostPlayerUpdate();
		}
		unsubscribeLaterHelper(PostPlayerUpdateSubscriber.class);
	}

	// publishPrePlayerUpdate -
	public static void publishPrePlayerUpdate() {
		for(PrePlayerUpdateSubscriber sub : prePlayerUpdateSubscribers) {
			sub.receivePrePlayerUpdate();
		}
		unsubscribeLaterHelper(PrePlayerUpdateSubscriber.class);
	}

	// publishPostCreateStartingDeck -
	public static void publishPostCreateStartingDeck(PlayerClass chosenClass, CardGroup cards) {
		logger.info("postCreateStartingDeck for: " + chosenClass);

		for (PostCreateStartingDeckSubscriber sub : postCreateStartingDeckSubscribers) {
			logger.info("postCreateStartingDeck modifying starting deck for: " + sub);
			sub.receivePostCreateStartingDeck(chosenClass, cards);
		}

		StringBuilder logString = new StringBuilder("postCreateStartingDeck adding [ ");
		for (AbstractCard card : cards.group) {
			logString.append(card.cardID).append(" ");
		}
		logString.append("]");
		logger.info(logString.toString());

		unsubscribeLaterHelper(PostCreateStartingDeckSubscriber.class);
	}

	// publishPostCreateStartingRelics -
	public static void publishPostCreateStartingRelics(PlayerClass chosenClass, ArrayList<String> relics) {
		logger.info("postCreateStartingRelics for: " + chosenClass);

		for (PostCreateStartingRelicsSubscriber sub : postCreateStartingRelicsSubscribers) {
			logger.info("postCreateStartingRelics modifying starting relics for: " + sub);
			sub.receivePostCreateStartingRelics(chosenClass, relics);
		}

		StringBuilder logString = new StringBuilder("postCreateStartingRelics adding [ ");
		for (String relic : relics) {
			logString.append(relic).append(" ");
		}
		logString.append("]");
		logger.info(logString.toString());

		// mark as seen
		for (String relic : relics) {
			UnlockTracker.markRelicAsSeen(relic);
		}

		AbstractDungeon.relicsToRemoveOnStart.addAll(relics);
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

		lastBaseCharacterIndex = CardCrawlGame.characterManager.getAllCharacters().size() - 1;

		for (EditCharactersSubscriber sub : editCharactersSubscribers) {
			sub.receiveEditCharacters();
		}
		unsubscribeLaterHelper(EditCharactersSubscriber.class);
	}

	// publishEditStrings -
	public static void publishEditStrings() {
		logger.info("begin editing localization strings");

		EventUtils.loadBaseEvents();

		String path = String.format("localization/basemod/%s/customMods.json", Settings.language.name().toLowerCase());
		if (!Gdx.files.internal(path).exists()) {
			path = String.format("localization/basemod/%s/customMods.json", Settings.GameLanguage.ENG.name().toLowerCase());
		}
		BaseMod.loadCustomStringsFile(RunModStrings.class, path);

		for (EditStringsSubscriber sub : editStringsSubscribers) {
			sub.receiveEditStrings();
		}
		unsubscribeLaterHelper(EditStringsSubscriber.class);
	}

	// publishAddAudio -
	public static void publishAddAudio(SoundMaster __instance) {
		logger.info("begin adding custom sounds");

		for (AddAudioSubscriber sub : addAudioSubscribers) {
			sub.receiveAddAudio();
		}

		BaseMod.addAudioToSoundMaster(__instance);

		unsubscribeLaterHelper(AddAudioSubscriber.class);
	}

	// publishPostBattle -
	public static void publishPostBattle(AbstractRoom battleRoom) {
		logger.info("publish post combat");

		for (PostBattleSubscriber sub : postBattleSubscribers) {
			sub.receivePostBattle(battleRoom);
		}
		unsubscribeLaterHelper(PostBattleSubscriber.class);
	}

	public static void publishStartBattle(AbstractRoom room){
		logger.info("publish start battle");

		for (OnStartBattleSubscriber sub : startBattleSubscribers) {
			sub.receiveOnBattleStart(room);
		}
		unsubscribeLaterHelper(OnStartBattleSubscriber.class);
	}

	// publishPostRefresh -
	public static void publishPostRefresh() {
		logger.info("publish post refresh - refreshing unlocks");

		for (SetUnlocksSubscriber sub : setUnlocksSubscribers) {
			sub.receiveSetUnlocks();
		}

		CountModdedUnlockCards.enabled = true;
		CountModdedUnlockCards.countModdedUnlocks(); //call it manually, as the count occurs during refresh normally.

		unsubscribeLaterHelper(SetUnlocksSubscriber.class);
	}

	// publishOnCardUse -
	public static void publishOnCardUse(AbstractCard c) {
		logger.info("publish on card use: " + (c == null ? "null" : c.cardID));

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

	public static void publishAddCustomModeMods(List<CustomMod> modList) {
		logger.info("publishAddCustomModeMods");

		CustomMod charMod = new CustomMod("Modded Character Cards", "p", false);
		for (AbstractPlayer character : getModdedCharacters()) {
			CustomMod mod = new CustomMod(RedCards.ID, "g", true);
			mod.ID = character.chosenClass.name() + charMod.name;
			mod.name = String.format(charMod.name, character.getLocalizedCharacterName());
			mod.description = String.format(charMod.description, character.getLocalizedCharacterName());
			String label = FontHelper.colorString("[" + mod.name + "]", mod.color) + " " + mod.description;
			ReflectionHacks.setPrivate(mod, CustomMod.class, "label", label);
			float height = -FontHelper.getSmartHeight(FontHelper.charDescFont, label, 1050.0F * Settings.scale, 32.0F * Settings.scale) + 70.0F * Settings.scale;
			ReflectionHacks.setPrivate(mod, CustomMod.class, "height", height);

			insertCustomMod(modList, mod);
		}

		for (AddCustomModeModsSubscriber sub : addCustomModeModsSubscribers) {
			List<CustomMod> tmpModList = new ArrayList<>();
			sub.receiveCustomModeMods(tmpModList);

			tmpModList.forEach(m -> insertCustomMod(modList, m));
		}
		unsubscribeLaterHelper(AddCustomModeModsSubscriber.class);
	}

	private static void insertCustomMod(List<CustomMod> modList, CustomMod mod)
	{
		int lastIndex = modList.size();
		for (int i=0; i<modList.size(); ++i) {
			if (modList.get(i).color.equals(mod.color)) {
				lastIndex = i + 1;
			}
		}
		modList.add(lastIndex, mod);
	}

	public static int publishMaxHPChange(int amount) {
		logger.info("publishMaxHPChange");

		for (MaxHPChangeSubscriber sub : maxHPChangeSubscribers) {
			amount = sub.receiveMapHPChange(amount);
		}
		unsubscribeLaterHelper(PostDeathSubscriber.class);

		return amount;
	}

	public static void publishPreRoomRender(SpriteBatch sb) {
		for(PreRoomRenderSubscriber sub : preRoomRenderSubscribers) {
			sub.receivePreRoomRender(sb);
		}
	}

	// publishOnPlayerLoseBlock
	public static int publishOnPlayerLoseBlock(int amount) {
		logger.info("publish on Player Lose Block");

		for (OnPlayerLoseBlockSubscriber sub : onPlayerLoseBlockSubscribers) {
			amount = sub.receiveOnPlayerLoseBlock(amount);
		}

		unsubscribeLaterHelper(OnPlayerLoseBlockSubscriber.class);
		return amount;
	}

	public static int publishOnPlayerDamaged(int amount, DamageInfo info) {
		logger.info("publish on Player Damaged");

		for (OnPlayerDamagedSubscriber sub : onPlayerDamagedSubscribers) {
			amount = sub.receiveOnPlayerDamaged(amount, info);
		}

		unsubscribeLaterHelper(OnPlayerDamagedSubscriber.class);
		return amount;
	}

	public static String publishOnCreateDescription(String rawDescription, AbstractCard card) {
		//logger.info("publish on card description initialized"); //too much logging?

		for (OnCreateDescriptionSubscriber sub : onCreateDescriptionSubscribers) {
			rawDescription = sub.receiveCreateCardDescription(rawDescription, card);
		}

		unsubscribeLaterHelper(OnCreateDescriptionSubscriber.class);
		return rawDescription;
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

	private static <T> void subscribeIfInstance(ArrayList<T> list, ISubscriber sub, Class<T> clazz) {
		if (clazz.isInstance(sub)) {
			list.add(clazz.cast(sub));
		}
	}

	private static <T> void unsubscribeIfInstance(ArrayList<T> list, ISubscriber sub, Class<T> clazz) {
		if (clazz.isInstance(sub)) {
			list.remove(clazz.cast(sub));
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
		subscribeIfInstance(postDungeonUpdateSubscribers, sub, PostDungeonUpdateSubscriber.class);
		subscribeIfInstance(preDungeonUpdateSubscribers, sub, PreDungeonUpdateSubscriber.class);
		subscribeIfInstance(postPlayerUpdateSubscribers, sub, PostPlayerUpdateSubscriber.class);
		subscribeIfInstance(prePlayerUpdateSubscribers, sub, PrePlayerUpdateSubscriber.class);
		subscribeIfInstance(postCreateStartingDeckSubscribers, sub, PostCreateStartingDeckSubscriber.class);
		subscribeIfInstance(postCreateStartingRelicsSubscribers, sub, PostCreateStartingRelicsSubscriber.class);
		subscribeIfInstance(postCreateShopRelicSubscribers, sub, PostCreateShopRelicSubscriber.class);
		subscribeIfInstance(postCreateShopPotionSubscribers, sub, PostCreateShopPotionSubscriber.class);
		subscribeIfInstance(editCardsSubscribers, sub, EditCardsSubscriber.class);
		subscribeIfInstance(addAudioSubscribers, sub, AddAudioSubscriber.class);
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
		subscribeIfInstance(addCustomModeModsSubscribers, sub, AddCustomModeModsSubscriber.class);
		subscribeIfInstance(maxHPChangeSubscribers, sub, MaxHPChangeSubscriber.class);
		subscribeIfInstance(preRoomRenderSubscribers, sub, PreRoomRenderSubscriber.class);
		subscribeIfInstance(onPlayerLoseBlockSubscribers, sub, OnPlayerLoseBlockSubscriber.class);
		subscribeIfInstance(onPlayerDamagedSubscribers, sub, OnPlayerDamagedSubscriber.class);
		subscribeIfInstance(onCreateDescriptionSubscribers, sub, OnCreateDescriptionSubscriber.class);
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
		} else if (additionClass.equals(PostDungeonUpdateSubscriber.class)) {
			postDungeonUpdateSubscribers.add((PostDungeonUpdateSubscriber) sub);
		} else if (additionClass.equals(PreDungeonUpdateSubscriber.class)) {
			preDungeonUpdateSubscribers.add((PreDungeonUpdateSubscriber) sub);
		} else if (additionClass.equals(PostPlayerUpdateSubscriber.class)) {
			postPlayerUpdateSubscribers.add((PostPlayerUpdateSubscriber) sub);
		} else if (additionClass.equals(PrePlayerUpdateSubscriber.class)) {
			prePlayerUpdateSubscribers.add((PrePlayerUpdateSubscriber) sub);
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
		} else if (additionClass.equals(AddCustomModeModsSubscriber.class)) {
			addCustomModeModsSubscribers.add((AddCustomModeModsSubscriber) sub);
		} else if (additionClass.equals(MaxHPChangeSubscriber.class)) {
			maxHPChangeSubscribers.add((MaxHPChangeSubscriber) sub);
		} else if (additionClass.equals(PreRoomRenderSubscriber.class)) {
			preRoomRenderSubscribers.add((PreRoomRenderSubscriber) sub);
		} else if (additionClass.equals(OnPlayerLoseBlockSubscriber.class)) {
			onPlayerLoseBlockSubscribers.add((OnPlayerLoseBlockSubscriber) sub);
		} else if (additionClass.equals(OnPlayerDamagedSubscriber.class)) {
			onPlayerDamagedSubscribers.add((OnPlayerDamagedSubscriber) sub);
		} else if (additionClass.equals(OnCreateDescriptionSubscriber.class)) {
			onCreateDescriptionSubscribers.add((OnCreateDescriptionSubscriber) sub);
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
		unsubscribeIfInstance(postDungeonUpdateSubscribers, sub, PostDungeonUpdateSubscriber.class);
		unsubscribeIfInstance(preDungeonUpdateSubscribers, sub, PreDungeonUpdateSubscriber.class);
		unsubscribeIfInstance(postPlayerUpdateSubscribers, sub, PostPlayerUpdateSubscriber.class);
		unsubscribeIfInstance(prePlayerUpdateSubscribers, sub, PrePlayerUpdateSubscriber.class);
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
		unsubscribeIfInstance(addCustomModeModsSubscribers, sub, AddCustomModeModsSubscriber.class);
		unsubscribeIfInstance(maxHPChangeSubscribers, sub, MaxHPChangeSubscriber.class);
		unsubscribeIfInstance(preRoomRenderSubscribers, sub, PreRoomRenderSubscriber.class);
		unsubscribeIfInstance(onPlayerLoseBlockSubscribers, sub, OnPlayerLoseBlockSubscriber.class);
		unsubscribeIfInstance(onPlayerDamagedSubscribers, sub, OnPlayerDamagedSubscriber.class);
		unsubscribeIfInstance(onCreateDescriptionSubscribers, sub, OnCreateDescriptionSubscriber.class);
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
		} else if (removalClass.equals(PostDungeonUpdateSubscriber.class)) {
			postDungeonUpdateSubscribers.remove(sub);
		} else if (removalClass.equals(PreDungeonUpdateSubscriber.class)) {
			preDungeonUpdateSubscribers.remove(sub);
		} else if (removalClass.equals(PostPlayerUpdateSubscriber.class)) {
			postPlayerUpdateSubscribers.remove(sub);
		} else if (removalClass.equals(PrePlayerUpdateSubscriber.class)) {
			prePlayerUpdateSubscribers.remove(sub);
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
		} else if (removalClass.equals(AddAudioSubscriber.class)) {
			addAudioSubscribers.remove(sub);
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
		} else if (removalClass.equals(AddCustomModeModsSubscriber.class)) {
			addCustomModeModsSubscribers.remove(sub);
		} else if (removalClass.equals(MaxHPChangeSubscriber.class)) {
			maxHPChangeSubscribers.remove(sub);
		} else if (removalClass.equals(PreRoomRenderSubscriber.class)) {
			preRoomRenderSubscribers.remove(sub);
		} else if (removalClass.equals(OnPlayerLoseBlockSubscriber.class)) {
			onPlayerLoseBlockSubscribers.remove(sub);
		} else if (removalClass.equals(OnPlayerDamagedSubscriber.class)) {
			onPlayerDamagedSubscribers.remove(sub);
		} else if (removalClass.equals(OnCreateDescriptionSubscriber.class)) {
			onCreateDescriptionSubscribers.remove(sub);
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

	@SuppressWarnings("unused")
	/**
	 * Open a {@link com.megacrit.cardcrawl.screens.select.GridCardSelectScreen} for selecting cards.
	 * Executes a callback function with the cards selected once selection is completed. Method must
	 * be called after {@link AbstractDungeon} has been initialized
	 *
	 * @param group Group of cards to select from
	 * @param numCards Number of cards to select
	 * @param tipMsg Tip message displayed at the bottom of the screen
	 * @param callback Callback function that is executed once card selection is complete. This function
	 *                 is not executed if the card selection is canceled/skipped.
	 *
	 *                 Example callback function: (cards) -> { cards.forEach(c -> logger.debug(c.cardID)); }
	 */
	public static void openCustomGridScreen(CardGroup group, int numCards, String tipMsg, GridCardSelectScreenFields.GridCallback callback) {
		logger.debug("Opening custom grid screen");
		String gridCancelText = CardCrawlGame.languagePack.getUIString("CardRewardScreen").TEXT[0];
		AbstractDungeon.gridSelectScreen.open(group, numCards, tipMsg, false);
		AbstractDungeon.overlayMenu.cancelButton.show(gridCancelText);
		AbstractDungeon.dynamicBanner.hide();
		GridCardSelectScreenFields.forCustomReward.set(AbstractDungeon.gridSelectScreen, true);
		GridCardSelectScreenFields.customCallback.set(AbstractDungeon.gridSelectScreen, callback);
	}
}
