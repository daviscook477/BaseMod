package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import java.util.HashSet;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = SpirePatch.CLASS
)
public class SeenEvents {
    public static SpireField<HashSet<String>> seenEvents = new SpireField<>(HashSet::new);
}
