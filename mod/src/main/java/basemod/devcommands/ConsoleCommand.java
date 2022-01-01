package basemod.devcommands;

import basemod.BaseMod;
import basemod.devcommands.act.ActCommand;
import basemod.devcommands.blight.Blight;
import basemod.devcommands.clear.Clear;
import basemod.devcommands.debug.Debug;
import basemod.devcommands.deck.Deck;
import basemod.devcommands.draw.Draw;
import basemod.devcommands.energy.Energy;
import basemod.devcommands.event.Event;
import basemod.devcommands.fight.Fight;
import basemod.devcommands.statics.EvalStatic;
import basemod.devcommands.gold.Gold;
import basemod.devcommands.hand.Hand;
import basemod.devcommands.history.History;
import basemod.devcommands.hp.Hp;
import basemod.devcommands.key.KeyCommand;
import basemod.devcommands.kill.Kill;
import basemod.devcommands.maxhp.MaxHp;
import basemod.devcommands.potions.Potions;
import basemod.devcommands.power.Power;
import basemod.devcommands.relic.Relic;
import basemod.devcommands.statics.SetStatic;
import basemod.devcommands.unlock.Unlock;
import basemod.DevConsole;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.util.*;

public abstract class ConsoleCommand {

    protected Map<String, Class<? extends ConsoleCommand>> followup = new HashMap<>();

    protected boolean simpleCheck = false;
    protected boolean requiresPlayer = false;
    protected int minExtraTokens = 0;
    protected int maxExtraTokens = 1;

    protected abstract void execute(String[] tokens, int depth);

    protected ArrayList<String> extraOptions(String[] tokens, int depth) {
        if(followup.isEmpty()) {
            if (tokens.length > depth && tokens[depth].length() > 0) {
                tooManyTokensError();
            } else {
                complete = true;
            }
        }
        return new ArrayList<>();
    }

    protected void errorMsg(String[] tokens) {
        errorMsg();
    }

    protected void errorMsg() {}

    private ConsoleCommand last(String[] tokens, int[] depth, boolean forExecution) throws IllegalAccessException, InstantiationException, InvalidCommandException {
        if (depth[0] < tokens.length - (forExecution ? 0 : 1) && followup.containsKey(tokens[depth[0]].toLowerCase())) {
            ConsoleCommand cc = ((ConsoleCommand) followup.get(tokens[depth[0]].toLowerCase()).newInstance());
            depth[0] = depth[0] + 1;
            return cc.last(tokens, depth, forExecution);
        } else {
            if(requiresPlayer && AbstractDungeon.player == null) {
                if(forExecution) {
                    DevConsole.log("This action can only be executed during a run.");
                } else {
                    duringRun = true;
                }
            } else if(forExecution && tokens.length < depth[0] + minExtraTokens) {
                this.errorMsg();
            } else if(!forExecution && maxExtraTokens > 0 && tokens.length > maxExtraTokens + depth[0] && tokens[depth[0] + maxExtraTokens].length() > 0) {
                tooManyTokensError();
            } else {
                return this;
            }
        }
        throw new InvalidCommandException();
    }

    private ArrayList<String> autocomplete(String[] tokens, int depth) {
        ArrayList<String> result = new ArrayList<>();
        for(final String key : followup.keySet()) {
            if(key.toLowerCase().startsWith(tokens[depth].toLowerCase())) {
                result.add(key);
            }
        }
        ArrayList<String> extras = extraOptions(tokens, depth);
        if(extras != null) {
            for (final String key : extras) {
                if (key.toLowerCase().startsWith(tokens[tokens.length - 1].toLowerCase())) {
                    result.add(key);
                }
            }
        }
        Collections.sort(result);
        return result;
    }


    private static ConsoleCommand getLastCommand(String[] tokens, int[] depth, boolean forExecution) {
        try {
            ConsoleCommand cc = new ConsoleCommand() {
                @Override
                protected void execute(String[] tokens, int depth) {}
            };
            cc.followup = root;
            cc = cc.last(tokens, depth, forExecution);

            return cc;
        } catch(InvalidCommandException ex) {}
        catch(Exception ex) {
            if(forExecution) {
                DevConsole.log("An error occurred.");
            }
            BaseMod.logger.error(ex.getStackTrace());
        }
        return null;
    }

