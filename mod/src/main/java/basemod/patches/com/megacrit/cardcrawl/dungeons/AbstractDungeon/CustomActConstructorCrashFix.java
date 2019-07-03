package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.customacts.CustomDungeon;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez={
                String.class,
                String.class,
                AbstractPlayer.class,
                ArrayList.class
        }
)
public class CustomActConstructorCrashFix {
    public static SpireReturn Prefix(AbstractDungeon __instance, String a, String levelId, AbstractPlayer p, ArrayList c) {
        //The constructor for AbstractDungeon requires an AbstractPlayer to be present or it crashes.
        if(p == null) {
            return SpireReturn.Return(null);
        } else {
            if(__instance instanceof CustomDungeon) {
                //Important for monster generation.
                ((CustomDungeon)__instance).id = CustomDungeon.datasource.id;
                ((CustomDungeon)__instance).weakpreset = CustomDungeon.datasource.weakpreset;
                ((CustomDungeon)__instance).strongpreset = CustomDungeon.datasource.strongpreset;
                ((CustomDungeon)__instance).elitepreset = CustomDungeon.datasource.elitepreset;
            }
            return SpireReturn.Continue();
        }
    }
}