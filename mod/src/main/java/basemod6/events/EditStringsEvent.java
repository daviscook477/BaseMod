package basemod6.events;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod6.BaseMod6;
import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static basemod.BaseMod.gson;

public class EditStringsEvent extends Event
{
	private static HashMap<Type, String> typeMaps;
	private static HashMap<Type, Type> typeTokens;

	static {
		BaseMod6.logger.info("initializeTypeMaps");

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

					BaseMod6.logger.info("Registered " + typeArgs[1].getTypeName().replace("com.megacrit.cardcrawl.localization.", ""));
					typeMaps.put(typeArgs[1], f.getName());
					ParameterizedType p = com.google.gson.internal.$Gson$Types.newParameterizedTypeWithOwner(null, Map.class, String.class, typeArgs[1]);
					typeTokens.put(typeArgs[1], p);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> void loadJsonStrings(Class<T> stringType, String jsonString)
	{
		BaseMod6.logger.info("loadJsonStrings: " + stringType.getTypeName());

		String typeMap = typeMaps.get(stringType);
		Type typeToken = typeTokens.get(stringType);

		String modName = BaseMod.findCallingModName();

		Map<String, T> localizationStrings = (Map<String, T>) ReflectionHacks.getPrivateStatic(LocalizedStrings.class, typeMap);
		if (localizationStrings != null) {
			Map<String, T> map = gson.fromJson(jsonString, typeToken);
			if (stringType.equals(CardStrings.class) || stringType.equals(RelicStrings.class)) {
				Map<String, T> map2 = new HashMap<>();
				for (String k : map.keySet()) {
					map2.put(modName == null ? k : modName + ":" + k, map.get(k));
				}
				localizationStrings.putAll(map2);
			} else {
				localizationStrings.putAll(map);
			}
			ReflectionHacks.setPrivateStaticFinal(LocalizedStrings.class, typeMap, localizationStrings);
		}
	}

	// loadCustomRelicStrings - loads custom RelicStrings from provided JSON
	// should be done inside the callback of an implementation of
	// EditStringsSubscriber
	public <T> void loadCustomStrings(Class<T> stringType, String jsonString)
	{
		loadJsonStrings(stringType, jsonString);
	}

	public <T> void loadCustomStringsFile(Class<T> stringType, String filepath)
	{
		loadJsonStrings(stringType, Gdx.files.internal(filepath).readString(String.valueOf(StandardCharsets.UTF_8)));
	}
}
