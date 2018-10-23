package basemod.patches.com.megacrit.cardcrawl.ui.panels.PotionPopUp;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.potions.AbstractPotion;

@SpirePatch(cls="com.megacrit.cardcrawl.ui.panels.PotionPopUp", method="updateInput")
public class PrePotionUseHook {
	@SpireInsertPatch(rloc=18,localvars= {"potion"})
	public static void Insert(Object __obj_instance, Object potion) {
		AbstractPotion p = (AbstractPotion)potion;
		BaseMod.publishPrePotionUse(p);;
	}
	
}
