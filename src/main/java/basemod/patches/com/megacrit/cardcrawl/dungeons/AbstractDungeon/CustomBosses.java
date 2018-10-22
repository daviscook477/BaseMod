package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;

public class CustomBosses
{
	@SpirePatch(
			clz=AbstractDungeon.class,
			method=SpirePatch.CONSTRUCTOR,
			paramtypez={
					String.class,
					String.class,
					AbstractPlayer.class,
					ArrayList.class
			}
	)
	public static class AddBosses
	{
		public static ExprEditor Instrument()
		{
			return new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException {
					if (m.getMethodName().equals("initializeBoss")) {
						m.replace("{" +
								"$_ = $proceed($$);" +
								"basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.CustomBosses.AddBosses.Do(this);" +
								"}");
					}
				}
			};
		}

		public static void Do(AbstractDungeon dungeon) {
			// Don't add custom bosses if player is still on guaranteed bosses due to not seeing all bosses
			if (AbstractDungeon.bossList.size() == 1) {
				return;
			}

			AbstractDungeon.bossList.addAll(BaseMod.getBossIDs(AbstractDungeon.id));
			Collections.shuffle(AbstractDungeon.bossList, new java.util.Random(AbstractDungeon.monsterRng.randomLong()));
		}
	}

	@SpirePatch(
			clz=AbstractDungeon.class,
			method="setBoss"
	)
	public static class SetBossIcon
	{
		private static final Logger logger = LogManager.getLogger(AbstractDungeon.class.getName());
		public static SpireReturn<Void> Prefix(AbstractDungeon __instance, String key)
		{
			BaseMod.BossInfo bossInfo = BaseMod.getBossInfo(key);
			if (bossInfo != null) {
				AbstractDungeon.bossKey = key;
				DungeonMap.boss = bossInfo.bossMap;
				DungeonMap.bossOutline = bossInfo.bossMapOutline;

				logger.info("[BOSS] " + key);
				return SpireReturn.Return(null);
			}
			return SpireReturn.Continue();
		}
	}

	@SpirePatch(
			clz=ProceedButton.class,
			method="update"
	)
	public static class Ascension20DoubleBoss
	{
		@SpireInsertPatch(
				locator=Locator.class
		)
		public static void Insert(ProceedButton __instance)
		{
			while (AbstractDungeon.bossList.size() > 2) {
				AbstractDungeon.bossList.remove(AbstractDungeon.bossList.size() - 1);
			}
		}

		private static class Locator extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws Exception
			{
				Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "ascensionLevel");
				return LineFinder.findInOrder(ctBehavior, finalMatcher);
			}
		}
	}
}
