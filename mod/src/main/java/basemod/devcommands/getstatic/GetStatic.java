package basemod.devcommands.getstatic;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.TextCodeInterpreter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class GetStatic extends ConsoleCommand {
    public GetStatic() {
        minExtraTokens = 1;
        maxExtraTokens = 0;
        simpleCheck = false;
    }

    @Override
    protected ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> options = new ArrayList<>();
        if (tokens.length > depth)
        {
            if (depth == 1)
            {
                switch (getTokenCount(tokens[1]))
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
                            for (Method m : methods)
                            {
                                if (Modifier.isStatic(m.getModifiers()))
                                {
                                    String option = baseText + m.getName() + "(";
                                    if (option.startsWith(tokens[1]))
                                    {
                                        if (!options.contains(option))
                                            options.add(option);
                                    }
                                    else if (tokens[1].startsWith(option)) //inside method params
                                    {
                                        //determine method parameters
                                    }
                                }
                            }
                        }
                        for (String s : TextCodeInterpreter.mappedAccess.keySet())
                        {
                            if (s.startsWith(tokens[depth]))
                                options.add(s + ".");
                        }
                        break;
                    default:

                        break;
                }
                //complete = true;
            }
            if (depth == 2)
            {

            }
        }
        return options;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        TextCodeInterpreter.InterpretedResult result = TextCodeInterpreter.interpret(tokens[1].replace('\\', ' '));

        if (result == null)
        {
            errorMsg();
            return;
        }
        if (result.type == TextCodeInterpreter.InterpretedResult.InterpretedType.FIELD || result.type == TextCodeInterpreter.InterpretedResult.InterpretedType.METHOD)
        {
            Object output = result.evaluate();

            if (output == null)
            {
                DevConsole.log("null");
            }
            else
            {
                DevConsole.log(output.toString());
            }
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
