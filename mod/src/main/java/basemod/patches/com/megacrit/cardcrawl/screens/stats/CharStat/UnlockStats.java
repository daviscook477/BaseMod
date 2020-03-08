package basemod.patches.com.megacrit.cardcrawl.screens.stats.CharStat;

import basemod.BaseMod;
import basemod.patches.com.megacrit.cardcrawl.unlock.UnlockTracker.CountModdedUnlockCards;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

@SpirePatch(
        clz = CharStat.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = { AbstractPlayer.class }
)
public class UnlockStats {
    public static void Raw(CtBehavior ctConstructor) throws CannotCompileException {
        ctConstructor.instrument(new ExprEditor() {
            public void edit(FieldAccess f) throws CannotCompileException
            {
                if (f.getClassName().equals("com.megacrit.cardcrawl.unlock.UnlockTracker")
                        && f.getFieldName().equals("lockedRedCardCount"))
                    f.replace("{" +
                            "$_ = basemod.patches.com.megacrit.cardcrawl.screens.stats.CharStat.UnlockStats.getLockedCardCount(c, $proceed());" +
                            "}");
            }
        });
    }

    public static int getLockedCardCount(AbstractPlayer p, int originalValue)
    {
        return CountModdedUnlockCards.getLockedCardCount(p.chosenClass, originalValue);
    }
}
