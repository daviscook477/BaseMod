package basemod.patches.com.megacrit.cardcrawl.cards.CardGroup;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

//Under certain circumstances CardGroup:RefreshHandLayout is called when the player is out of combat which causes a crash when glowCheck calls c.triggerOnGlowCheck() which calls c.canUse() which calls c.cardPlayable(m) whose if statement crashes if monsters is null
@SpirePatch2(clz = CardGroup.class, method = "glowCheck")
public class CardPlayableMonsterNullCrashFix {
    @SpirePrefixPatch
    public static SpireReturn<Void> patch() {
        if(AbstractDungeon.getMonsters() == null) {
            if(Loader.DEBUG) {
                BaseMod.logger.warn("cardPlayable crash has been averted.");
            }
            return SpireReturn.Return();
        }

        return SpireReturn.Continue();
    }
}
