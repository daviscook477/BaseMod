package basemod.patches.com.megacrit.cardcrawl.core.CardCrawlGame;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.PostRenderEvent;
import basemod6.events.PreRenderEvent;
import basemod6.events.RenderEvent;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.DrawMaster;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.lang.reflect.Field;

public class RenderHooks {

	@SpirePatch(
			clz=CardCrawlGame.class,
			method="render"
	)
	public static class RenderHook {

		@SpireInsertPatch(
				locator=Locator.class,
				localvars={ "sb" }
		)
		public static void Insert(CardCrawlGame __instance, SpriteBatch sb) {
			BaseMod6.EVENT_BUS.post(new RenderEvent(sb));
			BaseMod.publishRender(sb);
		}

		private static class Locator extends SpireInsertLocator
		{
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
			{
				Matcher finalMatcher = new Matcher.MethodCallMatcher(DrawMaster.class.getName(), "draw");
				return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
			}
		}
		
	}

	@SpirePatch(
			clz=CardCrawlGame.class,
			method="render"
	)
	public static class PreRenderHook {
	    public static void Prefix(CardCrawlGame __instance) {
			try {
				Field cameraField = CardCrawlGame.class.getDeclaredField("camera");
		        cameraField.setAccessible(true);
		        OrthographicCamera camera = (OrthographicCamera) cameraField.get(__instance);

		        BaseMod6.EVENT_BUS.post(new PreRenderEvent(camera));
		        BaseMod.publishPreRender(camera);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				BaseMod.logger.error("could not get camera for render hook ");
				BaseMod.logger.error("error was: " + e.toString());
				e.printStackTrace();
			}
	    }
	}

	@SpirePatch(
			clz=CardCrawlGame.class,
			method="render"
	)
	public static class PostRenderHook {
	    
		@SpireInsertPatch(
				locator=Locator.class,
				localvars={"sb"}
		)
	    public static void Insert(CardCrawlGame __instance, SpriteBatch sb) {
			BaseMod6.EVENT_BUS.post(new PostRenderEvent(sb));
	        BaseMod.publishPostRender(sb);
	    }

	    private static class Locator extends SpireInsertLocator
		{
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
			{
				Matcher finalMatcher = new Matcher.MethodCallMatcher(
						SpriteBatch.class.getName(), "end");

				return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
			}
		}
	    
	}

	
}
