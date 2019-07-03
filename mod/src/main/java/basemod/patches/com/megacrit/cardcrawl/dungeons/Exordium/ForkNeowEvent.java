package basemod.patches.com.megacrit.cardcrawl.dungeons.Exordium;

import basemod.customacts.CustomDungeon;
import basemod.patches.com.megacrit.cardcrawl.ui.buttons.ProceedButton.GoToNextDungeon;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.neow.NeowRoom;
import javassist.CtBehavior;

import java.util.ArrayList;


@SpirePatch(
        clz = Exordium.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez={
                AbstractPlayer.class,
                ArrayList.class
        }
)
public class ForkNeowEvent {

    @SpireInsertPatch(
            locator = Locator.class
    )
    //Sets a forkevent just before the Neow room is created
    public static SpireReturn<Void> Insert(Exordium __instance, AbstractPlayer p, ArrayList bleh) {
        ArrayList<String> availableActs = new ArrayList<>();
        if(CustomDungeon.actnumbers.containsKey(CustomDungeon.EXORDIUM)) {
            for(final String s : CustomDungeon.actnumbers.get(CustomDungeon.EXORDIUM)) {
                CustomDungeon cd = CustomDungeon.dungeons.get(s);
                if(!cd.finalAct) {
                    availableActs.add(s);
                }
            }
        }
        if(!availableActs.isEmpty()) {
            AbstractDungeon.actNum = CustomDungeon.EXORDIUM - 1;
            AbstractDungeon.currMapNode.room = new GoToNextDungeon.ForkEventRoom(null, false);
            AbstractDungeon.currMapNode.room.onPlayerEntry();
            return SpireReturn.Return(null);
        }

        return SpireReturn.Continue();
    }


    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.NewExprMatcher(NeowRoom.class);
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}