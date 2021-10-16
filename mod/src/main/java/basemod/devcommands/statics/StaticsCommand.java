package basemod.devcommands.statics;

import basemod.devcommands.ConsoleCommand;
import basemod.helpers.TextCodeInterpreter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class StaticsCommand extends ConsoleCommand {
    protected boolean extraOptions(String prefix, String token, List<String> options) {
        int tokenCount = getTokenCount(token);
        boolean maybeComplete = false;
        if (tokenCount == 0) {//first param, must be in mappedAccess
            for (String s : TextCodeInterpreter.getBaseOptions()) {
                s = prefix + s + ".";
                if (!options.contains(s))
                    options.add(s);
            }
        } else {
            Class<?> current = TextCodeInterpreter.getBaseClass(token);
            TextCodeInterpreter.InterpretedResult src = null;
            if (current != null) {
                String baseText = removeLastToken(token); //text going to the last parameter with a . after it
                TextCodeInterpreter.InterpretedResult lax = TextCodeInterpreter.lastComplete(baseText);
                TextCodeInterpreter.InterpretedResult strict = TextCodeInterpreter.interpret(token);
                //valid options are all static fields and methods of base

                if (strict != null) {
                    current = strict.getValueClass();
                    src = strict;
                    maybeComplete = true; //there might be other things with longer names that start with the same thing
                    String s = prefix + token + ".";
                    if (!strict.getValueClass().isPrimitive() && !options.contains(s)) {
                        options.add(s);
                    }
                }

                if (lax != null) {
                    current = lax.getValueClass();
                    src = lax;
                }
                else if (strict != null) { //lax and strict both null
                    baseText = token.substring(0, token.indexOf('.') + 1);
                }

                //If this is a complete thing that can be accessed
                //Should be true only for an exactly complete thing, without a .

                Field[] fields = current.getDeclaredFields();
                Method[] methods = current.getDeclaredMethods();

                //Static things are only shown on direct classes
                //Either it's static for the first token, or it's not static for the rest
                //You technically still *can* access these things through the command, but they will not be suggested
                for (Field f : fields) {
                    if (tokenCount >= 2 ^ Modifier.isStatic(f.getModifiers())) {
                        //If primitive, no fields/methods, no .
                        String option = baseText + f.getName() + (f.getType().isPrimitive() ? "" : ".");
                        if (option.startsWith(token)) {
                            if (!options.contains(prefix + option))
                                options.add(prefix + option);
                        }
                    }
                }

                HashMap<String, String> methodErrors = new HashMap<>();
                Set<String> validMethods = new HashSet<>();

                for (Method m : methods) {
                    if (tokenCount >= 2 ^ Modifier.isStatic(m.getModifiers())) {
                        String optionBase = baseText + m.getName() + "(";
                        if (token.startsWith(optionBase)) //inside method params
                        {
                            //determine method parameters
                            List<TextCodeInterpreter.InterpretedResult> processed = new ArrayList<>();
                            String[] params = new String[] { token.substring(optionBase.length()) };
                            String valid = null; //, rough;
                            int nextParam = 0;
                            boolean suggest = params[0].isEmpty(), test = false;

                            while (!suggest) {
                                //rough = params[0];
                                valid = TextCodeInterpreter.getUntrimmedParam(params[0]);
                                if (valid == null) {
                                    //No salvaging this one.
                                    errormsg = "Invalid closing parentheses.";
                                    break;
                                }
                                TextCodeInterpreter.InterpretedResult p = TextCodeInterpreter.processNextParameter(params);
                                if (p != null && valid.length() > 0) { //this is a complete parameter
                                    if (!params[0].isEmpty() || (valid.charAt(valid.length() - 1) == ',')) {
                                        processed.add(p);
                                        optionBase += valid;
                                        if (valid.charAt(valid.length() - 1) == ',')
                                            ++nextParam;

                                        valid = params[0];
                                        if (params[0].isEmpty()) {
                                            suggest = true;
                                        }
                                    } //params[0] must be empty, no data left
                                    else if (valid.charAt(valid.length() - 1) == ')') {
                                        //DONE, no suggestions other than a .
                                        processed.add(p);
                                        optionBase += valid;

                                        if (!m.getReturnType().isPrimitive()) {
                                            String suggestion = prefix + optionBase + ".";
                                            if (!options.contains(suggestion))
                                                options.add(suggestion);
                                        }

                                        test = true;
                                        break;
                                    }
                                    else { //This is a valid parameter but also not necessarily complete, so suggest on it
                                        processed.add(p);
                                        suggest = true;
                                    }
                                }
                                else {
                                    //In-Progress/failed parameter.
                                    suggest = true;
                                }
                            }

                            suggesting: if (suggest) {
                                if (valid == null)
                                    valid = "";

                                //DevConsole.logger.info("Method suggestion base: " + optionBase + " | " + valid);

                                Class<?>[] paramTypes;
                                if (src != null) {
                                    paramTypes = src.getParamTypes(m);
                                }
                                else {
                                    paramTypes = m.getParameterTypes();
                                }

                                if (paramTypes.length == 0 && nextParam == 0) {
                                    if (valid.isEmpty()) {
                                        options.add(prefix + optionBase + ")");
                                    }
                                    else if (valid.equals(")")) {
                                        if (m.getReturnType().isPrimitive()) {
                                            maybeComplete = true;
                                        }
                                        else {
                                            options.add(prefix + optionBase + ").");
                                        }
                                    }
                                    else {
                                        methodErrors.put(m.getName(), "Too many parameters.");
                                    }
                                }
                                else if (nextParam >= paramTypes.length) {
                                    //Too many commas.
                                    methodErrors.put(m.getName(), "Too many parameters.");
                                }
                                else {
                                    for (int i = 0; i < nextParam; ++i) { //are preceding completed parameters valid
                                        if (!processed.get(i).assignableTo(paramTypes[i])) {
                                            methodErrors.put(m.getName(), "Cannot assign " + processed.get(i) + " to " + paramTypes[i].getSimpleName());
                                            break suggesting;
                                        }
                                    }

                                    //get suggestions based on next parameter type and add to options if they match current leftover input
                                    validMethods.add(m.getName());

                                    List<String> suggestions = new ArrayList<>();
                                    if (TextCodeInterpreter.getSuggestions(paramTypes[nextParam], valid, suggestions)) {
                                        for (String suggestion : suggestions) {
                                            suggestion = optionBase + suggestion;
                                            if (suggestion.startsWith(token) && !options.contains(prefix + suggestion)) {
                                                options.add(prefix + suggestion);
                                            }
                                        }
                                    }
                                    else {
                                        for (String suggestion : suggestions) {
                                            suggestion = prefix + optionBase + suggestion;
                                            if (!options.contains(suggestion)) {
                                                options.add(suggestion);
                                            }
                                        }
                                    }

                                    if (processed.size() > nextParam) {
                                        //this current wip parameter is also a valid parameter
                                        if (processed.get(processed.size() - 1).assignableTo(paramTypes[processed.size() - 1])) {
                                            //Type of this last parameter is valid
                                            String suggestion = prefix + optionBase + valid + (processed.size() == paramTypes.length ? ")" : ",");
                                            if (!options.contains(suggestion)) //Suggest end of parameter
                                                options.add(suggestion);
                                        }
                                    }

                                    //Suggest.
                                    if (!valid.isEmpty())
                                        extraOptions(prefix + optionBase, valid, options);
                                }
                            }

                            if (test) {
                                //test to see if parameters are valid. If they are not, error message.
                                Class<?>[] paramTypes = m.getParameterTypes();

                                if (processed.size() > paramTypes.length) {
                                    methodErrors.put(m.getName(), "Too many parameters.");
                                }
                                else if (processed.size() < paramTypes.length) {
                                    if (!methodErrors.containsKey(m.getName())) //low priority
                                        methodErrors.put(m.getName(), "Not enough parameters.");
                                }
                                else {
                                    for (int i = 0; i < processed.size(); ++i) {
                                        if (!processed.get(i).assignableTo(paramTypes[i])) {
                                            methodErrors.put(m.getName(), "Cannot assign " + processed.get(i) + " to " + paramTypes[i].getSimpleName());
                                            break;
                                        }
                                    }
                                    validMethods.add(m.getName());
                                    maybeComplete = true;
                                }
                            }
                        }
                        else if (optionBase.startsWith(token)) {
                            if (!options.contains(prefix + optionBase))
                                options.add(prefix + optionBase);
                        }
                    }
                }

                if (validMethods.isEmpty()) {
                    for (String msg : methodErrors.values()) {
                        errormsg = msg;
                        break;
                    }
                }
            }
        }

        return maybeComplete;
    }

    private static int getTokenCount(String arg)
    {
        int index = 0;
        int depth = 0;
        int count = 0;
        boolean inQuote = false;
        boolean increased = false;

        while (count < 2 && depth >= 0 && index < arg.length())
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
                    if (increased) {
                        //second . in a row. The rest is invalid.
                        return count;
                    }
                    if (!inQuote && depth == 0) {
                        ++count;
                        increased = true;
                    }
                    continue;
            }
            increased = false;
        }

        return count;
    }
    private static String removeLastToken(String text) {
        StringBuilder token = new StringBuilder();
        List<String> tokens = new ArrayList<>();

        int index = 0;
        int depth = 0;
        boolean inQuote = false;

        while (depth >= 0 && index < text.length())
        {
            char next = text.charAt(index++);
            token.append(next);

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
                    if (token.length() == 1) // .. invalid
                        depth = -1;
                    else if (!inQuote && depth == 0) {
                        tokens.add(token.toString());
                        token.setLength(0);
                    }
                    break;
            }
        }

        if (token.length() > 0) {
            token.setLength(0);
            for (String s : tokens) {
                token.append(s); //append all complete tokens and ignore the incomplete/final one
            }
        }
        else {
            token.setLength(0);
            if (text.charAt(text.length() - 1) == '.') {
                //properly ending with a . means there's nothing to clear, and this is the base
                for (String s : tokens) token.append(s);
            }
            else {
                //no pending token, just ignore last token in tokens
                for (int i = 0; i < tokens.size() - 1; ++i)
                    token.append(tokens.get(i));
            }
        }

        return token.toString();
    }
}
