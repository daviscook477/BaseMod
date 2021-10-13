package basemod.patches.com.megacrit.cardcrawl.potions;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;

@SpirePatch(
		clz = AbstractPotion.class,
		method = "addToTop"
)
public class FixAddToTop
{
	public static void Replace(AbstractPotion __instance, AbstractGameAction action)
	{
		AbstractDungeon.actionManager.addToTop(action);
	}
}
