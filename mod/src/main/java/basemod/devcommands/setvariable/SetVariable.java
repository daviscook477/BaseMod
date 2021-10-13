package basemod.devcommands.setvariable;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.TextCodeInterpreter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SetVariable extends ConsoleCommand {
    public SetVariable() {
        minExtraTokens = 2;
        maxExtraTokens = 0;
        simpleCheck = false;
    }

    @Override
    protected ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> options = new ArrayList<>();
        if (tokens.length == 2)
        {
            switch (getTokenCount(tokens[1])) //i don't like this function name, but whatever
            {
                case 0: //first param, must be in mappedAccess
                    for (String s : TextCodeInterpreter.mappedAccess.keySet())
                    {
                        options.add(s + ".");
                    }
                    break;
                case 1: //second param.
                    Class<?> base = TextCodeInterpreter.getBaseClass(tokens[1]);
                    if (base != null)
                    {
                        String baseText = tokens[1].substring(0, tokens[1].indexOf('.')) + ".";
                        //valid options are all static fields and methods of base

                        Field[] fields = base.getDeclaredFields();
                        Method[] methods = base.getDeclaredMethods();

                        for (Field f : fields)
                        {
                            if (Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers()))
                            {
                                //If primitive, no fields/methods, no .
                                String option = baseText + f.getName() + (f.getType().isPrimitive() ? "" : ".");
                                if (option.startsWith(tokens[1]))
                                    options.add(option);
                            }
                        }

                        Map<Class<?>[], String> paramtypeses = new HashMap<>();
                        for (Method m : methods)
                        {
                            //add test: m.getReturnType
                            //if it returns nothing, it cannot be used here
                            //figure out what it returns if void return type
                            Class<?> a = m.getReturnType();
                            if (Modifier.isStatic(m.getModifiers()) && !m.getReturnType().isPrimitive())
                            {
                                Class<?>[] paramtypes = m.getParameterTypes();

                                String option = baseText + m.getName() + "(" + (paramtypes.length == 0 ? ")." : "");
                                if (option.startsWith(tokens[1]))
                                {
                                    if (!options.contains(option))
                                        options.add(option);
                                }
                                else if (tokens[1].startsWith(option) && paramtypes.length > 0) //inside method params
                                {
                                    //determine method parameters
                                    paramtypeses.put(paramtypes, option);
                                }
                            }
                        }

                        for (Map.Entry<Class<?>[], String> params : paramtypeses.entrySet())
                        {
                            //if there's anything in here you're in a method that has parameters
                            if (tokens[1].equals(params.getValue()))
                            {
                                //no arguments entered yet, so it's the first one.
                                Class<?> param = params.getKey()[0];

                                getParameterSuggestions(options, tokens[1], param);
                            }
                            else
                            {
                                String[] temp = new String[] { tokens[1].substring(params.getValue().length()) };

                                Class<?>[] paramtypes = params.getKey();
                                //test if current input parameters match this definition of method.
                                //if they do, have suggestions for last parameter.
                                boolean finished = true;
                                for (int i = 0; i < paramtypes.length; ++i)
                                {
                                    Class<?> paramtype = paramtypes[i];

                                    String lastParam = temp[0];
                                    TextCodeInterpreter.InterpretedResult r = TextCodeInterpreter.processNextParameter(temp);

                                    if (r == null)
                                    {
                                        //not a valid parameter. Assume this means it is in the process of being entered.
                                        finished = i == paramtypes.length - 1; //if this is the last parameter, finished

                                        //Add suggestions for the current parameter.
                                        baseText = tokens[1].substring(0, tokens[1].indexOf(lastParam));
                                        getParameterSuggestions(options, baseText, paramtype);
                                    }
                                    else if (!paramtype.isAssignableFrom(r.getValueClass()))
                                    {
                                        //The current parameters do not support this overload as a valid option.
                                        finished = false;
                                        break;
                                    }
                                    //else: This is a valid parameter. Check the next one.
                                }

                                if (finished) //add ). to the end of all suggestions
                                {

                                }
                            }
                        }
                    }
                    break;
                default:

                    break;
            }
            //complete = true;
        }
        else if (tokens.length > 2)
        {
            TextCodeInterpreter.InterpretedResult r = TextCodeInterpreter.interpret(tokens[1].replace('\\', ' '));

            if (r != null && r.type == TextCodeInterpreter.InterpretedResult.InterpretedType.FIELD)
            {
                Class<?> desiredType = r.getValueClass();

                if (desiredType != null)
                {

                }
            }
        }
        return options;
    }

    private void getParameterSuggestions(ArrayList<String> options, String token, Class<?> param) {
    }

    @Override
    public void execute(String[] tokens, int depth) {
        TextCodeInterpreter.InterpretedResult result = TextCodeInterpreter.interpret(tokens[1].replace('\\', ' '));

        if (result == null)
        {
            errorMsg();
            return;
        }
        if (result.type == TextCodeInterpreter.InterpretedResult.InterpretedType.FIELD)
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < tokens.length; ++i)
            {
                sb.append(tokens[i]).append(" ");
            }

            String value = sb.toString().trim();

            TextCodeInterpreter.InterpretedResult param = TextCodeInterpreter.processNextParameter(new String[] { value });

            if (param == null)
            {
                DevConsole.log("unable to evaluate the value you are attempting to assign");
            }

            result.setValue(param);
        }
        else
        {
            DevConsole.log("target is not a field, and cannot be assigned a value");
        }
    }


    @Override
    public void errorMsg() {
        DevConsole.couldNotParse();
        DevConsole.log("");
    }

    private static int getTokenCount(String arg)
    {
        int index = 0;
        int depth = 0;
        int count = 0;
        boolean inQuote = false;

        while (count < 2 && index < arg.length())
        {
            char next = arg.charAt(index++);

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
                case '.':
                    if (!inQuote && depth == 0)
                        ++count;
                    break;
            }
        }

        return count;
    }
}
