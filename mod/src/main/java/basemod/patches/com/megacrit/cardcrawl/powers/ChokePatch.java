package basemod.patches.com.megacrit.cardcrawl.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch;
import com.megacrit.cardcrawl.powers.ChokePower;
import javassist.*;
import javassist.bytecode.DuplicateMemberException;

public class ChokePatch {

    //A patch to fix a basegame bug with choke where applying multiple instances of the power in one turn made the text fail to update on the power's description.

    @SpirePatch2(clz = ChokePower.class, method = SpirePatch.CONSTRUCTOR)
    public static class AddUpdateOverride {
        @SpireRawPatch
        public static void addMethod(CtBehavior ctMethodToPatch) throws CannotCompileException, NotFoundException {
            CtClass ctNestClass = ctMethodToPatch.getDeclaringClass();
            CtClass superClass = ctNestClass.getSuperclass();
            CtMethod superMethod = superClass.getDeclaredMethod("updateDescription");
            CtMethod updateMethod = CtNewMethod.delegator(superMethod, ctNestClass);
            try {
                ctNestClass.addMethod(updateMethod);
            } catch (DuplicateMemberException ignored) {
                updateMethod = ctNestClass.getDeclaredMethod("updateDescription");
            }
            updateMethod.insertAfter(ChokePatch.class.getName()+".fixDesc($0);");
        }
    }

    public static void fixDesc(ChokePower __instance) {
        __instance.description = ChokePower.DESCRIPTIONS[0] + __instance.amount + ChokePower.DESCRIPTIONS[1];
    }

}
