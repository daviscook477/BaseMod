package basemod.patches.com.megacrit.cardcrawl.audio.SoundMaster;

import basemod.BaseMod;
import basemod6.BaseMod6;
import basemod6.events.AddAudioEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.audio.SoundMaster;

@SpirePatch(
        clz=SoundMaster.class,
        method=SpirePatch.CONSTRUCTOR
)
public class AddAudio {
    public static void Postfix(SoundMaster __instance) {
        BaseMod6.EVENT_BUS.post(new AddAudioEvent(__instance));
        BaseMod.publishAddAudio(__instance);
    }
}
