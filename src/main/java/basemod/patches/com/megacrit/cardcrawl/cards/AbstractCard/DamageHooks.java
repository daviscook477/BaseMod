	package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

	import basemod.BaseMod;
	import com.evacipated.cardcrawl.modthespire.lib.ByRef;
	import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
	import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
	import com.megacrit.cardcrawl.cards.AbstractCard;
	import com.megacrit.cardcrawl.characters.AbstractPlayer;
	import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
	import com.megacrit.cardcrawl.monsters.AbstractMonster;

	import java.util.ArrayList;

public class DamageHooks {

	@SpirePatch(cls="com.megacrit.cardcrawl.cards.AbstractCard", method="calculateCardDamage")
	public static class CalculateCardDamage {

		@SpireInsertPatch(rloc=7, localvars={"tmp"})
		public static void Insert(Object __obj_instance, Object monster, @ByRef float[] tmp) {
			AbstractCard c = (AbstractCard) __obj_instance;
			AbstractMonster mo = (AbstractMonster) monster;
			AbstractPlayer player = AbstractDungeon.player;
			tmp[0] = BaseMod.calculateCardDamage(player, mo, c, tmp[0]);
		}

	}

	@SpirePatch(cls="com.megacrit.cardcrawl.cards.AbstractCard", method="calculateCardDamage")
	public static class CalculateCardDamageMulti {

		@SpireInsertPatch(rloc=86, localvars={"tmp"})
		public static void Insert(Object __obj_instance, Object monster, float[] tmp) {
			AbstractCard c = (AbstractCard) __obj_instance;
			ArrayList<AbstractMonster> monsters = AbstractDungeon.getMonsters().monsters;
			AbstractPlayer player = AbstractDungeon.player;
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = BaseMod.calculateCardDamage(player, monsters.get(i), c, tmp[i]);
			}
		}

	}

	@SpirePatch(cls="com.megacrit.cardcrawl.cards.AbstractCard", method="applyPowers")
	public static class ApplyPowers {

		@SpireInsertPatch(rloc=19, localvars={"tmp"})
		public static void Insert(Object __obj_instance, @ByRef float[] tmp) {
			AbstractCard c = (AbstractCard) __obj_instance;
			AbstractPlayer player = AbstractDungeon.player;
			tmp[0] = BaseMod.calculateCardDamage(player, c, tmp[0]);
		}

	}

	@SpirePatch(cls="com.megacrit.cardcrawl.cards.AbstractCard", method="applyPowers")
	public static class ApplyPowersMulti {

		@SpireInsertPatch(rloc=69, localvars={"tmp"})
		public static void Insert(Object __obj_instance, float[] tmp) {
			AbstractCard c = (AbstractCard) __obj_instance;
			ArrayList<AbstractMonster> monsters = AbstractDungeon.getMonsters().monsters;
			AbstractPlayer player = AbstractDungeon.player;
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = BaseMod.calculateCardDamage(player, monsters.get(i), c, tmp[i]);
			}
		}

	}

}
