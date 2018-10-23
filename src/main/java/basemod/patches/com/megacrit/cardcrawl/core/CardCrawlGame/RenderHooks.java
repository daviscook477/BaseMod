package basemod.patches.com.megacrit.cardcrawl.core.CardCrawlGame;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.DrawMaster;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public class RenderHooks {

	@SpirePatch(cls = "com.megacrit.cardcrawl.core.CardCrawlGame", method = "render")
	public static class RenderHook {

		@SpireInsertPatch(
				locator=Locator.class,
				localvars={ "sb" }
		)
		public static void Insert(Object __obj_instance, SpriteBatch sb) {
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

	@SpirePatch(cls="com.megacrit.cardcrawl.core.CardCrawlGame", method="render")
	public static class PreRenderHook {
		public static final Logger logger = LogManager.getLogger(BaseMod.class.getName());
		
	    public static void Prefix(CardCrawlGame __instance) {
	        Field cameraField;
			try {
				cameraField = CardCrawlGame.class.getDeclaredField("camera");
		        cameraField.setAccessible(true);
		        OrthographicCamera camera = (OrthographicCamera) cameraField.get(__instance);
		        BaseMod.publishPreRender(camera);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				logger.error("could not get camera for render hook ");
				logger.error("error was: " + e.toString());
				e.printStackTrace();
			}

	    }
	}

	@SpirePatch(cls="com.megacrit.cardcrawl.core.CardCrawlGame", method="render")
	public static class PostRenderHook {
	    
		@SpireInsertPatch(
				locator=Locator.class,
				localvars={"sb"}
		)
	    public static void Insert(Object __obj_instance, SpriteBatch sb) {
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
