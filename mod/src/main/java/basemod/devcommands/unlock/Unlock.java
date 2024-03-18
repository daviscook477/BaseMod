package basemod.devcommands.unlock;

import basemod.devcommands.ConsoleCommand;
import basemod.DevConsole;
import basemod.helpers.ConvertHelper;

import java.util.ArrayList;

public class Unlock extends ConsoleCommand {

    public Unlock() {
        minExtraTokens = 1;
        maxExtraTokens = 2;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        if (tokens[1].toLowerCase().equals("always")) {
            DevConsole.forceUnlocks = !DevConsole.forceUnlocks;
        } else if (tokens[1].toLowerCase().equals("level") && tokens.length > 2) {
            DevConsole.unlockLevel = ConvertHelper.tryParseInt(tokens[2]);
        }
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> result = new ArrayList<>();
        result.add("always");
        result.add("level");

        if(tokens[depth].equalsIgnoreCase("always")) {
            if(tokens.length > depth + 1 && tokens[depth + 1].length() > 0) {
                tooManyTokensError();
            } else {
                complete = true;
            }
        } else if(tokens[depth].equalsIgnoreCase("level")) {
            result.clear();
            for (int i = 0; i < 5; i++) {
                result.add(String.valueOf(i));
            }
            if(tokens.length >= 3 && result.contains(tokens[depth + 1])) {
                complete = true;
            }
        } else if(tokens.length > depth + 1) {
            tooManyTokensError();
        }
        return result;
    }
}
