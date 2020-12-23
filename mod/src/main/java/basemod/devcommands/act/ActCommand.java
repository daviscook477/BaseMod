package basemod.devcommands.act;

import basemod.BaseMod;
import basemod.devcommands.ConsoleCommand;
import basemod.DevConsole;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.screens.DungeonTransitionScreen;
import com.megacrit.cardcrawl.vfx.combat.BattleStartEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActCommand extends ConsoleCommand {

    private static Map<String, Integer> acts = new HashMap<>();
    public static void initialize() {
        addAct(Exordium.ID, 1);
        addAct(TheCity.ID, 2);
        addAct(TheBeyond.ID, 3);
        addAct(TheEnding.ID, 4);

        if(Loader.isModLoaded("theJungle")) {
            addAct("theJungle:Jungle", 2);
        }
    }
    public static void addAct(String actID, int actNum) {
        if(!acts.containsKey(actID) && !actID.equalsIgnoreCase("boss") && !actID.equalsIgnoreCase("num")) {
            acts.put(actID, actNum);
        } else {
            BaseMod.logger.error("Act " + actID + " is already registered!");
        }
    }

    public ActCommand() {
        requiresPlayer = true;
        minExtraTokens = 1;
        simpleCheck = true;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        if(tokens[depth].equalsIgnoreCase("boss")) {
            DevConsole.log("Skipping to bossroom");
            prepareTransition();
            AbstractDungeon.currMapNode.room = new MonsterRoomBoss();
            AbstractDungeon.getCurrRoom().onPlayerEntry();

            AbstractDungeon.rs = AbstractDungeon.RenderScene.NORMAL;

            AbstractDungeon.combatRewardScreen.clear();
            AbstractDungeon.previousScreen = null;
            AbstractDungeon.closeCurrentScreen();

            CardCrawlGame.music.silenceTempBgmInstantly();
            CardCrawlGame.music.silenceBGMInstantly();
        } else if(tokens[depth].equalsIgnoreCase("num")) {
            DevConsole.log("Current Actnumber: " + AbstractDungeon.actNum);
        } else if(acts.containsKey(tokens[depth])) {
            try {
                DevConsole.log("Skipping to act " + tokens[depth]);

                //At 0, it adds the starterdeck each time a dungeon is instantiated.
                //At 1, it adds an ascender's bane when loading exordium.
                if(AbstractDungeon.floorNum <= 1) {
                    AbstractDungeon.floorNum = 2;
                }

                prepareTransition();
                CardCrawlGame.nextDungeon = tokens[depth];
                CardCrawlGame.dungeonTransitionScreen = new DungeonTransitionScreen(tokens[depth]);
                AbstractDungeon.actNum = acts.get(tokens[depth]) - 1;
                //Phase to complete to enable map control, otherwise game effectively softlocks.
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            } catch (Exception ex) {}
        } else {
            DevConsole.log("Invalid Act ID");
        }
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> tmp = new ArrayList<>();
        tmp.add("boss");
        tmp.add("num");
        for(final String s : acts.keySet()) {
            tmp.add(s);
        }
        return tmp;
    }

    private void prepareTransition() {
        AbstractDungeon.player.hand.group.clear();
        AbstractDungeon.actionManager.clear();
        AbstractDungeon.effectsQueue.clear();
        AbstractDungeon.effectList.clear();
        for(int i = AbstractDungeon.topLevelEffects.size() - 1; i > 0; i--) {
            if(AbstractDungeon.topLevelEffects.get(i) instanceof BattleStartEffect) {
                AbstractDungeon.topLevelEffects.remove(i);
            }
        }
    }
}
