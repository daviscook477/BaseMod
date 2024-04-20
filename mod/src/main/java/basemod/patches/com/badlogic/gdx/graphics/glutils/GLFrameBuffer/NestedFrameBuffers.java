package basemod.patches.com.badlogic.gdx.graphics.glutils.GLFrameBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class NestedFrameBuffers
{
	@SpirePatch2(
			clz = GLFrameBuffer.class,
			method = SpirePatch.CLASS
	)
	private static class Fields
	{
		public static SpireField<Integer> prevFBOHandle = new SpireField<>(() -> -1);
		public static SpireField<int[]> prevViewport = new SpireField<>(() -> new int[4]);
		public static SpireField<Boolean> isBound = new SpireField<>(() -> false);
	}

	public static final Logger logger = LogManager.getLogger(NestedFrameBuffers.class.getName());
	private static int depth = 0;
	private static boolean DEBUG = false;

	@SpirePatch2(
			clz = GLFrameBuffer.class,
			method = "begin"
	)
	private static class Begin
	{
		static void Prefix(GLFrameBuffer<?> __instance)
		{
			if (Fields.isBound.get(__instance)) {
				logger.warn("Framebuffer (" + __instance.getFramebufferHandle() + ") is already bound");
				return;
			}
			Fields.isBound.set(__instance, true);

			debugPrint("->", depth, __instance.getFramebufferHandle());
			depth++;

			Fields.prevFBOHandle.set(__instance, getBoundFBOHandle());
			Fields.prevViewport.set(__instance, getViewport());
		}
	}

	@SpirePatch2(
			clz = GLFrameBuffer.class,
			method = "end",
			paramtypez = {}
	)
	private static class End1
	{
		static SpireReturn<Void> Prefix(GLFrameBuffer<?> __instance)
		{
			int[] prevViewport = Fields.prevViewport.get(__instance);
			if (prevViewport[0] == 0 && prevViewport[1] == 0 && prevViewport[2] == 0 && prevViewport[3] == 0) {
				__instance.end(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
			} else {
				__instance.end(prevViewport[0], prevViewport[1], prevViewport[2], prevViewport[3]);
			}
			return SpireReturn.Return();
		}
	}

	@SpirePatch2(
			clz = GLFrameBuffer.class,
			method = "end",
			paramtypez = {int.class, int.class, int.class, int.class}
	)
	private static class End2
	{
		static SpireReturn<Void> Prefix(GLFrameBuffer<?> __instance, int x, int y, int width, int height)
		{
			if (!Fields.isBound.get(__instance)) {
				debugPrint("X-", depth-1, __instance.getFramebufferHandle());
				if (!DEBUG) logger.warn("Framebuffer (" + __instance.getFramebufferHandle() + ") is already unbound");
				return SpireReturn.Return();
			}
			Fields.isBound.set(__instance, false);

			depth--;
			debugPrint("<-", depth, __instance.getFramebufferHandle());

			if (getBoundFBOHandle() != __instance.getFramebufferHandle()) {
				logger.error(
						"The currently bound framebuffer (" +
								getBoundFBOHandle() +
								") doesn't match this one (" +
								__instance.getFramebufferHandle() +
								"). Make sure the nested framebuffers are closed in the same order they were opened in."
				);
			}

			Gdx.gl20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, Fields.prevFBOHandle.get(__instance));
			Gdx.gl20.glViewport(x, y, width, height);

			return SpireReturn.Return();
		}
	}

	private static void debugPrint(String prefix, int depth, int fboHandle)
	{
		if (!DEBUG) return;

		int h = getBoundFBOHandle();
		if (depth > 0) {
			System.out.printf("%" + depth + "s%s%d (%d)\n", "", prefix, fboHandle, h);
		} else {
			System.out.printf("%s%s%d (%d)\n", "", prefix, fboHandle, h);
		}
	}

	@SpirePatch2(
			clz = GLFrameBuffer.class,
			method = "build"
	)
	private static class FixBuildResettingCurrentFrameBuffer
	{
		static void Raw(CtBehavior ctBehavior) throws CannotCompileException
		{
			ctBehavior.addLocalVariable("prevFrameBufferHandle", CtClass.intType);
			ctBehavior.insertBefore("prevFrameBufferHandle = " + NestedFrameBuffers.class.getName() + ".getBoundFBOHandle();");
			ctBehavior.instrument(new ExprEditor() {
				@Override
				public void edit(FieldAccess f) throws CannotCompileException
				{
					if (f.isReader() && f.getClassName().equals(GLFrameBuffer.class.getName()) && f.getFieldName().equals("defaultFramebufferHandle")) {
						f.replace(
								"$_ = prevFrameBufferHandle;"
						);
					}
				}
			});
		}
	}

	private static final IntBuffer INT_BUF = ByteBuffer
			.allocateDirect(16 * Integer.BYTES)
			.order(ByteOrder.nativeOrder())
			.asIntBuffer();

	public static synchronized int getBoundFBOHandle()
	{
		Gdx.gl.glGetIntegerv(GL20.GL_FRAMEBUFFER_BINDING, INT_BUF);
		return INT_BUF.get(0);
	}

	private static synchronized int[] getViewport()
	{
		Gdx.gl.glGetIntegerv(GL20.GL_VIEWPORT, INT_BUF);
		return new int[] { INT_BUF.get(0), INT_BUF.get(1), INT_BUF.get(2), INT_BUF.get(3) };
	}
}
