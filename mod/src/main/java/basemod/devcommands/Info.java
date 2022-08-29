package basemod.devcommands;

import basemod.DevConsole;
import com.megacrit.cardcrawl.core.Settings;

import java.util.ArrayList;

public class Info extends ConsoleCommand {
    public Info() {
        minExtraTokens = 0;
        maxExtraTokens = 1;
        simpleCheck = true;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        if (tokens.length < 2) {
            Settings.isInfo = !Settings.isInfo;
            DevConsole.log("Setting info mode to: " + Settings.isInfo);
            return;
        }

        if(tokens[1].equals("true") || tokens[1].equals("false")) {
            try {
                Settings.isInfo = Boolean.parseBoolean(tokens[1]);
                DevConsole.log("Setting info mode to: " + Settings.isInfo);
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
