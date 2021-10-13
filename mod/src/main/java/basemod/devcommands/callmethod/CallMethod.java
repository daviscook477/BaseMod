package basemod.devcommands.callmethod;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.TextCodeInterpreter;

import java.util.ArrayList;

public class CallMethod extends ConsoleCommand {
    public CallMethod() {
        minExtraTokens = 1;
        maxExtraTokens = 0;
        simpleCheck = false;
    }

    @Override
    protected ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> options = new ArrayList<>();
        if (tokens.length > depth)
        {
            if (depth == 2)
            {

            }
            for (String s : TextCodeInterpreter.mappedAccess.keySet())
            {
                if (s.startsWith(tokens[depth]))
                    options.add(s + ".");
            }
            //complete = true;
        }
        return options;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        StringBuilder code = new StringBuilder();
        for (int i = 1; i < tokens.length; ++i)
        {
            code.append(tokens[i]).append(" ");
        }
        TextCodeInterpreter.InterpretedResult result = TextCodeInterpreter.interpret(code.toString().trim());

        if (result == null)
        {
            errorMsg();
            return;
        }
        if (result.type == TextCodeInterpreter.InterpretedResult.InterpretedType.METHOD)
        {
            result.evaluate();
        }
        else
        {
            DevConsole.log("target is not a method, and cannot be called");
        }
    }


    @Override
    public void errorMsg() {
        DevConsole.couldNotParse();
        DevConsole.log("");
    }
}
