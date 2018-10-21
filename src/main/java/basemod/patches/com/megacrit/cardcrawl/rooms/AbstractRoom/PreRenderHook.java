package basemod.patches.com.megacrit.cardcrawl.rooms.AbstractRoom;


import basemod.BaseMod;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

@SpirePatch(clz= AbstractRoom.class, method = "render")
public class PreRenderHook {
	@SpirePrefixPatch
	public static void preRoomRenderHook(AbstractRoom __instance, SpriteBatch sb) {
		BaseMod.publishPreRoomRender(sb);
	}
}
