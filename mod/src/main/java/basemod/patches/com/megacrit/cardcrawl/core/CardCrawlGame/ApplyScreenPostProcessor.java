package basemod.patches.com.megacrit.cardcrawl.core.CardCrawlGame;

import basemod.ReflectionHacks;
import basemod.interfaces.ScreenPostProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.List;

@SpirePatch2(
        clz = CardCrawlGame.class,
        method = "render"
)
public class ApplyScreenPostProcessor {
    public static final List<ScreenPostProcessor> postProcessors = new ArrayList<>();

    private static int defaultFramebufferHandle;

    private static FrameBuffer primaryFrameBuffer;
    private static FrameBuffer secondaryFrameBuffer;
    private static TextureRegion primaryFboRegion;
    private static TextureRegion secondaryFboRegion;

    public static Texture getFrameBufferTexture() {
        return primaryFrameBuffer.getColorBufferTexture();
    }

    @SpireInsertPatch(locator = BeginLocator.class)
    public static void BeforeSpriteBatchBegin() {
        if (primaryFrameBuffer == null) {
            initFrameBuffer();
        }

        setDefaultFrameBuffer(primaryFrameBuffer);
        primaryFrameBuffer.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public static class BeginLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(SpriteBatch.class, "begin");
            return LineFinder.findInOrder(ctBehavior, matcher);
        }
    }

    public static void BeforeSpriteBatchEnd(SpriteBatch sb, OrthographicCamera camera) {
        sb.end();
        primaryFrameBuffer.end();

        for (ScreenPostProcessor postProcessor : postProcessors) {
            FrameBuffer tempBuffer = primaryFrameBuffer;
            primaryFrameBuffer = secondaryFrameBuffer;
            secondaryFrameBuffer = tempBuffer;

            TextureRegion tempRegion = primaryFboRegion;
            primaryFboRegion = secondaryFboRegion;
            secondaryFboRegion = tempRegion;

            setDefaultFrameBuffer(primaryFrameBuffer);
            primaryFrameBuffer.begin();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            sb.begin();

            postProcessor.postProcess(sb, secondaryFboRegion, camera);

            sb.end();
            primaryFrameBuffer.end();
        }

        sb.setShader(null);
        Gdx.gl20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, defaultFramebufferHandle);
        sb.begin();
        sb.setColor(Color.WHITE);

        sb.setProjectionMatrix(camera.combined);
        sb.draw(primaryFboRegion, 0, 0, Settings.WIDTH, Settings.HEIGHT);
    }

    private static void initFrameBuffer() {
        defaultFramebufferHandle = ReflectionHacks.<Integer>getPrivateStatic(GLFrameBuffer.class, "defaultFramebufferHandle");

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        primaryFrameBuffer = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
        primaryFboRegion = new TextureRegion(primaryFrameBuffer.getColorBufferTexture());
        primaryFboRegion.flip(false, true);

        secondaryFrameBuffer = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
        secondaryFboRegion = new TextureRegion(secondaryFrameBuffer.getColorBufferTexture());
        secondaryFboRegion.flip(false, true);
    }

    private static void setDefaultFrameBuffer(FrameBuffer fbo) {
        ReflectionHacks.setPrivateStatic(GLFrameBuffer.class, "defaultFramebufferHandle", fbo.getFramebufferHandle());
    }

    @SpirePatch2(
            clz = SpriteBatch.class,
            method = "setupMatrices"
    )
    @SpirePatch2(
            clz = PolygonSpriteBatch.class,
            method = "setupMatrices"
    )
    private static class ShaderScreenSizeUniform {
        private static void Postfix(ShaderProgram ___customShader) {
            if (___customShader != null) {
                ___customShader.setUniformf("u_scale", Settings.scale);
                ___customShader.setUniformf("u_screenSize", Settings.WIDTH, Settings.HEIGHT);
            }
        }
    }
}
