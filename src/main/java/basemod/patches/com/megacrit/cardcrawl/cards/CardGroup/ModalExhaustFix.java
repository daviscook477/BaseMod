package basemod.patches.com.megacrit.cardcrawl.cards.CardGroup;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import javassist.CannotCompileException;
import javassist.CtBehavior;

@SpirePatch(
		cls="com.megacrit.cardcrawl.cards.CardGroup",
		method="moveToExhaustPile"
)
public class ModalExhaustFix
{
	public static void Raw(CtBehavior ctMethodToPatch) throws CannotCompileException
	{
		String src =
				"if (basemod.BaseMod.modalChoiceScreen.isOpen) {" +
					"basemod.patches.com.megacrit.cardcrawl.cards.CardGroup.ModalExhaustFix.Nested.Delay($0, $1);" +
					"return;" +
				"}";
		ctMethodToPatch.insertBefore(src);
	}

	public static class Nested {
		public static void Delay(CardGroup __instance, AbstractCard card) {
			BaseMod.modalChoiceScreen.delayExhaust(__instance, card);
		}
	}
}
