package basemod.patches.com.megacrit.cardcrawl.actions.common.VampireDamageAllEnemiesAction;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.unique.VampireDamageAllEnemiesAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CannotCompileException;
import javassist.CtBehavior;


@SpirePatch(
        clz = VampireDamageAllEnemiesAction.class,
        method = "update"
)
public class SafeDamageAllEnemies {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void checkSafety(VampireDamageAllEnemiesAction __instance)
    {
        if (__instance.damage.length != AbstractDungeon.getCurrRoom().monsters.monsters.size()) //There's a problem.
        {
            int[] newDamage = new int[AbstractDungeon.getCurrRoom().monsters.monsters.size()];

            int lastIndex = 0;

            for (; lastIndex < Math.min(newDamage.length, __instance.damage.length); ++lastIndex)
            {
                newDamage[lastIndex] = __instance.damage[lastIndex];
            }
            for (; lastIndex < newDamage.length; ++lastIndex)
            {
                newDamage[lastIndex] = __instance.amount; //amount's value is the value of index 0 of damage array. This could have issues if enemy at index 0 is Vulnerable or something, but this is better than crashing, and doing 0 damage would likely make less sense.
            }

            __instance.damage = newDamage;
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class,"getCurrRoom");

            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
