package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;

import java.util.ArrayList;

@SpirePatch(
		clz=AbstractPlayer.class,
		method="initializeStarterRelics",
		paramtypez={
				PlayerClass.class
		}
)
public class PostInitializeStarterRelicsHookSwitch
{
	@SpireInsertPatch(
			rloc=14,
			localvars={"relics"}
	)
    public static void Insert(AbstractPlayer __instance, PlayerClass chosenClass, @ByRef ArrayList<String>[] relics) {
    	BaseMod.publishPostCreateStartingRelics(chosenClass, relics[0]);
    }
}
