package basemod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;

import java.util.HashMap;

public class AtlasLoader {
    private static HashMap<String, TextureAtlas> atlases = new HashMap<>();

    public static TextureAtlas getAtlas(final String atlasString) {
        if (atlases.get(atlasString) == null) {
            try {
                loadAtlas(atlasString);
            } catch (GdxRuntimeException e) {

            }
        }
        return atlases.get(atlasString);
    }

    private static void loadAtlas(final String atlasString) throws GdxRuntimeException {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(atlasString));
        atlases.put(atlasString, atlas);
    }

    public static boolean testAtlas(String filePath) {
        return Gdx.files.internal(filePath).exists();
    }

    @SpirePatch(clz = TextureAtlas.class, method = "dispose")
    public static class DisposeListener {
        @SpirePrefixPatch
        public static void DisposeListenerPatch(final TextureAtlas __instance) {
            atlases.entrySet().removeIf(entry -> {
                return entry.getValue().equals(__instance);
            });
        }
    }
}