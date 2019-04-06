package basemod6.events;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.megacrit.cardcrawl.audio.Sfx;
import com.megacrit.cardcrawl.audio.SoundMaster;

import java.util.HashMap;

public class AddAudioEvent extends Event
{
    private SoundMaster soundMaster;

    public AddAudioEvent(SoundMaster soundMaster)
    {
        this.soundMaster = soundMaster;
    }

    public void addAudio(String audioKey, String file)
    {
        FileHandle sfxFile = Gdx.files.internal(file); // Ensure audio is valid file

        if (sfxFile != null && sfxFile.exists()) {
            Sfx audioSfx = new Sfx(file, false);
            @SuppressWarnings("unchecked")
            HashMap<String, Sfx> map = (HashMap<String, Sfx>) ReflectionHacks.getPrivate(soundMaster, SoundMaster.class, "map");

            if (map != null) {
                if (map.containsKey(audioKey)) {
                    BaseMod.logger.warn("Audio \"" + audioKey + "\" already exists, overriding");
                }
                map.put(audioKey, audioSfx);
                BaseMod.logger.info("Added sound: " + audioKey);
            } else {
                BaseMod.logger.warn("Unexpectedly failed to add sound: " + audioKey);
            }

            ReflectionHacks.setPrivate(soundMaster, SoundMaster.class, "map", map);
        } else {
            BaseMod.logger.warn("Audio file: " + file + " was not found.");
        }
    }
}
