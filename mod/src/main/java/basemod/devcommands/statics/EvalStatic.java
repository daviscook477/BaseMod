package basemod.devcommands.statics;

import basemod.AutoComplete;
import basemod.DevConsole;
import basemod.helpers.TextCodeInterpreter;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

//Invoke a static method and output the result as toString
public class EvalStatic extends StaticsCommand {
    public EvalStatic() {
        minExtraTokens = 1;
        maxExtraTokens = 0;
        simpleCheck = false;
    }

    @Override
    protected ArrayList<String> extraOptions(String[] tokens, int depth) {
        AutoComplete.addWhitespace = false;

        ArrayList<String> options = new ArrayList<>();
        boolean maybeComplete = false;
        if (tokens.length == 2) {
            if (extraOptions("", tokens[1], options))
                maybeComplete = true;
        }
        else if (tokens.length > 2)
            tooManyTokensError();

        if (maybeComplete && options.isEmpty() && errormsg == null)
            complete = true;

        return options;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        try {
            TextCodeInterpreter.InterpretedResult result = TextCodeInterpreter.interpret(tokens[1].replace('\\', ' '));

            if (result == null)
            {
                errorMsg();
                return;
            }


            if (result.type == TextCodeInterpreter.InterpretedResult.InterpretedType.FIELD || result.type == TextCodeInterpreter.InterpretedResult.InterpretedType.METHOD || result.type == TextCodeInterpreter.InterpretedResult.InterpretedType.CONSTANT)
            {
                Object output = result.evaluate();

                if (output == null)
                {
                    DevConsole.log("Output: null");
                }
                else
                {
                    DevConsole.log("Output: " + output);
                }
            }
            else
            {
                DevConsole.log("Target cannot be evaluated");
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
