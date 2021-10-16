package basemod.devcommands.statics;

import basemod.AutoComplete;
import basemod.DevConsole;
import basemod.helpers.TextCodeInterpreter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class SetStatic extends StaticsCommand {
    public SetStatic() {
        minExtraTokens = 2;
        maxExtraTokens = 0;
        simpleCheck = false;
    }

    @Override
    protected ArrayList<String> extraOptions(String[] tokens, int depth) {
        AutoComplete.addWhitespace = false;

        ArrayList<String> options = new ArrayList<>();
        boolean maybeComplete = false;
        if (tokens.length == 2) {
            if (extraOptions("", tokens[1], options)) {
                AutoComplete.addWhitespace = true;
            }
        }
        else if (tokens.length == 3)
        {
            TextCodeInterpreter.InterpretedResult r = TextCodeInterpreter.interpret(tokens[1].replace('\\', ' '));

            if (r != null && r.type == TextCodeInterpreter.InterpretedResult.InterpretedType.FIELD)
            {
                Class<?> desiredType = r.getValueClass();

                if (desiredType != null)
                {
                    List<String> suggestions = new ArrayList<>();
                    if (TextCodeInterpreter.getSuggestions(desiredType, tokens[2], suggestions)) {
                        for (String suggestion : suggestions) {
                            if (suggestion.startsWith(tokens[2]) && !options.contains(suggestion)) {
                                options.add(suggestion);
                            }
                        }
                    }
                    else {
                        for (String suggestion : suggestions) {
                            if (!options.contains(suggestion)) {
                                options.add(suggestion);
                            }
                        }
                    }
                    if (!tokens[2].isEmpty())
                        extraOptions("", tokens[2], options);

                    TextCodeInterpreter.InterpretedResult[] val = TextCodeInterpreter.processParameters(tokens[2]);

                    if (val != null && val.length == 1) {
                        if (TextCodeInterpreter.thoroughTestAssignable(desiredType, val[0].getValueClass()))
                            maybeComplete = true;
                        else
                            errormsg = "Cannot assign " + val[0] + " to " + desiredType.getSimpleName();
                    }
                }
                else {
                    errormsg = "Invalid target.";
                }
            }
            else if (r != null) {
                errormsg = "Cannot assign a value to a " + r.type.name() + ".";
            }
            else {
                errormsg = "Invalid target.";
            }
        }
        else if (tokens.length > 3)
            tooManyTokensError();

        if (tokens.length == 3 && maybeComplete && (options.isEmpty() || (options.size() == 1 && options.get(0).equals(tokens[2]))) && errormsg == null)
            complete = true;

        return options;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        try {
            TextCodeInterpreter.InterpretedResult result = TextCodeInterpreter.interpret(tokens[1].replace('\\', ' '));

            if (result == null) {
                DevConsole.log("Invalid target.");
                DevConsole.log("");
                return;
            }

            if (result.type == TextCodeInterpreter.InterpretedResult.InterpretedType.FIELD) {
                if (result.getValueClass() != null) {
                    TextCodeInterpreter.InterpretedResult[] val = TextCodeInterpreter.processParameters(tokens[2]);

                    if (val != null && val.length == 1) {
                        if (TextCodeInterpreter.thoroughTestAssignable(result.getValueClass(), val[0].getValueClass())) {
                            result.setValue(val[0]);
                            DevConsole.log("Success.");
                        } else {
                            DevConsole.log("Cannot assign " + val[0] + " to " + result);
                        }
                    } else if (val != null) {
                        DevConsole.log("Too many parameters.");
                        DevConsole.log("");
                    } else {
                        DevConsole.log("Invalid value.");
                        DevConsole.log("");
                    }

                } else {
                    DevConsole.log("Invalid target.");
                    DevConsole.log("");
                }
            } else {
                DevConsole.log("Target is not a field, and cannot be assigned a value");
                DevConsole.log("");
            }
        }
        catch (InvocationTargetException e) {
            Throwable t = e;
            while (t instanceof InvocationTargetException) {
                t = t.getCause();
            }
            if (t == null) {
                DevConsole.log("Exception: " + e);
            }
            else {
                DevConsole.log("Exception: " + t);
            }
            DevConsole.log("");
        }
        catch (Exception e) {
            DevConsole.log("Exception: " + e);
            DevConsole.log("");
        }
    }

    @Override
    public void errorMsg() {
        DevConsole.couldNotParse();
        DevConsole.log("");
    }
}
