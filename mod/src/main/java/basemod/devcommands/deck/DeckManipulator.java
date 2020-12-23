package basemod.devcommands.deck;

import basemod.BaseMod;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.ConvertHelper;

import java.util.Arrays;

public abstract class DeckManipulator extends ConsoleCommand {

    public DeckManipulator() {
        requiresPlayer = true;
        minExtraTokens = 1;
    }

    protected String getCardID(String[] tokens) {
        return getCardID(tokens, countIndex(tokens));
    }
    protected String getCardID(String[] tokens, int countIndex) {

        String[] cardNameArray = Arrays.copyOfRange(tokens, 2, countIndex + 1);
        String cardName = String.join(" ", cardNameArray);

        // If the ID was written using underscores, find the original ID
        if (BaseMod.underScoreCardIDs.containsKey(cardName)) {
            cardName = BaseMod.underScoreCardIDs.get(cardName);
        }
        return cardName;
    }

    protected int countIndex(String[] tokens) {
        int countIndex = tokens.length - 1;
        while (ConvertHelper.tryParseInt(tokens[countIndex]) != null) {
            countIndex--;
        }
        return countIndex;
    }

    @Override
    public void errorMsg() {
        Deck.cmdDeckHelp();
    }
}
