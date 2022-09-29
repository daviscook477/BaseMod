package basemod.devcommands.kill;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class Kill extends ConsoleCommand {
    public Kill() {
        requiresPlayer = true;
        minExtraTokens = 1;
        maxExtraTokens = 1;
        simpleCheck = true;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        if (tokens[1].equalsIgnoreCase("all")) {
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                AbstractDungeon.actionManager.addToTop(
                        new InstantKillAction(m)
                );
            }
        } else if (tokens[1].equalsIgnoreCase("self")) {
            AbstractDungeon.actionManager
                    .addToTop(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, 999));
        } else {
            cmdKillHelp();
        }
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> result = new ArrayList<>();

        result.add("all");
        result.add("self");

        return result;
    }

    @Override
    public void errorMsg() {
        cmdKillHelp();
    }

    private static void cmdKillHelp() {
        DevConsole.couldNotParse();
        DevConsole.log("options are:");
        DevConsole.log("* all");
        DevConsole.log("* self");
    }
}
