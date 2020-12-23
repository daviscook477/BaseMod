package basemod.devcommands.hand;

import basemod.BaseMod;
import basemod.devcommands.ConsoleCommand;
import basemod.DevConsole;
import basemod.helpers.ConvertHelper;

import java.util.Arrays;

public class Hand extends ConsoleCommand {
    public Hand() {
        followup.put("add", HandAdd.class);
        followup.put("remove", HandRemove.class);
        followup.put("discard", HandDiscard.class);
        followup.put("set", HandSet.class);
        requiresPlayer = true;
        simpleCheck = true;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        cmdHandHelp();
    }

    @Override
    public void errorMsg() {
        cmdHandHelp();
    }

    public static int countIndex(String[] tokens) {
        int countIndex = tokens.length - 1;
        while (ConvertHelper.tryParseInt(tokens[countIndex]) != null) {
            countIndex--;
        }
        return countIndex;
    }

    public static String cardName(String[] tokens) {
        return cardName(tokens, countIndex(tokens));
    }
    public static String cardName(String[] tokens, int countIndex) {
        String[] cardNameArray = Arrays.copyOfRange(tokens, 2, countIndex + 1);
        String cardName = String.join(" ", cardNameArray);

        // If the ID was written using underscores, find the original ID
        if (BaseMod.underScoreCardIDs.containsKey(cardName)) {
            cardName = BaseMod.underScoreCardIDs.get(cardName);
        }
        return cardName;
    }

    public static void cmdHandHelp() {
        DevConsole.couldNotParse();
        DevConsole.log("options are:");
        DevConsole.log("* add [id] {count} {upgrade amt}");
        DevConsole.log("* remove [id]");
        DevConsole.log("* remove all");
        DevConsole.log("* discard [id]");
        DevConsole.log("* discard all");
        DevConsole.log("* set damage [id] [amount]");
        DevConsole.log("* set block [id] [amount]");
        DevConsole.log("* set magic [id] [amount]");
        DevConsole.log("* set cost [id] [amount]");
    }
}
