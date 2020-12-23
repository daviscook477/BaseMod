package basemod.devcommands.deck;

import basemod.devcommands.ConsoleCommand;
import basemod.DevConsole;
import basemod.helpers.ConvertHelper;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;

public class DeckAdd extends DeckManipulator {

    public DeckAdd() {
        maxExtraTokens = 3;
    }

    public void execute(String[] tokens, int depth) {
        int countIndex = countIndex(tokens);
        String cardName = getCardID(tokens, countIndex);

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

                UnlockTracker.markCardAsSeen(copy.cardID);
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(copy, Settings.WIDTH / 2.0f,
                        Settings.HEIGHT / 2.0f));
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
}
