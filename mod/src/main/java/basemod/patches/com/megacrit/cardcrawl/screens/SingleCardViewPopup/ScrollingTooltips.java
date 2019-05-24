package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ScrollingTooltips
{
	private static class ScrollListener implements ScrollBarListener
	{
		@Override
		public void scrolledUsingBar(float v)
		{
			Fields.scrollTarget = MathHelper.valueFromPercentBetween(Fields.scrollLowerBound, Fields.scrollUpperBound, v);
			UpdateScrollBar.updateBarPosition();
		}
	}

	private static class Fields
	{
		private static ScrollBar scrollBar = new ScrollBar(
				new ScrollListener(),
				1300 * Settings.scale,
				0 * Settings.scale,
				440 * Settings.scale
		) {
			@Override
			public void reset()
			{
				// Disable the reset, otherwise it gets called constantly by us moving the scrollbar
				//super.reset();
			}
		};
		private static float scrollPosition = 0;
		private static float scrollTarget = 0;
		private static float scrollLowerBound = 0;
		private static float scrollUpperBound = 200; // TODO

		private static boolean grabbedScreen = false;
		private static float grabStartY = 0.0f;
	}

	@SpirePatch(
			clz=TipHelper.class,
			method="renderPowerTips"
	)
	public static class RenderScrollingPowerTips
	{
		private static OrthographicCamera camera = null;

		public static void Prefix(float x, @ByRef float[] y, SpriteBatch sb, ArrayList<PowerTip> powerTips)
		{
			if (camera == null) {
				try {
					Field f = CardCrawlGame.class.getDeclaredField("camera");
					f.setAccessible(true);
					camera = (OrthographicCamera) f.get(Gdx.app.getApplicationListener());
				} catch (NoSuchFieldException | IllegalAccessException e) {
					e.printStackTrace();
					return;
				}
			}

			if (IsScrolling.isScrolling.get() > 0) {
				Fields.scrollBar.changeHeight(IsScrolling.isScrolling.get() - 32 * Settings.scale);
				Fields.scrollBar.setCenter(x + 320 * Settings.scale + ScrollBar.TRACK_W / 2, y[0] - IsScrolling.isScrolling.get() / 2 + 32 * Settings.scale);

				sb.flush();
				Rectangle scissors = new Rectangle();
				Rectangle clipBounds = new Rectangle(
						1300 * Settings.scale, y[0] - IsScrolling.isScrolling.get(),
						320 * Settings.scale + ScrollBar.TRACK_W, IsScrolling.isScrolling.get() + 32 * Settings.scale
				);
				ScissorStack.calculateScissors(camera, sb.getTransformMatrix(), clipBounds, scissors);
				ScissorStack.pushScissors(scissors);

				y[0] += Fields.scrollPosition;
			}
		}

		public static void Postfix(float x, float y, SpriteBatch sb, ArrayList<PowerTip> powerTips)
		{
			if (IsScrolling.isScrolling.get() > 0) {
				if (camera != null) {
					Fields.scrollBar.render(sb);

					sb.flush();
					ScissorStack.popScissors();
				}

				IsScrolling.isScrolling.set(-1f);
			}
		}

		@SpireInsertPatch(
				rloc=6,
				localvars={"shift", "y"}
		)
		public static void Insert(float x, float yParam, SpriteBatch sb, ArrayList<PowerTip> powerTips, @ByRef boolean[] shift, @ByRef float[] y)
		{
			if (IsScrolling.isScrolling.get() > 0) {
				shift[0] = true;
				y[0] += Fields.scrollPosition;
			}
		}
	}

	@SpirePatch(
			clz=SingleCardViewPopup.class,
			method="updateInput"
	)
	public static class UpdateScrollBar
	{
		@SpireInsertPatch(
				locator=Locator.class
		)
		public static SpireReturn<Void> Insert(SingleCardViewPopup __instance)
		{
			if (Fields.scrollBar.update()) {
				return SpireReturn.Return(null);
			}

			if (InputHelper.scrolledUp) {
				Fields.scrollTarget -= Settings.SCROLL_SPEED / 2;
			} else if (InputHelper.scrolledDown) {
				Fields.scrollTarget += Settings.SCROLL_SPEED / 2;
			}

			Fields.scrollPosition = MathHelper.scrollSnapLerpSpeed(Fields.scrollPosition, Fields.scrollTarget);

			if (Fields.scrollTarget < 0) {
				Fields.scrollTarget = 0;
			}
			if (Fields.scrollTarget > Fields.scrollUpperBound) {
				Fields.scrollTarget = Fields.scrollUpperBound;
			}
			updateBarPosition();

			return SpireReturn.Continue();
		}

		private static void updateBarPosition()
		{
			float percent = MathHelper.percentFromValueBetween(Fields.scrollLowerBound, Fields.scrollUpperBound, Fields.scrollPosition);
			Fields.scrollBar.parentScrolledToPercent(percent);
		}

		private static class Locator extends SpireInsertLocator
		{
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws Exception
			{
				Matcher matcher = new Matcher.FieldAccessMatcher(InputHelper.class, "justReleasedClickLeft");
				return LineFinder.findInOrder(ctBehavior, matcher);
			}
		}
	}

	private static float BODY_TEXT_WIDTH = -1;
	private static float TIP_DESC_LINE_SPACING;
	private static float BOX_EDGE_H;

	private static float powerTipsHeight(ArrayList<PowerTip> powerTips)
	{
		getSizes();

		float height = 0;

		for (PowerTip tip : powerTips) {
			float textHeight = -FontHelper.getSmartHeight(FontHelper.tipBodyFont, tip.body, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING) - 7 * Settings.scale;
			height += textHeight + BOX_EDGE_H * 3.15f;
		}

		return height;
	}

	private static void getSizes()
	{
		if (BODY_TEXT_WIDTH < 0) {
			BODY_TEXT_WIDTH = (float) ReflectionHacks.getPrivateStatic(TipHelper.class, "BODY_TEXT_WIDTH");
			TIP_DESC_LINE_SPACING = (float) ReflectionHacks.getPrivateStatic(TipHelper.class, "TIP_DESC_LINE_SPACING");
			BOX_EDGE_H = (float) ReflectionHacks.getPrivateStatic(TipHelper.class, "BOX_EDGE_H");
		}
	}

	public static void queueScrollingPowerTips(float x, float y, ArrayList<PowerTip> powerTips, float scrollingSize)
	{
		// Normalize the positions of keyword tooltips on all cards
		// For some reason they move slightly depending on the number of tooltips
		y += powerTips.size() * 20 * Settings.scale;
		y -= 20 * Settings.scale;

		float tipsHeight = powerTipsHeight(powerTips);
		if (tipsHeight > scrollingSize) {
			IsScrolling.isScrolling.set(scrollingSize);
			Fields.scrollUpperBound = tipsHeight - scrollingSize;
		}
		TipHelper.queuePowerTips(x, y, powerTips);
	}

	@SpirePatch(
			clz=TipHelper.class,
			method=SpirePatch.CLASS
	)
	public static class IsScrolling
	{
		public static StaticSpireField<Float> isScrolling = new StaticSpireField<>(() -> -1f);
	}

	@SpirePatch(
			clz=SingleCardViewPopup.class,
			method="renderTips"
	)
	public static class ChangeMethodCall
	{
		public static ExprEditor Instrument()
		{
			return new ExprEditor() {
				@Override
				public void edit(MethodCall m) throws CannotCompileException
				{
					if (m.getMethodName().equals("queuePowerTips")) {
						m.replace(ScrollingTooltips.class.getName() + ".queueScrollingPowerTips($$, $2);");
					}
				}
			};
		}
	}

	@SpirePatch(
			clz=SingleCardViewPopup.class,
			method="open",
			paramtypez={
					AbstractCard.class
			}
	)
	@SpirePatch(
			clz=SingleCardViewPopup.class,
			method="open",
			paramtypez={
					AbstractCard.class,
					CardGroup.class
			}
	)
	public static class ResetScrolling
	{
		public static void MyPrefix()
		{
			Fields.scrollPosition = Fields.scrollTarget = 0;
		}

		public static void Raw(CtBehavior ctBehavior) throws CannotCompileException
		{
			ctBehavior.insertBefore(ResetScrolling.class.getName() + ".MyPrefix();");
		}
	}
}
