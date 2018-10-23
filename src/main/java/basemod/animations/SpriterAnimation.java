package basemod.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.LibGdx.LibGdxDrawer;
import com.brashmonkey.spriter.LibGdx.LibGdxLoader;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.Point;
import com.brashmonkey.spriter.SCMLReader;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class SpriterAnimation extends AbstractAnimation
{
    public static boolean drawBones = false;

    LibGdxLoader loader;
    LibGdxDrawer drawer;
    Player myPlayer;
    ShapeRenderer renderer;

    public SpriterAnimation(String filepath)
    {
        renderer = new ShapeRenderer();

        FileHandle handle = Gdx.files.internal(filepath);
        Data data = new SCMLReader(handle.read()).getData();

        loader = new LibGdxLoader(data);
        loader.load(handle.file());

        drawer = new LibGdxDrawer(loader, renderer);

        myPlayer = new Player(data.getEntity(0));

        myPlayer.setScale(Settings.scale);
    }

    @Override
    public Type type()
    {
        return Type.SPRITE;
    }

    @Override
    public void renderSprite(SpriteBatch batch)
    {
        drawer.batch = batch;
        // Update animation
        myPlayer.update();

        // Move to correct location on screen
        AbstractPlayer player = AbstractDungeon.player;
        if (player != null) {
            Point pos = new Point();
            pos.x = player.drawX + player.animX;
            pos.y = player.drawY + player.animY + AbstractDungeon.sceneOffsetY;
            myPlayer.setPosition(pos);

            drawer.draw(myPlayer);

            if (drawBones) {
                batch.end();
                renderer.setAutoShapeType(true);
                renderer.begin();
                drawer.drawBoxes(myPlayer);
                drawer.drawBones(myPlayer);
                renderer.end();
                batch.begin();
            }
        }
    }
}