    private static Map<String, Class<? extends ConsoleCommand>> root = new HashMap<>();

    public static void initialize() {
        addCommand("deck", Deck.class);
        addCommand("potion", Potions.class);
        addCommand("blight", Blight.class);
        addCommand("clear", Clear.class);
        addCommand("debug", Debug.class);
        addCommand("draw", Draw.class);
        addCommand("energy", Energy.class);
        addCommand("event", Event.class);
        addCommand("fight", Fight.class);
        addCommand("gold", Gold.class);
        addCommand("hand", Hand.class);
        addCommand("help", Help.class);
        addCommand("hp", Hp.class);
        addCommand("info", Info.class);
        addCommand("kill", Kill.class);
        addCommand("maxhp", MaxHp.class);
        addCommand("power", Power.class);
        addCommand("relic", Relic.class);
        addCommand("unlock", Unlock.class);
        addCommand("history", History.class);
        addCommand("act", ActCommand.class);
        addCommand("key", KeyCommand.class);
        addCommand("setstatic", SetStatic.class);
        addCommand("evalstatic", EvalStatic.class);

        ActCommand.initialize();
    }

    public static Iterator<String> getKeys() {
        return root.keySet().iterator();
    }

    public static void addCommand(String key, Class<? extends ConsoleCommand> val) {
        if(root.containsKey(key)) {
            BaseMod.logger.error("Command \"" + key + "\" already exists.");
        } else if(!key.matches("[a-zA-Z:]+")) {
            BaseMod.logger.error("Commands cannot contain whitespaces.");
        } else {
            root.put(key, val);
        }
    }

    public static void execute(String[] tokens) {
        ConsoleCommand cc;
        int[] depth = new int[]{0};
        if((cc = getLastCommand(tokens, depth, true)) != null) {
            cc.execute(tokens, depth[0]);
        }
    }

    public static boolean complete;
    public static boolean isNumber;
    public static boolean duringRun;
    public static String errormsg;

    public static ArrayList<String> suggestions(String[] tokens) {
        complete = false;
        isNumber = false;
        duringRun = false;
        errormsg = null;
        ConsoleCommand cc;
        int[] depth = new int[]{0};
        if((cc = getLastCommand(tokens, depth, false)) != null) {
            ArrayList<String> result = cc.autocomplete(tokens, depth[0]);

            if(cc.simpleCheck && result.contains(tokens[depth[0]])) {
                result.clear();
                ConsoleCommand.complete = true;
            }

            if(!complete && !duringRun && errormsg == null) {
                return result;
            }
        }
        return new ArrayList<>();
    }

    public static ArrayList<String> smallNumbers() {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            result.add(String.valueOf(i));
        }
        isNumber = true;
        return result;
    }

    public static ArrayList<String> mediumNumbers() {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 10; i <= 90; i += 10) {
            result.add(String.valueOf(i));
            result.add(String.valueOf(i * 10));
        }
        isNumber = true;
        return result;
    }

    public static ArrayList<String> bigNumbers() {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 100; i <= 900; i += 100) {
            result.add(String.valueOf(i));
            result.add(String.valueOf(i * 10));
        }
        isNumber = true;
        return result;
    }

    public static ArrayList<String> getCardOptions() {
        ArrayList<String> result = new ArrayList<>();
        for (String key : CardLibrary.cards.keySet()) {
            result.add(key.replace(' ', '_'));
        }
        return result;
    }

    public static ArrayList<String> getCardOptionsFromCardGroup(CardGroup cg) {
        ArrayList<String> result = new ArrayList<>();
        for (AbstractCard card : cg.group) {
            String cardid = card.cardID.replace(' ', '_');
            if (!result.contains(cardid)) {
                result.add(cardid);
            }
        }
        return result;
    }

    public static ArrayList<String> getRelicOptions() {
        ArrayList<String> result = new ArrayList<>();
        for (String id : BaseMod.listAllRelicIDs()) {
            result.add(id.replace(' ', '_'));
        }
        return result;
    }

    public static void tooManyTokensError() {
        errormsg = "Too many tokens";
    }
}
