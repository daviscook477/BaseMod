package basemod.patches.com.megacrit.cardcrawl.ui.buttons.ProceedButton;

import basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer.SeenEvents;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;

@SpirePatch(
        clz = ProceedButton.class,
        method = "goToNextDungeon"
)
public class ResetSeenEvents {
    @SpirePrefixPatch
    public static void reset(ProceedButton __instance, AbstractRoom room) {
        if (AbstractDungeon.actNum % 3 == 0) //Reset seen events at the end of every third act for endless
        {
            SeenEvents.seenEvents.get(AbstractDungeon.player).clear();
        }
    }
}
