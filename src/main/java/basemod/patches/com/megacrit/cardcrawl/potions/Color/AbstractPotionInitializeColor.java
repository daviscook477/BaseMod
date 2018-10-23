package basemod.patches.com.megacrit.cardcrawl.potions.Color;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.potions.AbstractPotion;

@SpirePatch(
		cls = "com.megacrit.cardcrawl.potions.AbstractPotion",
		method = "initializeColor"
)
public class AbstractPotionInitializeColor {
	public static void Postfix(Object __object__instance) {
		AbstractPotion potion = (AbstractPotion) __object__instance;
		if (BaseMod.getPotionLiquidColor(potion.ID) != null) {
			potion.liquidColor = BaseMod.getPotionLiquidColor(potion.ID);
			potion.hybridColor = BaseMod.getPotionHybridColor(potion.ID);
			potion.spotsColor = BaseMod.getPotionSpotsColor(potion.ID);
		}
	}
} 
