package basemod.devcommands.event;

import basemod.BaseMod;
import basemod.CustomEventRoom;
import basemod.devcommands.ConsoleCommand;
import basemod.DevConsole;
import basemod.ReflectionHacks;
import basemod.eventUtil.EventUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.EventRoom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Event extends ConsoleCommand {

    public Event() {
        requiresPlayer = true;
        minExtraTokens = 1;
        maxExtraTokens = 1;
        simpleCheck = true;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        if (AbstractDungeon.currMapNode == null) {
            DevConsole.log("cannot execute event when there is no map");
            return;
        }

        if (tokens.length == 2 && tokens[1].toLowerCase().equals("random")) {
            RoomEventDialog.optionList.clear();

            MapRoomNode cur = AbstractDungeon.currMapNode;
            MapRoomNode node = new MapRoomNode(cur.x, cur.y);
            node.room = new EventRoom();

            ArrayList<MapEdge> curEdges = cur.getEdges();
            for (MapEdge edge : curEdges) {
                node.addEdge(edge);
            }

            AbstractDungeon.player.releaseCard();
            AbstractDungeon.overlayMenu.hideCombatPanels();
            AbstractDungeon.previousScreen = null;
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.dungeonMapScreen.closeInstantly();
            AbstractDungeon.closeCurrentScreen();
            AbstractDungeon.topPanel.unhoverHitboxes();
            AbstractDungeon.fadeIn();
            AbstractDungeon.effectList.clear();
            AbstractDungeon.topLevelEffects.clear();
            AbstractDungeon.topLevelEffectsQueue.clear();
            AbstractDungeon.effectsQueue.clear();
            AbstractDungeon.dungeonMapScreen.dismissable = true;
            AbstractDungeon.nextRoom = node;
            AbstractDungeon.setCurrMapNode(node);
            AbstractDungeon.getCurrRoom().onPlayerEntry();
            AbstractDungeon.scene.nextRoom(node.room);
            AbstractDungeon.rs = node.room.event instanceof AbstractImageEvent ? AbstractDungeon.RenderScene.EVENT : AbstractDungeon.RenderScene.NORMAL;

            return;
        }

        String[] eventArray = Arrays.copyOfRange(tokens, 1, tokens.length);
        String eventName = String.join(" ", eventArray);

        // If the ID was written using underscores, find the original ID
        if (BaseMod.underScoreEventIDs.containsKey(eventName)) {
            eventName = BaseMod.underScoreEventIDs.get(eventName);
        }

        if (EventHelper.getEvent(eventName) == null) {
            DevConsole.couldNotParse();
            DevConsole.log(eventName + " is not an event ID");
            return;
        }
        
        RoomEventDialog.optionList.clear();

        AbstractDungeon.eventList.add(0, eventName);

        MapRoomNode cur = AbstractDungeon.currMapNode;
        MapRoomNode node = new MapRoomNode(cur.x, cur.y);
        node.room = new CustomEventRoom();

        ArrayList<MapEdge> curEdges = cur.getEdges();
        for (MapEdge edge : curEdges) {
            node.addEdge(edge);
        }

        AbstractDungeon.player.releaseCard();
        AbstractDungeon.overlayMenu.hideCombatPanels();
        AbstractDungeon.previousScreen = null;
        AbstractDungeon.dynamicBanner.hide();
        AbstractDungeon.dungeonMapScreen.closeInstantly();
        AbstractDungeon.closeCurrentScreen();
        AbstractDungeon.topPanel.unhoverHitboxes();
        AbstractDungeon.fadeIn();
        AbstractDungeon.effectList.clear();
        AbstractDungeon.topLevelEffects.clear();
        AbstractDungeon.topLevelEffectsQueue.clear();
        AbstractDungeon.effectsQueue.clear();
        AbstractDungeon.dungeonMapScreen.dismissable = true;
        AbstractDungeon.nextRoom = node;
        AbstractDungeon.setCurrMapNode(node);
        AbstractDungeon.getCurrRoom().onPlayerEntry();
        AbstractDungeon.scene.nextRoom(node.room);
        AbstractDungeon.rs = node.room.event instanceof AbstractImageEvent ? AbstractDungeon.RenderScene.EVENT : AbstractDungeon.RenderScene.NORMAL;
    }

    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> result = new ArrayList<>(EventUtils.eventIDs);

        result.add("random");

        return result;
    }
}
