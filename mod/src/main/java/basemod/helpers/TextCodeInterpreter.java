package basemod.helpers;

import basemod.BaseMod;
import basemod.devcommands.ConsoleCommand;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class TextCodeInterpreter {
    private static final HashMap<String, Class<?>> mappedAccess;
    public static void addAccessible(Class<?> cls) {
        if (cls == null)
            return;

        mappedAccess.put(cls.getSimpleName(), cls);
    }
    public static void addAccessible(String name, Class<?> cls) {
        if (cls == null)
            return;

        mappedAccess.put(name, cls);
    }
    static {
        mappedAccess = new HashMap<>();

        addAccessible(AbstractDungeon.class);
        addAccessible(CardCrawlGame.class);
        addAccessible(CardLibrary.class);
        addAccessible(RelicLibrary.class);
        addAccessible(PotionHelper.class);
        addAccessible(EventHelper.class);
        addAccessible(Color.class);
        addAccessible("ModLoader", Loader.class);
    }

    public static Set<String> getBaseOptions() {
        return mappedAccess.keySet();
    }

    public static class ParameterProvider {
        private final Class<?> cls;
        private final String key;
        private final Function<String, InterpretedResult> provider;

        public ParameterProvider(Class<?> cls, String key, Function<String, InterpretedResult> conversion) {
            this.cls = cls;
            this.key = key;
            this.provider = conversion;
        }
        public List<String> getSuggestions() {
            return Collections.emptyList();
        }
    }
    private static final HashMap<String, ParameterProvider> mappedProviders;
    private static final List<ParameterProvider> parameterProviders;
    public static void registerCustomConstant(ParameterProvider p) {
        parameterProviders.add(p);
        mappedProviders.put(p.key, p);
    }
    static {
        parameterProviders = new ArrayList<>();
        mappedProviders = new HashMap<>();

        registerCustomConstant(new ParameterProvider(AbstractCard.class, "card", (id)-> {
            id = id.replace('_', ' ');
            if (CardLibrary.cards.containsKey(id)) {
                String finalId = id;
                return new InterpretedResult(()->CardLibrary.getCopy(finalId));
            }
            return null;
        }) {
            @Override
            public List<String> getSuggestions() {
                return ConsoleCommand.getCardOptions();
            }
        });
        registerCustomConstant(new ParameterProvider(AbstractRelic.class, "relic", (id)-> {
            id = id.replace('_', ' ');
            if (RelicLibrary.isARelic(id)) {
                String finalId = id;
                return new InterpretedResult(()->RelicLibrary.getRelic(finalId));
            }
            return null;
        }) {
            @Override
            public List<String> getSuggestions() {
                return ConsoleCommand.getRelicOptions();
            }
        });

        registerCustomConstant(new ParameterProvider(AbstractPotion.class, "potion", (id)-> {
            id = id.replace('_', ' ');
            if (PotionHelper.isAPotion(id)) {
                String finalId = id;
                return new InterpretedResult(()->PotionHelper.getPotion(finalId));
            }
            return null;
        }) {
            @Override
            public List<String> getSuggestions() {
                return potions;
            }
        });

        List<String> listTypes = new ArrayList<>();
        listTypes.add("empty");
        registerCustomConstant(new ParameterProvider(List.class, "arraylist", (id)->{
            if (id.equals("empty")) {
                return new InterpretedResult(ArrayList::new);
            }
            return null;
        }) {
            @Override
            public List<String> getSuggestions() {
                return listTypes;
            }
        });
    }

    //Will return either a field or method, which can be accessed or called using the object returned.
    public static InterpretedResult interpret(String textCode)
    {
        String[] byrefText = new String[] { textCode };
        Class<?> base = getBaseClass(byrefText);

        if (base == null)
            return null;

        return interpret(base, byrefText);
    }

    public static InterpretedResult lastComplete(String textCode)
    {
        String[] byrefText = new String[] { textCode };
        Class<?> base = getBaseClass(byrefText);

        if (base == null)
            return null;

        return lenientInterpret(base, byrefText);
    }

    private static InterpretedResult interpret(Class<?> clazz, String[] textCode)
    {
        //The first token must be static, otherwise it cannot be accessed.
        InterpretedResult next = processStaticToken(clazz, textCode);

        if (next == null)
            return null;

        //first should be an InterpretedResult of a static field or method
        try {
            while (textCode[0].length() > 0)
            {
                next = processToken(next, textCode);

                if (next == null) //invalid token
                    return null;
            }
        }
        catch (Exception e)
        {
            return null;
        }

        return next;
    }
    private static InterpretedResult lenientInterpret(Class<?> clazz, String[] textCode)
    {
        //The first token must be static, otherwise it cannot be accessed.
        String leftover = textCode[0];
        InterpretedResult next = processLenientStaticToken(clazz, textCode), last = next;

        if (next == null) {
            textCode[0] = leftover;
            return null; //first non-base token is incomplete, still on the base's stuff.
        }

        //first should be an InterpretedResult of a static field or method
        try {
            while (textCode[0].length() > 0)
            {
                leftover = textCode[0];
                next = processLenientToken(last, textCode);

                if (next == null) { //invalid token
                    textCode[0] = leftover;
                    return last;
                }

                last = next;
            }
        }
        catch (Exception e)
        {
            textCode[0] = leftover;
            return null;
        }

        //Completely processed as valid tokens
        return next;
    }

    private static Class<?> getBaseClass(String[] textCode)
    {
        int split = textCode[0].indexOf('.');
        if (split > 0 && textCode[0].length() > split + 1)
        {
            String base = textCode[0].substring(0, split);
            textCode[0] = textCode[0].substring(split + 1);

            if (mappedAccess.containsKey(base))
                return mappedAccess.get(base);
        }
        return null;
    }
    public static Class<?> getBaseClass(String textCode)
    {
        int split = textCode.indexOf('.');
        if (split > 0)
        {
            String base = textCode.substring(0, split);

            if (mappedAccess.containsKey(base))
                return mappedAccess.get(base);
        }
        return null;
    }

    private static InterpretedResult processStaticToken(Class<?> clazz, String[] textCode)
    {
        int nextDot = textCode[0].indexOf('.');
        int nextParentheses = textCode[0].indexOf('(');

        String name;

        if (nextParentheses != -1 && (nextParentheses < nextDot || nextDot == -1)) //this is a function
        {
            name = textCode[0].substring(0, nextParentheses);

            //textCode is trimmed by getParenthesesSection
            return processStaticMethod(clazz, name, getParenthesesSection(textCode, nextParentheses));
        }
        else if (nextDot == -1) //this is the last token (and not a function), so it should just be a field
        {
            name = textCode[0];
            textCode[0] = "";
        }
        else //This is maybe a field?
        {
            if (textCode[0].length() > nextDot + 1)
            {
                name = textCode[0].substring(0, nextDot);
                textCode[0] = textCode[0].substring(nextDot + 1);
            }
            else
            {
                return null; //Invalid. Thing ends with a .
            }
        }

        try { //see if it's a field.
            Field f = recursiveGetField(clazz, name);

            if (Modifier.isStatic(f.getModifiers()))
                return new InterpretedResult(f);

            return null;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
    private static InterpretedResult processLenientStaticToken(Class<?> clazz, String[] textCode)
    {
        int nextDot = textCode[0].indexOf('.');
        int nextParentheses = textCode[0].indexOf('(');

        String name;

        if (nextParentheses != -1 && (nextParentheses < nextDot || nextDot == -1)) //this is a function
        {
            name = textCode[0].substring(0, nextParentheses);

            //textCode is trimmed by getParenthesesSection
            return processStaticMethod(clazz, name, getLenientParenthesesSection(textCode, nextParentheses));
        }
        else if (nextDot == -1) //this is the last token (and not a function), so it should just be a field
        {
            name = textCode[0];
            textCode[0] = "";
        }
        else //This is maybe a field?
        {
            name = textCode[0].substring(0, nextDot);
            textCode[0] = textCode[0].substring(Math.min(nextDot + 1, textCode[0].length()));
        }

        try { //see if it's a field.
            Field f = recursiveGetField(clazz, name);

            if (Modifier.isStatic(f.getModifiers()))
                return new InterpretedResult(f);

            return null;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static InterpretedResult processToken(InterpretedResult r, String[] textCode)
    {
        Class<?> clazz = r.getValueClass();

        int nextDot = textCode[0].indexOf('.');
        int nextParentheses = textCode[0].indexOf('(');

        String name;

        if (nextParentheses != -1 && (nextParentheses < nextDot || nextDot == -1)) //this is a function
        {
            name = textCode[0].substring(0, nextParentheses);

            //textCode is trimmed by getParenthesesSection
            return processMethod(r, clazz, name, getParenthesesSection(textCode, nextParentheses));
        }
        else if (nextDot == -1) //this is the last token (and not a function), so it should just be a field
        {
            name = textCode[0];
            textCode[0] = "";
        }
        else //This is just a normal field.
        {
            if (textCode[0].length() > nextDot + 1)
            {
                name = textCode[0].substring(0, nextDot);
                textCode[0] = textCode[0].substring(nextDot + 1);
            }
            else
            {
                return null; //Invalid. Thing ends with a .
            }
        }
        try {
            Field f = recursiveGetField(clazz, name);

            return new InterpretedResult(f, r);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
    private static InterpretedResult processLenientToken(InterpretedResult r, String[] textCode)
    {
        Class<?> clazz = r.getValueClass();

        int nextDot = textCode[0].indexOf('.');
        int nextParentheses = textCode[0].indexOf('(');

        String name;

        if (nextParentheses != -1 && (nextParentheses < nextDot || nextDot == -1)) //this is a function
        {
            name = textCode[0].substring(0, nextParentheses);

            //textCode is trimmed by getParenthesesSection
            return processMethod(r, clazz, name, getLenientParenthesesSection(textCode, nextParentheses));
        }
        else if (nextDot == -1) //this is the last token (and not a function), so it should just be a field
        {
            name = textCode[0];
            textCode[0] = "";
        }
        else //This is just a normal field.
        {
            name = textCode[0].substring(0, nextDot);
            textCode[0] = textCode[0].substring(Math.min(nextDot + 1, textCode[0].length()));
        }
        try {
            Field f = recursiveGetField(clazz, name);

            return new InterpretedResult(f, r);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static InterpretedResult processStaticMethod(Class<?> clazz, String name, String params)
    {
        if (params == null)
            return null;

        try {
            InterpretedResult[] processedParams = processParameters(params);

            if (processedParams == null)
                return null;

            Class<?>[] paramTypes = new Class<?>[processedParams.length];
            for (int i = 0; i < processedParams.length; ++i)
            {
                paramTypes[i] = processedParams[i].getValueClass();
            }

            Method m = thoroughGetMethod(clazz, name, paramTypes);

            if (!Modifier.isStatic(m.getModifiers()))
                return null;

            if (processedParams.length == 0)
            {
                return new InterpretedResult(m);
            }
            else
            {
                return new InterpretedResult(m, processedParams);
            }
        } catch (Exception ignored) {
            return null;
        }
    }

    private static InterpretedResult processMethod(InterpretedResult r, Class<?> clazz, String name, String params)
    {
        try {
            InterpretedResult[] processedParams = processParameters(params);

            if (processedParams == null)
                return null;

            boolean hasPrimitiveWrappers = false;

            Class<?>[] paramTypes = new Class<?>[processedParams.length];
            for (int i = 0; i < processedParams.length; ++i)
            {
                paramTypes[i] = processedParams[i].getValueClass();
                switch (paramTypes[i].getName())
                {
                    case "java.lang.Byte":
                    case "java.lang.Character":
                    case "java.lang.Short":
                    case "java.lang.Integer":
                    case "java.lang.Long":
                    case "java.lang.Float":
                    case "java.lang.Double":
                    case "java.lang.Boolean":
                        hasPrimitiveWrappers = true;
                }
            }

            Method m;

            try
            {
                m = thoroughGetMethod(clazz, name, paramTypes);
            }
            catch (NoSuchMethodException e) {
                if (hasPrimitiveWrappers)
                {
                    //convert parameter classes to primitive types.
                    //Might miss some if they use a weird combination of wrappers and primitive types.
                    for (int i = 0; i < paramTypes.length; ++i)
                    {
                        switch (paramTypes[i].getName())
                        {
                            case "java.lang.Byte":
                                paramTypes[i] = byte.class;
                                break;
                            case "java.lang.Character":
                                paramTypes[i] = char.class;
                                break;
                            case "java.lang.Short":
                                paramTypes[i] = short.class;
                                break;
                            case "java.lang.Integer":
                                paramTypes[i] = int.class;
                                break;
                            case "java.lang.Long":
                                paramTypes[i] = long.class;
                                break;
                            case "java.lang.Float":
                                paramTypes[i] = float.class;
                                break;
                            case "java.lang.Double":
                                paramTypes[i] = double.class;
                                break;
                            case "java.lang.Boolean":
                                paramTypes[i] = boolean.class;
                                break;
                        }
                    }

                    m = thoroughGetMethod(clazz, name, paramTypes);
                }
                else
                {
                    return null;
                }
            }

            if (processedParams.length == 0)
            {
                return new InterpretedResult(m, r);
            }
            else
            {
                return new InterpretedResult(m, r, processedParams);
            }
        } catch (Exception ignored) {
            return null;
        }
    }

    public static InterpretedResult[] processParameters(String params)
    {
        String[] byrefParams = new String[] { params };
        ArrayList<InterpretedResult> paramResults = new ArrayList<>();

        while (byrefParams[0].length() > 0)
        {
            InterpretedResult r = processNextParameter(byrefParams);
            if (r == null)
                return null;

            paramResults.add(r);
        }

        InterpretedResult[] result = new InterpretedResult[paramResults.size()];
        return paramResults.toArray(result);
    }

    public static InterpretedResult processNextParameter(String[] params)
    {
        String parameter = getNextParam(params);

        if (parameter == null || parameter.isEmpty())
            return null;

        //Now have single parameter (and has been removed from parameter string)
        //Now to process this parameter.
        //Accepted values:
        //null
        //boolean (true, false)
        //String ("text")
        //numeric (positive or negative)
        //special defined types with ids
        //statically accessed field
        //statically accessed method that returns a value

        switch (parameter)
        {
            case "null":
                return new InterpretedResult();
            case "true":
                return new InterpretedResult(()->true);
            case "false":
                return new InterpretedResult(()->false);
            default:
                char first = parameter.charAt(0);
                switch (first)
                {
                    case '"': //text constant
                        if (parameter.endsWith("\""))
                        {
                            String s = parameter.substring(1, parameter.length() - 1);
                            return new InterpretedResult(()->s);
                        }
                        else
                        {
                            //starts with quote and doesn't end with quote.
                            //should result in a fail state at the start, though.
                            return null;
                        }
                    case '\'': //char constant
                        if (parameter.endsWith("'") && parameter.length() == 3) {
                            char c = parameter.charAt(1);
                            return new InterpretedResult(()->c);
                        }
                        else {
                            return null;
                        }
                    case '-':
                    case '.':
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        try
                        {
                            if (parameter.contains("."))
                            {
                                if (parameter.endsWith("d"))
                                {
                                    double d = Double.parseDouble(parameter.substring(0, parameter.length() - 1));
                                    return new InterpretedResult(()->d);
                                }
                                else
                                {
                                    float f = Float.parseFloat(parameter);
                                    return new InterpretedResult(()->f);
                                }
                            }
                            else
                            {
                                long l = Long.parseLong(parameter);
                                return new InterpretedResult(()->l);

                            }
                        }
                        catch (NumberFormatException e)
                        {
                            return null;
                        }
                    default: //Check for a special constant thingy, and if that fails... well, time to interpret again.
                        int separator = parameter.indexOf(':');
                        if (separator > 0 && separator + 1 < parameter.length())
                        {
                            String key = parameter.substring(0, separator);
                            ParameterProvider p = mappedProviders.get(key);
                            if (p != null)
                            {
                                return p.provider.apply(parameter.substring(separator + 1));
                            }
                        }
                        //Check for a valid field or method
                        return interpret(parameter); //here we go again
                }
        }
    }

    private static String getParenthesesSection(String[] text, int index)
    {
        ++index;
        int start = index;
        int depth = 1;
        boolean inQuote = false;

        while (depth > 0 && index < text[0].length()) //depth cannot decrease to 0 while inQuote is true, so it doesn't need to be tested
        {
            char next = text[0].charAt(index++);

            switch (next)
            {
                case '(':
                    if (!inQuote)
                        ++depth;
                    break;
                case ')':
                    if (!inQuote)
                        --depth;
                    break;
                case '"':
                    inQuote = !inQuote;
                    break;
            }
        }

        if (depth > 0)
        {
            return null;
        }

        //index is indexof last ) + 1
        int end = index - 1;
        String section = text[0].substring(start, end);

        if (index + 1 > text[0].length())
            text[0] = "";
        else {
            text[0] = text[0].substring(index);
            //There's stuff on the end of this function.
            if (text[0].length() == 1 || (text[0].length() > 1 && text[0].charAt(0) != '.')) {
                text[0] = text[0].substring(1);
                return null; //can't be just one character on here.
            }
            text[0] = text[0].substring(1);
        }

        return section;
    }
    private static String getLenientParenthesesSection(String[] text, int index)
    {
        ++index;
        int start = index;
        int depth = 1;
        boolean inQuote = false;

        while (depth > 0 && index < text[0].length()) //depth cannot decrease to 0 while inQuote is true, so it doesn't need to be tested
        {
            char next = text[0].charAt(index++);

            switch (next)
            {
                case '(':
                    if (!inQuote)
                        ++depth;
                    break;
                case ')':
                    if (!inQuote)
                        --depth;
                    break;
                case '"':
                    inQuote = !inQuote;
                    break;
            }
        }

        if (depth > 0)
        {
            return null;
        }

        //index is indexof last ) + 1
        int end = index - 1;
        String section = text[0].substring(start, end);

        if (index + 1 > text[0].length())
            text[0] = "";
        else {
            text[0] = text[0].substring(index + 1);
        }

        return section;
    }

    public static String getNextParam(String[] params) {
        int depth = 0;
        boolean inQuote = false;
        int index = 0;
        char next = params[0].charAt(0);

        while ((next != ',' || depth > 0 || inQuote) && index < params[0].length())
        {
            next = params[0].charAt(index++);

            switch (next)
            {
                case '(':
                    if (!inQuote)
                        ++depth;
                    break;
                case ')':
                    if (!inQuote)
                        --depth; //if depth is reduced to -1, this *should* be the last parameter.
                    break;
                case '"':
                    inQuote = !inQuote;
                    break;
            }
        }

        if (depth > 0 || inQuote || index == 0)
            return null;

        String parameter = params[0].substring(0, index);
        if (parameter.charAt(index - 1) == ',' || (depth == -1 && parameter.charAt(index - 1) == ')')) //remove comma after parameter or closing parentheses after last parameter
            parameter = parameter.substring(0, parameter.length() - 1);
        parameter = parameter.trim();

        if (index < params[0].length() && depth == 0) {
            params[0] = params[0].substring(index);
        }
        else {
            params[0] = "";
        }

        return parameter;
    }
    public static String getUntrimmedParam(String params) {
        if (params.isEmpty())
            return null;

        int depth = 0, minDepth = 0;
        boolean inQuote = false;
        int index = 0;
        char next = params.charAt(0);

        while ((next != ',' || inQuote) && index < params.length())
        {
            next = params.charAt(index++);

            switch (next)
            {
                case '(':
                    if (!inQuote)
                        ++depth;
                    break;
                case ')':
                    if (!inQuote) {
                        --depth; //if depth is reduced to -1, this *should* be the last parameter.
                        minDepth = Math.min(minDepth, depth);
                    }
                    break;
                case '"':
                    inQuote = !inQuote;
                    break;
            }
        }

        /*if (depth > 0 || inQuote || index == 0) This doesn't need to be a complete or proper parameter, just a single parameter.
            return null;*/
        if (minDepth < -1) { //went too far. -1 is the closing parentheses, this is straight up illegal and cannot be salvaged.
            return null;
        }

        return params.substring(0, index);
    }

    private static Field recursiveGetField(Class<?> clazz, String name) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            Class<?> superclass = clazz.getSuperclass();

            if (superclass == null || superclass.equals(Object.class))
            {
                throw e;
            }

            return recursiveGetField(superclass, name);
        }
    }

    private static Method thoroughGetMethod(Class<?> clazz, String name, Class<?>[] params) throws NoSuchMethodException {
        try {
            Method[] methods = clazz.getDeclaredMethods();
            List<Method> filtered = new ArrayList<>();

            for (Method method : methods) {
                if (method.getName().equals(name))
                    filtered.add(method);
            }

            for (Method m : filtered) {
                if (lazyTestApplicable(m, params))
                    return m;
            }

            //backup plan?
            return clazz.getDeclaredMethod(name, params);
        } catch (NoSuchMethodException e) {
            Class<?> superclass = clazz.getSuperclass();

            if (superclass == null || superclass.equals(Object.class))
            {
                throw e;
            }

            return thoroughGetMethod(superclass, name, params);
        }
    }

    private static boolean lazyTestApplicable(Method m, Class<?>[] params) {
        Class<?>[] methodParams = m.getParameterTypes();

        if (methodParams.length != params.length)
            return false;

        for (int i = 0; i < methodParams.length; ++i) {
            if (!thoroughTestAssignable(methodParams[i], params[i]))
                return false;
        }
        return true;
    }

    public static boolean thoroughTestAssignable(Class<?> target, Class<?> src) {
        if (target == null)
            return false;
        if (src == null) {
            return !target.isPrimitive();
        }
        if (target.isAssignableFrom(src))
            return true;

        switch (target.getName())
        {
            case "java.lang.Byte":
                target = byte.class;
                break;
            case "java.lang.Character":
                target = char.class;
                break;
            case "java.lang.Short":
                target = short.class;
                break;
            case "java.lang.Integer":
                target = int.class;
                break;
            case "java.lang.Long":
                target = long.class;
                break;
            case "java.lang.Float":
                target = float.class;
                break;
            case "java.lang.Double":
                target = double.class;
                break;
            case "java.lang.Boolean":
                target = boolean.class;
                break;
        }

        //The exceptional cases are primitives, which require equality for isAssignableFrom to return true.
        if (target.isPrimitive()) {
            //convert src from primitive wrapper to primitive
            switch (src.getName())
            {
                case "java.lang.Byte":
                    src = byte.class;
                    break;
                case "java.lang.Character":
                    src = char.class;
                    break;
                case "java.lang.Short":
                    src = short.class;
                    break;
                case "java.lang.Integer":
                    src = int.class;
                    break;
                case "java.lang.Long":
                    src = long.class;
                    break;
                case "java.lang.Float":
                    src = float.class;
                    break;
                case "java.lang.Double":
                    src = double.class;
                    break;
                case "java.lang.Boolean":
                    src = boolean.class;
                    break;
            }
            if (!src.isPrimitive()) //can't assign non-primitives to primitives.
                return false;

            if (target == src)
                return true;

            switch (target.getName()) {
                case "boolean":
                case "char":
                    return false;
                //now for all the numerics.
                default:
                    int targetVal = TextCodeInterpreter.lazyNumeric.getOrDefault(target.getName(), -1);
                    int srcVal = TextCodeInterpreter.lazyNumeric.getOrDefault(src.getName(), -1);
                    if (targetVal == -1 || srcVal == -1)
                        return false;

                    return targetVal > srcVal;
            }
        }

        return false;
    }

    //true = only shown if it matches current input
    //false = shown unconditionally
    public static boolean getSuggestions(Class<?> paramType, String base, List<String> suggestions) {
        String chars;
        switch (paramType.getName()) {
            case "java.lang.Character":
            case "char":
                suggestions.addAll(basicCharacters);
                return true;
            case "java.lang.String":
                if (base.isEmpty()) {
                    suggestions.add("\"");
                }
                else if (base.startsWith("\"")) {
                    if (!base.endsWith("\""))
                        suggestions.addAll(prefixedStrings(base, "\"abc"));
                    else
                        suggestions.add(base);
                }
                return true;
            case "java.lang.Byte":
            case "java.lang.Short":
            case "java.lang.Integer":
            case "java.lang.Long":
            case "byte":
            case "short":
            case "int":
            case "long":
                chars = "0123456789";
                if (base.isEmpty())
                    chars += "-";
                if (base.matches("^-?[0123456789]*$"))
                    suggestions.addAll(prefixedStrings(base, chars));
                return false;
            case "java.lang.Float":
            case "java.lang.Double":
            case "float":
            case "double":
                chars = "0123456789";
                if (!base.contains("."))
                    chars += ".";
                if (base.isEmpty())
                    chars += "-";
                if (base.matches("^-?[0123456789]*\\.?[0123456789]*$"))
                    suggestions.addAll(prefixedStrings(base, chars));
                return true;
            case "java.lang.Boolean":
            case "boolean":
                suggestions.add("true");
                suggestions.add("false");
                return true;
            default:
                for (ParameterProvider p : parameterProviders) {
                    if (thoroughTestAssignable(paramType, p.cls)) {
                        String suggestion = p.key + ":";
                        if (base.startsWith(suggestion)) {
                            for (String s : p.getSuggestions()) {
                                suggestions.add(suggestion + s);
                            }
                        }
                        else {
                            suggestions.add(p.key + ":");
                        }
                    }
                }
                if (!base.isEmpty() && "null".startsWith(base)) {
                    suggestions.add("null");
                }
                if (suggestions.isEmpty()) {
                    suggestions.add(paramType.getSimpleName());
                    return false;
                }
                return true;
        }
    }

    private static final Map<String, Integer> lazyNumeric;
    private static final Map<String, Integer> strictNumeric; ///hm guess I don't actually need this
    private static final List<String> basicCharacters;
    private static final List<String> potions;
    static {
        basicCharacters = new ArrayList<>();
        for (String s : "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".split("")) {
            basicCharacters.add("'" + s + "'");
        }

        potions = new ArrayList<>();
        List<String> allPotions = PotionHelper.getPotions(AbstractPlayer.PlayerClass.IRONCLAD, true);
        for (String key : allPotions) {
            potions.add(key.replace(' ', '_'));
        }

        lazyNumeric = new HashMap<>();
        strictNumeric = new HashMap<>();

        lazyNumeric.put("long", 0); //numeric input parameters with no decimals are read as longs
        lazyNumeric.put("byte", 1);
        lazyNumeric.put("short", 1);
        lazyNumeric.put("int", 2);
        lazyNumeric.put("float", 3);
        lazyNumeric.put("double", 4);
        
        strictNumeric.put("byte", 0); //Anything with a lower number can be assigned to a variable with a higher number.
        strictNumeric.put("short", 1); //int -> long int -> float int -> double all fine
        strictNumeric.put("int", 2); //long -> float not fine, long -> double fine
        strictNumeric.put("long", 3); //Same level cannot be assigned.
        strictNumeric.put("float", 3);
        strictNumeric.put("double", 4);
    }
    private static List<String> prefixedStrings(String base, String chars) {
        List<String> result = new ArrayList<>();
        for (char c : chars.toCharArray()) {
            result.add(base + c);
        }
        return result;
    }

    public static class InterpretedResult {
        public enum InterpretedType {
            FIELD,
            METHOD,
            CONSTANT,
            NULL
        }

        public InterpretedType type;

        private Supplier<Object> constValue = null;

        private InterpretedResult src = null;
        private Method method = null;
        private InterpretedResult[] params = null;
        private Field field = null;

        private Class<?> determinedType;

        //constant
        public InterpretedResult()
        {
            type = InterpretedType.NULL;
        }
        public InterpretedResult(Supplier<Object> value)
        {
            type = InterpretedType.CONSTANT;
            constValue = value;
        }

        //static method
        public InterpretedResult(Method m)
        {
            type = InterpretedType.METHOD;
            method = m;
            method.setAccessible(true);

            checkGenerics();
        }
        public InterpretedResult(Method m, InterpretedResult[] params)
        {
            type = InterpretedType.METHOD;
            method = m;
            method.setAccessible(true);
            this.params = params;

            checkGenerics();
        }

        //static field
        public InterpretedResult(Field f)
        {
            type = InterpretedType.FIELD;
            field = f;
            field.setAccessible(true);

            checkGenerics();
        }

        //non-static
        public InterpretedResult(Method m, InterpretedResult src)
        {
            type = InterpretedType.METHOD;
            method = m;
            method.setAccessible(true);
            this.src = src;

            checkGenerics();
        }
        public InterpretedResult(Method m, InterpretedResult src, InterpretedResult[] params)
        {
            type = InterpretedType.METHOD;
            method = m;
            method.setAccessible(true);
            this.params = params;
            this.src = src;

            checkGenerics();
        }
        public InterpretedResult(Field f, InterpretedResult src)
        {
            type = InterpretedType.FIELD;
            field = f;
            field.setAccessible(true);
            this.src = src;

            checkGenerics();
        }

        public String getText() {
            switch (type) {
                case METHOD:
                    return method.getName() + "(";
                case FIELD:
                    return field.getName();
                default:
                    return "";
            }
        }

        public Object evaluate() throws Exception
        {
            Object o = src == null ? null : src.evaluate();
            switch (type)
            {
                case CONSTANT:
                    return constValue.get();
                case METHOD:
                    if (params != null)
                    {
                        Class<?>[] paramTypes = method.getParameterTypes();
                        Object[] paramValues = new Object[params.length];
                        for (int i = 0; i < params.length; ++i)
                        {
                            paramValues[i] = convertAssignable(paramTypes[i], params[i].evaluate());
                        }
                        return method.invoke(o, paramValues);
                    }
                    else
                    {
                        return method.invoke(o);
                    }
                case FIELD:
                    return field.get(o);
                default:
                    return null;
            }
        }

        public void setValue(InterpretedResult value) throws Exception
        {
            if (type != InterpretedType.FIELD)
                return;

            Object o = src == null ? null : src.evaluate();
            Object newValue = value.evaluate();

            try
            {
                Class<?> c = field.getType();

                if (newValue == null)
                {
                    if (c.isPrimitive())
                    {
                        BaseMod.logger.error("unable to set primitive type to null");
                    }
                    else
                    {
                        field.set(o, null);
                    }
                }
                else
                {
                    field.set(o, convertAssignable(c, newValue));
                }
            }
            catch (Exception e)
            {
                BaseMod.logger.error(e.getMessage());
                e.printStackTrace();
            }
        }

        public Class<?> getValueClass()
        {
            if (type == InterpretedType.NULL)
                return null;
            if (determinedType == null) {
                switch (type)
                {
                    case METHOD:
                        if (method != null) {
                            determinedType = method.getReturnType();
                        }
                        break;
                    case FIELD:
                        if (field != null) {
                            determinedType = field.getType();
                        }
                        break;
                    case CONSTANT:
                        if (constValue != null)
                            determinedType = constValue.get().getClass();
                        break;
                    default:
                        break;
                }
            }

            return determinedType;
        }

        public boolean assignableTo(Class<?> paramType) {
            Class<?> c = getValueClass();
            return thoroughTestAssignable(paramType, c);
        }

        private static Object convertAssignable(Class<?> target, Object o) {
            if (target.isAssignableFrom(o.getClass()))
                return o;

            if (!thoroughTestAssignable(target, o.getClass()))
                throw new IllegalArgumentException(o.getClass().getName() + " cannot be assigned to " + target.getName() + ".");

            switch (target.getName())
            {
                case "java.lang.Byte":
                    target = byte.class;
                    break;
                case "java.lang.Character":
                    target = char.class;
                    break;
                case "java.lang.Short":
                    target = short.class;
                    break;
                case "java.lang.Integer":
                    target = int.class;
                    break;
                case "java.lang.Long":
                    target = long.class;
                    break;
                case "java.lang.Float":
                    target = float.class;
                    break;
                case "java.lang.Double":
                    target = double.class;
                    break;
                case "java.lang.Boolean":
                    target = boolean.class;
                    break;
            }

            if (target.isPrimitive()) {
                Class<?> src = o.getClass();
                switch (src.getName())
                {
                    case "java.lang.Byte":
                        src = byte.class;
                        break;
                    case "java.lang.Character":
                        src = char.class;
                        break;
                    case "java.lang.Short":
                        src = short.class;
                        break;
                    case "java.lang.Integer":
                        src = int.class;
                        break;
                    case "java.lang.Long":
                        src = long.class;
                        break;
                    case "java.lang.Float":
                        src = float.class;
                        break;
                    case "java.lang.Double":
                        src = double.class;
                        break;
                    case "java.lang.Boolean":
                        src = boolean.class;
                        break;
                }

                if (!src.isPrimitive())
                    throw new IllegalArgumentException(o.getClass().getName() + " cannot be assigned to " + target.getName() + ".");

                if (target == src)
                    return o;

                switch (target.getName()) {
                    case "boolean":
                    case "char":
                        break;
                    case "byte":
                        if (src == long.class) {
                            if ((long) o <= Byte.MAX_VALUE && (long) o >= Byte.MIN_VALUE) {
                                return ((Long) o).byteValue();
                            }
                            throw new IllegalArgumentException("Value " + o + " is too large for a byte.");
                        }
                        break;
                    case "short":
                        if (src == long.class) {
                            if ((long) o <= Short.MAX_VALUE && (long) o >= Short.MIN_VALUE) {
                                return ((Long) o).shortValue();
                            }
                            throw new IllegalArgumentException("Value " + o + " is too large for a short.");
                        }
                        break;
                    case "int":
                        if (src == long.class) {
                            if ((long) o <= Integer.MAX_VALUE && (long) o >= Integer.MIN_VALUE) {
                                return ((Long) o).intValue();
                            }
                            throw new IllegalArgumentException("Value " + o + " is too large for an int.");
                        }
                        break;
                    case "long":
                        if (src == long.class) {
                            return o;
                        }
                        break;
                    case "float":
                        if (src == long.class) {
                            return ((Long) o).floatValue();
                        }
                        else if (src == float.class) {
                            return o;
                        }
                        break;
                    case "double":
                        if (src == long.class) {
                            return ((Long) o).doubleValue();
                        }
                        else if (src == float.class) {
                            return ((Float) o).doubleValue();
                        }
                        else if (src == double.class) {
                            return o;
                        }
                        break;
                }
            }

            throw new IllegalArgumentException(o.getClass().getName() + " cannot be assigned to " + target.getName() + ".");
        }

        ////AAAAHAHHHHHHHHH GENERICS
        public Class<?>[] getParamTypes(Method m) {
            //This should be a method of the class this returns
            if (genericMap.isEmpty()) //No generic info, just return it.
                return m.getParameterTypes();

            Type[] genericParams = m.getGenericParameterTypes();
            if (genericParams.length == 0) {
                return m.getParameterTypes();
            }

            Class<?>[] resolvedParams = new Class<?>[genericParams.length];
            Class<?>[] params = m.getParameterTypes();

            for (int i = 0; i < genericParams.length; ++i) {
                Class<?> resolved = resolveGenericType(genericParams[i], this, 0);
                resolvedParams[i] = resolved == null ? params[i] : resolved;
            }

            return resolvedParams;
        }

        private Map<String, Class<?>> genericMap = Collections.emptyMap();
        private void checkGenerics() {
            try {
                Type t;
                Class<?> b;
                switch (type) {
                    case NULL:
                    case CONSTANT:
                        return;
                    case FIELD:
                        t = field.getGenericType();
                        b = field.getType();
                        determinedType = resolveGenericType(t, b);
                        break;
                    case METHOD:
                        t = method.getGenericReturnType();
                        b = method.getReturnType();
                        determinedType = resolveGenericType(t, b);
                        break;
                }
            }
            catch (Exception e) {
                genericMap = Collections.emptyMap();
            }
        }

        private Class<?> resolveGenericType(Type t, Class<?> b) {
            return resolveGenericType(t, b, 0);
        }
        private Class<?> resolveGenericType(Type t, Class<?> b, int depth) {
            if (t instanceof ParameterizedType) {
                Type[] paramTypes = ((ParameterizedType) t).getActualTypeArguments();
                TypeVariable<?>[] typeParams = b.getTypeParameters();

                if (typeParams.length > 0 && typeParams.length == paramTypes.length) {
                    if (genericMap == Collections.EMPTY_MAP)
                        genericMap = new HashMap<>();
                    for (int i = 0; i < typeParams.length; ++i) {
                        Class<?> c = resolveGenericType(paramTypes[i], depth + 1);
                        if (c != null)
                            genericMap.put(depth + typeParams[i].getTypeName(), c);
                    }
                }
                return b;
            }
            else if (t instanceof TypeVariable<?>) {
                if (src != null) {
                    return src.genericMap.get(0 + t.getTypeName()); //class or null. Can only refer to depth 0 variables (variables at same definition level)
                }
            }
            else if (t instanceof Class<?>)
                return (Class<?>) t;

            return b;
        }
        private Class<?> resolveGenericType(Type t, int depth) {
            if (t instanceof ParameterizedType) {
                Type[] paramTypes = ((ParameterizedType) t).getActualTypeArguments();
                Type test = ((ParameterizedType) t).getRawType();

                if (test instanceof Class<?>) {
                    TypeVariable<?>[] typeParams = ((Class<?>) test).getTypeParameters();

                    if (typeParams.length > 0 && typeParams.length == paramTypes.length) {
                        if (genericMap == Collections.EMPTY_MAP)
                            genericMap = new HashMap<>();
                        for (int i = 0; i < typeParams.length; ++i) {
                            Class<?> c = resolveGenericType(paramTypes[i], depth + 1);
                            if (c != null)
                                genericMap.put(depth + typeParams[i].getTypeName(), c);
                        }
                    }
                    return (Class<?>) test;
                }
            }
            else if (t instanceof TypeVariable<?>) {
                if (src != null) {
                    return src.genericMap.get(0 + t.getTypeName()); //class or null
                }
            }
            else if (t instanceof Class<?>)
                return (Class<?>) t;

            return null;
        }
        private Class<?> resolveGenericType(Type t, InterpretedResult src, int depth) {
            if (t instanceof ParameterizedType) {
                Type[] paramTypes = ((ParameterizedType) t).getActualTypeArguments();
                Type test = ((ParameterizedType) t).getRawType();

                if (test instanceof Class<?>) {
                    TypeVariable<?>[] typeParams = ((Class<?>) test).getTypeParameters();

                    if (typeParams.length > 0 && typeParams.length == paramTypes.length) {
                        if (genericMap == Collections.EMPTY_MAP)
                            genericMap = new HashMap<>();
                        for (int i = 0; i < typeParams.length; ++i) {
                            Class<?> c = resolveGenericType(paramTypes[i], src, depth + 1);
                            if (c != null)
                                genericMap.put(depth + typeParams[i].getTypeName(), c);
                        }
                    }
                    return (Class<?>) test;
                }
            }
            else if (t instanceof TypeVariable<?>) {
                if (src != null) {
                    return src.genericMap.get(0 + t.getTypeName()); //class or null
                }
            }
            else if (t instanceof Class<?>)
                return (Class<?>) t;

            return null;
        }

        public String toString() {
            Class<?> c = getValueClass();

            if (c != null) {
                return c.getName() + "(" + type.name() + ")";
            }
            else {
                return "null (" + type.name() + ")";
            }
        }
    }
}
