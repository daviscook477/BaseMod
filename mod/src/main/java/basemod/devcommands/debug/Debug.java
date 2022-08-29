package basemod.devcommands.debug;

import basemod.devcommands.ConsoleCommand;
import basemod.DevConsole;
import com.megacrit.cardcrawl.core.Settings;

import java.util.ArrayList;

public class Debug extends ConsoleCommand {
    public Debug() {
        minExtraTokens = 0;
        maxExtraTokens = 1;
        simpleCheck = true;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        if (tokens.length < 2) {
            Settings.isDebug = !Settings.isDebug;
            DevConsole.log("Setting debug mode to: " + Settings.isDebug);
            return;
        }

        if(tokens[1].equals("true") || tokens[1].equals("false")) {
            try {
                Settings.isDebug = Boolean.parseBoolean(tokens[1]);
                DevConsole.log("Setting debug mode to: " + Settings.isDebug);
            } catch(Exception e) {
                errorMsg();
            }
        } else {
            errorMsg();
        }
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> result = new ArrayList<>();

        result.add("true");
        result.add("false");

        return result;
    }

    @Override
    public void errorMsg() {
        DevConsole.couldNotParse();
        DevConsole.log("options are:");
        DevConsole.log("* true");
        DevConsole.log("* false");
    }
}
