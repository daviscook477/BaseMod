package basemod.patches.com.megacrit.cardcrawl.rewards;

import basemod.abstracts.CustomReward;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;

// CustomReward calls the default (parameterless) constructor of RewardItem, which is perfectly reasonable except for
// thing: that constructor is normally used for generating card rewards, and it calls AbstractDungeon.getRewardCards().
// This has negative effects: it advances the rare card chance and it makes outcomes change on save/load. It's also
// unnecessary, since custom rewards are going to do their own logic to populate whatever rewards get shown, and don't
// need the card rewards to be populated.
// In theory, this could also be fixed by explicitly calling a different superclass constructor in CustomReward, but all
// the choices have their own quirks. That approach seems more likely to break things and/or take more work than than
// this patch, which simply eliminates the harmful call for anything that's a CustomReward and has been used by a few
// mods for a while now without any issues.
@SpirePatch(clz = RewardItem.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {})
public class AvoidCardGenerationForCustomRewardsPatch {
    public static class AvoidCardGenerationForCustomRewardsPatchExprEditor extends ExprEditor {
        @Override
        public void edit(MethodCall methodCall) throws CannotCompileException {
            if (methodCall.getClassName().equals(AbstractDungeon.class.getName()) && methodCall.getMethodName().equals("getRewardCards")) {
                methodCall.replace(String.format("{ $_ = this instanceof %1$s ? new %2$s() : $proceed($$); }", CustomReward.class.getName(), ArrayList.class.getName()));
            }
        }
    }

    @SpireInstrumentPatch
    public static ExprEditor getAvoidCardGenerationForCustomRewardsPatch() {
        return new AvoidCardGenerationForCustomRewardsPatchExprEditor();
    }
}