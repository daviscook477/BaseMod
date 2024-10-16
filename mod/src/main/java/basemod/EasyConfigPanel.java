package basemod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EasyConfigPanel extends ModPanel {
    private static final float ELEMENT_X = 355;
    private static final float MIN_SLIDER_X = ELEMENT_X + 450f;
    private static final float PAGE_START_Y = 730f;
    private static final float PAGE_MIN_Y = 140f;

    private final String modID;
    private final UIStrings uiStrings;

    private SpireConfig config; //Only for internal use.
    private final List<ConfigField> configFields;

    private final Map<String, List<Pair<IUIElement, Float>>> customElements = new HashMap<>();
    private final Map<String, Map<String, Object>> buildParams = new HashMap<>();
    private float elementPadding = 8f;

    private List<List<IUIElement>> pages = new ArrayList<>();

    public EasyConfigPanel(String modID, String localizationID) {
        this(modID, CardCrawlGame.languagePack.getUIString(localizationID), "config");
    }
    public EasyConfigPanel(String modID, UIStrings text, String configName) {
        super(EasyConfigPanel::buildInterface);

        this.modID = modID;
        if (text == null) text = new UIStrings();
        uiStrings = text;
        if (uiStrings.TEXT_DICT == null) uiStrings.TEXT_DICT = Collections.emptyMap();

        //Read static fields of class. Their values should have been initialized before this super occurs.
        Properties configDefaults = new Properties();
        configFields = new ArrayList<>();

        try {
            List<Field> validFields = new ArrayList<>();
            for (Field f : getClass().getDeclaredFields()) {
                int modifiers = f.getModifiers();
                if (!Modifier.isStatic(modifiers) || Modifier.isPrivate(modifiers) || Modifier.isFinal(modifiers) || Modifier.isTransient(modifiers))
                    continue;

                configDefaults.put(f.getName(), String.valueOf(f.get(null)));
                validFields.add(f);
            }

            config = new SpireConfig(modID, configName, configDefaults);
            for (Field f : validFields) {
                configFields.add(makeConfigField(config, f));
            }
        }
        catch (IOException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set up SpireConfig for " + modID, e);
        }
    }

    public void addUIElement(String fieldName, IUIElement element, float elementHeight) {
        customElements.compute(fieldName,
                (k, v)->{
                    if (v == null) v = new ArrayList<>();
                    v.add(new Pair<>(element, elementHeight));
                    return v;
                }
            );
    }

    public void setNumberRange(String fieldName, float minValue, float maxValue) {
        buildParams.compute(fieldName,
                (k, v)->{
                    if (v == null) v = new HashMap<>();
                    v.put("MIN", minValue);
                    v.put("MAX", maxValue);
                    return v;
                }
        );
    }

    public void setupTextField(String fieldName, float width, int charLimit) {
        setupTextField(fieldName, width, charLimit, null, null);
    }
    public void setupTextField(String fieldName, float width, int charLimit, Predicate<Character> charFilter, Predicate<String> resultValidation) {
        buildParams.compute(fieldName,
                (k, v)->{
                    if (v == null) v = new HashMap<>();
                    v.put("WIDTH", width);
                    v.put("CHARACTERS", charLimit);
                    v.put("FILTER", charFilter);
                    v.put("VALIDATION", resultValidation);
                    return v;
                }
        );
    }

    public void setPadding(float padding) {
        this.elementPadding = padding;
    }

    private static void buildInterface(ModPanel modPanel) {
        if (!(modPanel instanceof EasyConfigPanel)) return;

        try {
            ((EasyConfigPanel) modPanel).buildInterface0();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to build EasyConfigPanel for " + ((EasyConfigPanel) modPanel).modID, e);
        }
    }

    private void buildInterface0() throws IllegalAccessException {
        List<IUIElement> page = new ArrayList<>();
        pages.add(page);

        float pagePos = PAGE_START_Y;

        for (ConfigField field : configFields) {
            List<Pair<IUIElement, Float>> fieldElement = customElements.get(field.getName());

            if (fieldElement == null) {
                fieldElement = field.buildElements(this, uiStrings.TEXT_DICT.get(field.getName()), buildParams.get(field.getName()));
            }

            float totalHeight = 0;
            for (Pair<IUIElement, Float> element : fieldElement) {
                if (element.getValue() > 0) {
                    totalHeight += element.getValue();
                    totalHeight += elementPadding;
                }
            }

            if (pagePos - totalHeight < PAGE_MIN_Y) {
                page = new ArrayList<>();
                pages.add(page);
                pagePos = PAGE_START_Y;
            }

            for (Pair<IUIElement, Float> element : fieldElement) {
                element.getKey().setY(pagePos);
                page.add(element.getKey());

                if (element.getValue() > 0) { //if custom element has 0 height, they want it to have the same y
                    pagePos -= (element.getValue() + elementPadding);
                }
            }
        }

        if (pages.size() > 1) {
            //Add page change buttons
            int pageNum = 0;
            for (List<IUIElement> finishedPage : pages) {
                int thisPageNum = pageNum;
                finishedPage.add(new ModLabeledButton(">", ELEMENT_X + 1010f, PAGE_START_Y - 20,
                        Settings.CREAM_COLOR, Color.WHITE, FontHelper.cardEnergyFont_L, this,
                        button -> setPage(thisPageNum + 1))
                );
                finishedPage.add(new ModLabeledButton("<", ELEMENT_X + 910f, PAGE_START_Y - 20,
                        Settings.CREAM_COLOR, Color.WHITE, FontHelper.cardEnergyFont_L, this,
                        button -> setPage(thisPageNum - 1))
                );
                ++pageNum;
            }
        }

        setPage(0);
    }

    public void setPage(int pageIndex) {
        if (pages.isEmpty()) return;
        if (pageIndex < 0) pageIndex = pages.size() - 1;;
        pageIndex %= pages.size();

        getUpdateElements().clear();
        getRenderElements().clear();

        for (IUIElement element : pages.get(pageIndex)) {
            addUIElement(element);
        }
    }

    //For manual saving if someone ever has to do that for some reason
    public void save() {
        try {
            for (ConfigField field : configFields) {
                field.readValue();
            }
            config.save();
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to read field", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config", e);
        }
    }

    @Override
    public void update() {
        int size = getUpdateElements().size();
        for (int i = 0; i < size; ++i) {
            IUIElement next = getUpdateElements().get(i);
            next.update();
            if (getUpdateElements().size() != size || !getUpdateElements().get(i).equals(next)) {
                break;
            }
        }

        if (InputHelper.pressedEscape) {
            InputHelper.pressedEscape = false;
            BaseMod.modSettingsUp = false;
        }

        if (!BaseMod.modSettingsUp) {
            waitingOnEvent = false;
            Gdx.input.setInputProcessor(oldInputProcessor);
            CardCrawlGame.mainMenuScreen.lighten();
            CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
            CardCrawlGame.cancelButton.hideInstantly();
            isUp = false;
        }
    }

    private static ConfigField makeConfigField(SpireConfig config, Field f) {
        BiFunction<SpireConfig, Field, ConfigField> builder = configFieldBuilders.get(f.getType());
        if (builder == null) {
            throw new RuntimeException("Type " + f.getName() + " is not supported by EasyConfigPanel. If you require this type, registerFieldType can be used.");
        }
        return builder.apply(config, f);
    }

    private static final Map<Class<?>, BiFunction<SpireConfig, Field, ConfigField>> configFieldBuilders = new HashMap<>();
    public static void registerFieldType(Class<?> cls, BiFunction<SpireConfig, Field, ConfigField> builder) {
        if (configFieldBuilders.containsKey(cls)) {
            BaseMod.logger.warn("Duplicate ConfigField builder registered for EasyConfigPanel for class " + cls.getName());
        }
        configFieldBuilders.put(cls, builder);
    }

    static {
        registerFieldType(String.class, (config, field)->
            new ConfigField(config, field, (s)->field.set(null, s),
                    (configField, elements, params, panel, displayName)->{
                        elements.add(new Pair<>(new AutoOffsetLabel(displayName, ELEMENT_X, 0, Settings.CREAM_COLOR,
                                FontHelper.charDescFont, panel, (label)->{}), 0f));

                        float textWidth = FontHelper.getWidth(FontHelper.charDescFont, displayName, 1f / Settings.scale);

                        float width = (float) params.getOrDefault("WIDTH", 300f);
                        int charLimit = (int) params.getOrDefault("CHARACTERS", 50);
                        Predicate<Character> filter = (Predicate<Character>) params.getOrDefault("FILTER", null);
                        Predicate<String> validator = (Predicate<String>) params.getOrDefault("VALIDATION", null);

                        elements.add(new Pair<>(
                                new ModTextInput((String) field.get(null), Math.max(MIN_SLIDER_X - 15, ELEMENT_X + 75 + textWidth), 0, width, 32,
                                        FontHelper.charDescFont, panel,
                                        (textInput) -> configField.setValue(textInput.text, true))
                                        .setCharacterLimit(charLimit)
                                        .setCharacterFilter(filter)
                                        .setResultValidator(validator), 50f)
                        );
                    })
        );
        registerFieldType(byte.class, (config, field)->
                new ConfigField(config, field, (s)->field.set(null, Byte.parseByte(s)),
                        (configField, elements, params, panel, displayName)->{
                            float min = (float) params.getOrDefault("MIN", 0f);
                            float max = (float) params.getOrDefault("MAX", 100f);
                            if (max < min) {
                                float temp = min;
                                min = max;
                                max = temp;
                            }
                            buildSlider(elements, panel, displayName, "%.0f", min, max, (byte) field.get(null), (fVal)->{
                                configField.setValue(fVal.byteValue(), true);
                            });
                        })
        );
        registerFieldType(short.class, (config, field)->
                new ConfigField(config, field, (s)->field.set(null, Short.parseShort(s)),
                        (configField, elements, params, panel, displayName)->{
                            float min = (float) params.getOrDefault("MIN", 0f);
                            float max = (float) params.getOrDefault("MAX", 100f);
                            if (max < min) {
                                float temp = min;
                                min = max;
                                max = temp;
                            }
                            buildSlider(elements, panel, displayName, "%.0f", min, max, (short) field.get(null), (fVal)->{
                                configField.setValue(fVal.shortValue(), true);
                            });
                        })
        );
        registerFieldType(int.class, (config, field)->
                new ConfigField(config, field, (s)->field.set(null, Integer.parseInt(s)),
                        (configField, elements, params, panel, displayName)->{
                            float min = (float) params.getOrDefault("MIN", 0f);
                            float max = (float) params.getOrDefault("MAX", 100f);
                            if (max < min) {
                                float temp = min;
                                min = max;
                                max = temp;
                            }
                            buildSlider(elements, panel, displayName, "%.0f", min, max, (int) field.get(null), (fVal)->{
                                configField.setValue(MathUtils.round(fVal), true);
                            });
                        })
        );
        registerFieldType(long.class, (config, field)->
                new ConfigField(config, field, (s)->field.set(null, Long.parseLong(s)),
                        (configField, elements, params, panel, displayName)->{
                            float min = (float) params.getOrDefault("MIN", 0f);
                            float max = (float) params.getOrDefault("MAX", 100f);
                            if (max < min) {
                                float temp = min;
                                min = max;
                                max = temp;
                            }
                            buildSlider(elements, panel, displayName, "%.0f", min, max, (long) field.get(null), (fVal)->{
                                configField.setValue(fVal.longValue(), true);
                            });
                        })
        );
        registerFieldType(float.class, (config, field)->
                new ConfigField(config, field, (s)->field.set(null, Float.parseFloat(s)),
                        (configField, elements, params, panel, displayName)->{
                            float min = (float) params.getOrDefault("MIN", 0f);
                            float max = (float) params.getOrDefault("MAX", 100f);
                            if (max < min) {
                                float temp = min;
                                min = max;
                                max = temp;
                            }
                            buildSlider(elements, panel, displayName, "%.2f", min, max, (float) field.get(null), (fVal)->{
                                configField.setValue(fVal, true);
                            });
                        })
        );
        registerFieldType(double.class, (config, field)->
                new ConfigField(config, field, (s)->field.set(null, Double.parseDouble(s)),
                        (configField, elements, params, panel, displayName)->{
                            float min = (float) params.getOrDefault("MIN", 0f);
                            float max = (float) params.getOrDefault("MAX", 100f);
                            if (max < min) {
                                float temp = min;
                                min = max;
                                max = temp;
                            }
                            buildSlider(elements, panel, displayName, "%.3f", min, max, ((Double) field.get(null)).floatValue(), (fVal)->{
                                configField.setValue(fVal.doubleValue(), true);
                            });
                        })
        );
        registerFieldType(boolean.class, (config, field)->
                new ConfigField(config, field, (s)->field.set(null, Boolean.parseBoolean(s)),
                        (configField, elements, params, panel, displayName)->{
                            elements.add(
                                    new Pair<>(new ModLabeledToggleButton(displayName, ELEMENT_X, 0, Settings.CREAM_COLOR,
                                            FontHelper.charDescFont, (boolean) field.get(null), panel, (label)->{},
                                            (button) -> {
                                                configField.setValue(button.enabled, true);
                                            }), 50f)
                            );
                        })
        );
        /*registerFieldType(char.class, (config, field)->
                new ConfigField(config, field, (s)->{
                    if (s.length() != 1) throw new InvalidParameterException("Attempted to set char field " + field.getName() + " to non-char value \"" + s + "\"");
                    field.set(null, s.charAt(0));
                }, (configField, elements, params, panel, displayName)->{})
        );*/
    }

    private static void buildSlider(List<Pair<IUIElement, Float>> elements, ModPanel panel, String displayName, String format, float min, float max, float pos, Consumer<Float> setVal) {
        elements.add(new Pair<>(new AutoOffsetLabel(displayName, ELEMENT_X, 0, Settings.CREAM_COLOR,
                        FontHelper.charDescFont, panel, (label)->{}), 0f));

        float textWidth = FontHelper.getWidth(FontHelper.charDescFont, displayName, 1f / Settings.scale);

        elements.add(new Pair<>(
                new AutoOffsetSlider("", Math.max(MIN_SLIDER_X, ELEMENT_X + 100 + textWidth), 0, min, max, pos, format, panel,
                        (slider) -> setVal.accept(slider.getValue())), 50f)
        );
    }

    public static class ConfigField {
        private final SpireConfig config;
        private final Field field;
        private final FieldSetter fieldSetter;
        private final UIBuilder uiBuilder;

        public ConfigField(SpireConfig config, Field field, FieldSetter setter, UIBuilder uiBuilder) {
            this.config = config;
            this.field = field;
            this.fieldSetter = setter;
            this.uiBuilder = uiBuilder;

            loadValue();
        }

        //Move value from config to field
        void loadValue() {
            setValue(config.getString(field.getName()), false);
        }

        //Move value from field to config
        void readValue() throws IllegalAccessException {
            config.setString(field.getName(), field.get(null).toString());
        }

        void setValue(Object val, boolean save) {
            try {
                fieldSetter.set(val.toString());
                readValue();
                if (save) {
                    config.save();
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to set value of config field " + field.getName(), e);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save config", e);
            }
        }

        public String getName() {
            return field.getName();
        }

        @Override
        public String toString() {
            return field.toString();
        }

        private List<Pair<IUIElement, Float>> buildElements(ModPanel panel, String displayName, Map<String, Object> buildParams) throws IllegalAccessException {
            if (displayName == null) displayName = getName();
            if (buildParams == null) buildParams = Collections.emptyMap();

            List<Pair<IUIElement, Float>> elements = new ArrayList<>();
            uiBuilder.build(this, elements, buildParams, panel, displayName);

            return elements;
        }

        public interface FieldSetter {
            void set(String val) throws IllegalAccessException;
        }

        public interface UIBuilder {
            void build(ConfigField configField, List<Pair<IUIElement, Float>> elements, Map<String, Object> params, ModPanel panel, String displayName) throws IllegalAccessException;
        }
    }

    private static class AutoOffsetLabel extends ModLabel {
        public AutoOffsetLabel(String labelText, float xPos, float yPos, ModPanel p, Consumer<ModLabel> updateFunc) {
            super(labelText, xPos + 40F, yPos + 8F, p, updateFunc);
        }

        public AutoOffsetLabel(String labelText, float xPos, float yPos, Color color, ModPanel p, Consumer<ModLabel> updateFunc) {
            super(labelText, xPos + 40F, yPos + 8F, color, p, updateFunc);
        }

        public AutoOffsetLabel(String labelText, float xPos, float yPos, BitmapFont font, ModPanel p, Consumer<ModLabel> updateFunc) {
            super(labelText, xPos + 40F, yPos + 8F, font, p, updateFunc);
        }

        public AutoOffsetLabel(String labelText, float xPos, float yPos, Color color, BitmapFont font, ModPanel p, Consumer<ModLabel> updateFunc) {
            super(labelText, xPos + 40F, yPos + 8F, color, font, p, updateFunc);
        }

        @Override
        public void setX(float xPos) {
            super.setX(xPos + 40F);
        }

        @Override
        public void setY(float yPos) {
            super.setY(yPos + 8F);
        }
    }

    private static class AutoOffsetSlider extends ModMinMaxSlider {
        public AutoOffsetSlider(String lbl, float posX, float posY, float min, float max, float val, String format, ModPanel p, Consumer<ModMinMaxSlider> changeAction) {
            super(lbl, posX, posY + 15f, min, max, val, format, p, changeAction);
        }

        @Override
        public void setY(float yPos) {
            super.setY(yPos + 15f);
        }
    }
}
