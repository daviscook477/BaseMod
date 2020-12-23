package basemod.devcommands.hand;

import basemod.devcommands.ConsoleCommand;
import basemod.DevConsole;
import basemod.helpers.ConvertHelper;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.util.ArrayList;

public class HandAdd extends ConsoleCommand {
    public HandAdd() {
        requiresPlayer = true;
        minExtraTokens = 1;
        maxExtraTokens = 3;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        int countIndex = Hand.countIndex(tokens);
        String cardName = Hand.cardName(tokens, countIndex);
        AbstractCard c = CardLibrary.getCard(cardName);
        if (c != null) {
            // card count
            int count = 1;
            if (tokens.length > countIndex + 1 && ConvertHelper.tryParseInt(tokens[countIndex + 1]) != null) {
                count = ConvertHelper.tryParseInt(tokens[countIndex + 1], 0);
            }

            int upgradeCount = 0;
            if (tokens.length > countIndex + 2) {
                upgradeCount = ConvertHelper.tryParseInt(tokens[countIndex + 2], 0);
            }

            DevConsole.log("adding " + count + (count == 1 ? " copy of " : " copies of ") + cardName + " with " + upgradeCount + " upgrade(s)");

            for (int i = 0; i < count; i++) {
                AbstractCard copy = c.makeCopy();
                for (int j = 0; j < upgradeCount; j++) {
                    copy.upgrade();
                }

                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(copy, true));
            }
        } else {
            DevConsole.log("could not find card " + cardName);
        }
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> options = ConsoleCommand.getCardOptions();
        if(options.contains(tokens[depth])) { //Input cardID is correct
            if(tokens.length > depth + 1 && tokens[depth + 1].matches("\\d*")) {
                if(tokens.length > depth + 2) {
                    if(tokens[depth + 2].matches("\\d+")) {
                        ConsoleCommand.complete = true;
                    } else if(tokens[depth + 2].length() > 0) {
                        tooManyTokensError();
                    }
                }
                return ConsoleCommand.smallNumbers();
            } else if(tokens.length > depth + 1) {
                tooManyTokensError();
            }
        } else if(tokens.length > depth + 1) {//CardID is not correct, but you're typing more parameters???
            tooManyTokensError();
        }
        return options;
    }

    @Override
    public void errorMsg() {
        Hand.cmdHandHelp();
    }
}
