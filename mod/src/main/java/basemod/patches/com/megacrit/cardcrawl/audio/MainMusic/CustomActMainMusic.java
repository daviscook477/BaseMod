package basemod.patches.com.megacrit.cardcrawl.audio.MainMusic;

import basemod.BaseMod;
import basemod.customacts.CustomDungeon;
import com.badlogic.gdx.audio.Music;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.audio.MainMusic;

@SpirePatch(clz = MainMusic.class, method = "getSong")
public class CustomActMainMusic {
    //Load custom music for an act if it exists.
    @SpirePostfixPatch
    public static Music Postfix(Music __result, MainMusic __instance, String key) {
        if(CustomDungeon.dungeons.containsKey(key)) {
            CustomDungeon cd = CustomDungeon.dungeons.get(key);
            if(cd.mainmusic != null) {
                BaseMod.logger.info("Starting custom music for act: " + key);
                return MainMusic.newMusic(cd.mainmusic);
            }
        }
        return __result;
    }

}