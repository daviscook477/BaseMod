package basemod.patches.com.megacrit.cardcrawl.helpers.RelicLibrary;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;
import java.util.Map;

@SpirePatch(
		clz=RelicLibrary.class,
		method="addClassSpecificRelics"
)
public class PopulateLists
{
	public static void Postfix(ArrayList<AbstractRelic> relicPool)
	{
		AbstractPlayer.PlayerClass selection = AbstractDungeon.player.chosenClass;
		if (!BaseMod.isBaseGameCharacter(selection)) {
			for (Map.Entry<String, AbstractRelic> r : BaseMod.getRelicsInCustomPool(
					AbstractDungeon.player.getCardColor()).entrySet()) {
				relicPool.add(r.getValue());
			}
		}
	}
}
