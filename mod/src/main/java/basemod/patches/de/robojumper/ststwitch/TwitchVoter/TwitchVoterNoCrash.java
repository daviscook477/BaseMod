package basemod.patches.de.robojumper.ststwitch.TwitchVoter;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import de.robojumper.ststwitch.TwitchConfig;
import de.robojumper.ststwitch.TwitchVoteOption;
import de.robojumper.ststwitch.TwitchVoter;

@SpirePatch(
        clz = TwitchVoter.class,
        method = SpirePatch.CONSTRUCTOR
)
public class TwitchVoterNoCrash {
    @SpirePostfixPatch
    public static void initializeOptions(TwitchVoter __instance, TwitchConfig config) {
        ReflectionHacks.setPrivate(__instance, TwitchVoter.class, "options", new TwitchVoteOption[0]);
    }
}
