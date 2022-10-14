package basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.BaseMod;
import basemod.abstracts.CustomScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.options.SettingsScreen;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.apache.logging.log4j.Logger;

public class CustomScreenPatches
{
	@SpirePatch2(
			clz = AbstractDungeon.class,
			method = "openPreviousScreen"
	)
	public static class Reopen
	{
		public static void Prefix(AbstractDungeon.CurrentScreen s)
		{
			CustomScreen screen = BaseMod.getCustomScreen(s);
			if (screen != null) {
				screen.reopen();
			}
		}
	}

	@SpirePatch2(
			clz = AbstractDungeon.class,
			method = "closeCurrentScreen"
	)
	public static class Close
	{
		public static void close()
		{
			CustomScreen screen = BaseMod.getCustomScreen(AbstractDungeon.screen);
			if (screen != null) {
				screen.close();
			} else {
				FixLogging.unknownScreenClose();
			}
		}

		public static ExprEditor Instrument()
		{
			return new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException
				{
					if (m.getClassName().equals(Logger.class.getName()) && m.getMethodName().equals("info")) {
						m.replace(Close.class.getName() + ".close();");
					}
				}
			};
		}
	}

	@SpirePatch2(
			clz = AbstractDungeon.class,
			method = "update"
	)
	public static class Update
	{
		public static void update()
		{
			CustomScreen screen = BaseMod.getCustomScreen(AbstractDungeon.screen);
			if (screen != null) {
				screen.update();
			} else {
				FixLogging.unknownScreenUpdate();
			}
		}

		public static ExprEditor Instrument()
		{
			return new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException
				{
					if (m.getClassName().equals(Logger.class.getName()) && m.getMethodName().equals("info")) {
						m.replace(Update.class.getName() + ".update();");
					}
				}
			};
		}
	}

	@SpirePatch2(
			clz = AbstractDungeon.class,
			method = "render"
	)
	public static class Render
	{
		public static void render(SpriteBatch sb)
		{
			CustomScreen screen = BaseMod.getCustomScreen(AbstractDungeon.screen);
			if (screen != null) {
				screen.render(sb);
			} else {
				FixLogging.unknownScreenRender();
			}
		}

		public static ExprEditor Instrument()
		{
			return new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException
				{
					if (m.getClassName().equals(Logger.class.getName()) && m.getMethodName().equals("info")) {
						m.replace(Render.class.getName() + ".render(sb);");
					}
				}
			};
		}
	}

	@SpirePatch2(
			clz = TopPanel.class,
			method = "updateSettingsButtonLogic"
	)
	public static class SettingsOpen
	{
		@SpireInsertPatch(
				locator = Locator.class
		)
		public static void Insert()
		{
			CustomScreen customScreen = BaseMod.getCustomScreen(AbstractDungeon.screen);
			if (customScreen != null) {
				customScreen.openingSettings();
			}
		}

		private static class Locator extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws Exception
			{
				Matcher matcher = new Matcher.MethodCallMatcher(SettingsScreen.class, "open");
				// last
				int[] found = LineFinder.findAllInOrder(ctBehavior, matcher);
				return new int[]{ found[found.length-1] };
			}
		}
	}

	@SpirePatch2(
			clz = TopPanel.class,
			method = "updateDeckViewButtonLogic"
	)
	public static class DeckOpen
	{
		private static boolean saveHovered = false;
		private static AbstractDungeon.CurrentScreen saveScreen = null;

		@SpireInsertPatch(
				locator = LocatorOpenDeck.class
		)
		public static void InsertOpenDeck()
		{
			CustomScreen customScreen = BaseMod.getCustomScreen(saveScreen);
			if (customScreen != null && customScreen.allowOpenDeck()) {
				customScreen.openingDeck();
				AbstractDungeon.deckViewScreen.open();
			}
		}

		@SpirePrefixPatch
		public static void SaveHovered(Hitbox ___deckHb)
		{
			saveHovered = ___deckHb.hovered;
		}

		@SpireInsertPatch(
				locator = LocatorFixUpdate.class
		)
		public static void InsertFixUpdate(@ByRef boolean[] ___deckButtonDisabled, Hitbox ___deckHb)
		{
			saveScreen = AbstractDungeon.screen;
			CustomScreen customScreen = BaseMod.getCustomScreen(AbstractDungeon.screen);
			if (customScreen != null && ___deckButtonDisabled[0] && customScreen.allowOpenDeck()) {
				___deckButtonDisabled[0] = false;
				___deckHb.hovered = saveHovered;
				___deckHb.update();
			}
		}

		private static class LocatorOpenDeck extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws Exception
			{
				Matcher matcher = new Matcher.FieldAccessMatcher(InputHelper.class, "justClickedLeft");
				// last
				int[] found = LineFinder.findAllInOrder(ctBehavior, matcher);
				return new int[]{ found[found.length-1] };
			}
		}

		private static class LocatorFixUpdate extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws Exception
			{
				Matcher matcher = new Matcher.FieldAccessMatcher(InputHelper.class, "justClickedLeft");
				return LineFinder.findInOrder(ctBehavior, matcher);
			}
		}
	}

	@SpirePatch2(
			clz = TopPanel.class,
			method = "updateMapButtonLogic"
	)
	public static class MapOpen
	{
		private static boolean saveHovered = false;
		private static AbstractDungeon.CurrentScreen saveScreen = null;

		@SpireInsertPatch(
				locator = LocatorOpenMap.class
		)
		public static void Insert()
		{
			CustomScreen customScreen = BaseMod.getCustomScreen(saveScreen);
			if (customScreen != null && customScreen.allowOpenMap()) {
				customScreen.openingMap();
				AbstractDungeon.dungeonMapScreen.open(false);
			}
		}
		@SpirePrefixPatch
		public static void SaveHovered(Hitbox ___mapHb)
		{
			saveHovered = ___mapHb.hovered;
		}

		@SpireInsertPatch(
				locator = LocatorFixUpdate.class
		)
		public static void InsertFixUpdate(@ByRef boolean[] ___mapButtonDisabled, Hitbox ___mapHb)
		{
			saveScreen = AbstractDungeon.screen;
			CustomScreen customScreen = BaseMod.getCustomScreen(AbstractDungeon.screen);
			if (customScreen != null && ___mapButtonDisabled[0] && customScreen.allowOpenMap()) {
				___mapButtonDisabled[0] = false;
				___mapHb.hovered = saveHovered;
				___mapHb.update();
			}
		}

		private static class LocatorOpenMap extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws Exception
			{
				Matcher matcher = new Matcher.FieldAccessMatcher(InputHelper.class, "justClickedLeft");
				// last
				int[] found = LineFinder.findAllInOrder(ctBehavior, matcher);
				return new int[]{ found[found.length-1] };
			}
		}

		private static class LocatorFixUpdate extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws Exception
			{
				Matcher matcher = new Matcher.FieldAccessMatcher(InputHelper.class, "justClickedLeft");
				return LineFinder.findInOrder(ctBehavior, matcher);
			}
		}
	}
}
