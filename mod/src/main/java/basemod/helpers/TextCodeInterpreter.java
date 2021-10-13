package basemod.helpers;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class TextCodeInterpreter {
    public static final HashMap<String, Class<?>> mappedAccess;
    static {
        mappedAccess = new HashMap<>();

        mappedAccess.put("AbstractDungeon", AbstractDungeon.class);
        mappedAccess.put("CardCrawlGame", CardCrawlGame.class);
        mappedAccess.put("BaseMod", BaseMod.class);
        mappedAccess.put("Color", Color.class);
    }


    public static HashMap<String, Function<String, InterpretedResult>> constantParameters;
    static {
        constantParameters = new HashMap<>();

        constantParameters.put("card", (id)-> {
            if (CardLibrary.cards.containsKey(id)) {
                return new InterpretedResult(()->CardLibrary.getCopy(id));
            }
            return null;
        });
        constantParameters.put("relic", (id)-> {
            if (RelicLibrary.isARelic(id)) {
                return new InterpretedResult(()->RelicLibrary.getRelic(id));
            }
            return null;
        });
        constantParameters.put("potion", (id)-> {
            if (PotionHelper.isAPotion(id)) {
                return new InterpretedResult(()->PotionHelper.getPotion(id));
            }
            return null;
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
                Object o = next.evaluate();
                next = processToken(o, textCode);

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

            return new InterpretedResult(f);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static InterpretedResult processToken(Object o, String[] textCode)
    {
        Class<?> clazz = o.getClass();

        int nextDot = textCode[0].indexOf('.');
        int nextParentheses = textCode[0].indexOf('(');

        String name;

        if (nextParentheses < nextDot || (nextDot == -1 && nextParentheses != -1)) //this is a function
        {
            name = textCode[0].substring(0, nextParentheses);

            //textCode is trimmed by getParenthesesSection
            return processMethod(o, clazz, name, getParenthesesSection(textCode, nextParentheses));
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

            return new InterpretedResult(f, o);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static InterpretedResult processStaticMethod(Class<?> clazz, String name, String params)
    {
        try {
            InterpretedResult[] processedParams = processParameters(params);

            if (processedParams == null)
                return null;

            Class<?>[] paramTypes = new Class<?>[processedParams.length];
            for (int i = 0; i < processedParams.length; ++i)
            {
                paramTypes[i] = processedParams[i].evaluate().getClass();
            }

            Method m = recursiveGetMethod(clazz, name, paramTypes);

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

    private static InterpretedResult processMethod(Object o, Class<?> clazz, String name, String params)
    {
        try {
            InterpretedResult[] processedParams = processParameters(params);

            if (processedParams == null)
                return null;

            boolean hasPrimitiveWrappers = false;

            Class<?>[] paramTypes = new Class<?>[processedParams.length];
            for (int i = 0; i < processedParams.length; ++i)
            {
                paramTypes[i] = processedParams[i].evaluate().getClass();
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
                m = recursiveGetMethod(clazz, name, paramTypes);
            }
            catch (NoSuchMethodException e) {
                if (hasPrimitiveWrappers)
                {
                    //convert parameter classes to primitive types
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

                    m = recursiveGetMethod(clazz, name, paramTypes);
                }
                else
                {
                    return null;
                }
            }

            if (processedParams.length == 0)
            {
                return new InterpretedResult(m, o);
            }
            else
            {
                return new InterpretedResult(m, o, processedParams);
            }
        } catch (Exception ignored) {
            return null;
        }
    }

    private static InterpretedResult[] processParameters(String params)
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
                        --depth;
                    break;
                case '"':
                    inQuote = !inQuote;
                    break;
            }
        }

        if (depth > 0 || inQuote)
            return null;

        String parameter = params[0].substring(0, index).trim();

        if (index + 1 < params[0].length())
            params[0] = params[0].substring(index + 1);
        else
            params[0] = "";

        //Now have single parameter (and has been removed from parameter string)
        //Now to process this parameter.
        //Accepted values:
        //null
        //boolean (true, false)
        //String ("text")
        //numeric (positive or negative)
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
                    case '-':
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
                                int i = Integer.parseInt(parameter);
                                return new InterpretedResult(()->i);

                            }
                        }
                        catch (NumberFormatException e)
                        {
                            return null;
                        }
                    default:
                        int separator = parameter.indexOf(':');
                        if (separator > 0 && separator + 1 < parameter.length())
                        {
                            String key = parameter.substring(0, separator);
                            Function<String, InterpretedResult> p = constantParameters.get(key);
                            if (p != null)
                            {
                                return p.apply(parameter.substring(separator + 1));
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
        else
            text[0] = text[0].substring(index + 1);

        return section;
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

    private static Method recursiveGetMethod(Class<?> clazz, String name, Class<?>[] params) throws NoSuchMethodException {
        try {
            return clazz.getDeclaredMethod(name, params);
        } catch (NoSuchMethodException e) {
            Class<?> superclass = clazz.getSuperclass();

            if (superclass == null || superclass.equals(Object.class))
            {
                throw e;
            }

            return recursiveGetMethod(superclass, name, params);
        }
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

        private Object o = null;
        private Method method = null;
        private InterpretedResult[] params = null;
        private Field field = null;

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
        }
        public InterpretedResult(Method m, InterpretedResult[] params)
        {
            type = InterpretedType.METHOD;
            method = m;
            method.setAccessible(true);
            this.params = params;
        }

        //static field
        public InterpretedResult(Field f)
        {
            type = InterpretedType.FIELD;
            field = f;
            field.setAccessible(true);
        }

        //non-static
        public InterpretedResult(Method m, Object o)
        {
            type = InterpretedType.METHOD;
            method = m;
            method.setAccessible(true);
            this.o = o;
        }
        public InterpretedResult(Method m, Object o, InterpretedResult[] params)
        {
            type = InterpretedType.METHOD;
            method = m;
            method.setAccessible(true);
            this.params = params;
            this.o = o;
        }
        public InterpretedResult(Field f, Object o)
        {
            type = InterpretedType.FIELD;
            field = f;
            field.setAccessible(true);
            this.o = o;
        }


        public Object evaluate()
        {
            switch (type)
            {
                case CONSTANT:
                    return constValue.get();
                case METHOD:
                    try
                    {
                        if (params != null)
                        {
                            Object[] paramValues = new Object[params.length];
                            for (int i = 0; i < params.length; ++i)
                            {
                                paramValues[i] = params[i].evaluate();
                            }
                            return method.invoke(o, paramValues);
                        }
                        else
                        {
                            return method.invoke(o);
                        }
                    } catch (Exception e) {
                        return null;
                    }
                case FIELD:
                    try
                    {
                        return field.get(o);
                    } catch (Exception e)
                    {
                        return null;
                    }
                default:
                    return null;
            }
        }

        public void setValue(InterpretedResult value)
        {
            if (type != InterpretedType.FIELD)
                return;

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
                    if (c.isAssignableFrom(newValue.getClass()))
                    {
                        field.set(o, newValue);
                    }
                    else
                    {
                        BaseMod.logger.error("unable to assign value of type " + newValue.getClass().getName() + " to " + c.getName());
                    }
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
            switch (type)
            {
                case METHOD:
                    if (method != null)
                        return method.getReturnType();
                    return null;
                case FIELD:
                    if (field != null)
                        return field.getType();
                    return null;
                case CONSTANT:
                    if (constValue != null)
                        return constValue.get().getClass();
                default:
                    return null;
            }
        }
    }
}
