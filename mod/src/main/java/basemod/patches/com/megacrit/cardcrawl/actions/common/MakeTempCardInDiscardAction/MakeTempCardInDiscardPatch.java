package basemod.patches.com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Graphics;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import javassist.CannotCompileException;
import javassist.CtBehavior;

@SpirePatch(
        clz = MakeTempCardInDiscardAction.class,
        method = "update"
)
public class MakeTempCardInDiscardPatch {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void Insert(MakeTempCardInDiscardAction __instance, int ___numCards) {
        if(___numCards >= 6 && BaseMod.fixesEnabled) for (int i = 0; i < ___numCards; i++) {
            AbstractCard card = ReflectionHacks.privateMethod(MakeTempCardInDiscardAction.class, "makeNewCard").invoke(__instance);
            AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(card));
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(Graphics.class,"getDeltaTime");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}

