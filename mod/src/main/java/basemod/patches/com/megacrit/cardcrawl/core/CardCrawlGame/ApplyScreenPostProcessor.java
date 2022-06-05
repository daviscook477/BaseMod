package basemod.patches.com.megacrit.cardcrawl.core.CardCrawlGame;

import basemod.interfaces.ScreenPostProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.List;

@SpirePatch(clz = CardCrawlGame.class, method = "render")
public class ApplyScreenPostProcessor {
    public static final List<ScreenPostProcessor> postProcessors = new ArrayList<>();

    private static FrameBuffer primaryFrameBuffer;
    private static FrameBuffer secondaryFrameBuffer;
    private static TextureRegion primaryFboRegion;
    private static TextureRegion secondaryFboRegion;
    private static boolean usingFrameBuffer = false;

    @SpireInsertPatch(locator = BeginLocator.class)
    public static void BeforeSpriteBatchBegin(CardCrawlGame __instance) {
        if (!postProcessors.isEmpty()) {
            if (primaryFrameBuffer == null) {
                initFrameBuffer();
            }

            usingFrameBuffer = true;
            primaryFrameBuffer.begin();
        }
    }

    public static class BeginLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(SpriteBatch.class, "begin");
            return LineFinder.findInOrder(ctBehavior, matcher);
        }
    }

    public static void BeforeSpriteBatchEnd(SpriteBatch sb, OrthographicCamera camera) {
        if (!usingFrameBuffer) {
            return;
        }

        sb.end();
        primaryFrameBuffer.end();

        for (ScreenPostProcessor postProcessor : postProcessors) {
            FrameBuffer tempBuffer = primaryFrameBuffer;
            primaryFrameBuffer = secondaryFrameBuffer;
            secondaryFrameBuffer = tempBuffer;

            TextureRegion tempRegion = primaryFboRegion;
            primaryFboRegion = secondaryFboRegion;
            secondaryFboRegion = tempRegion;

            primaryFrameBuffer.begin();
            sb.begin();

            postProcessor.postProcess(sb, secondaryFboRegion, camera);

            sb.end();
            primaryFrameBuffer.end();
        }

        sb.setShader(null);
        sb.begin();
        sb.setColor(Color.WHITE);

        sb.setProjectionMatrix(camera.combined);
        sb.draw(primaryFboRegion, 0, 0, Settings.WIDTH, Settings.HEIGHT);

        usingFrameBuffer = false;
    }

    private static void initFrameBuffer() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        primaryFrameBuffer = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
        primaryFboRegion = new TextureRegion(primaryFrameBuffer.getColorBufferTexture());
        primaryFboRegion.flip(false, true);

        secondaryFrameBuffer = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
        secondaryFboRegion = new TextureRegion(secondaryFrameBuffer.getColorBufferTexture());
        secondaryFboRegion.flip(false, true);
    }
}
