package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.ReflectionHacks;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CtBehavior;

import java.util.Collections;

public class UpgradeChangesPortraitPatch {

    /*
    To make a card change portraits on upgrade, you should call CustomCard loadCardImage on upgrade,
    and override CustomCard getPortraitImage to return your alternate art if the card is upgraded.
     */

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "updateUpgradePreview"
    )
    public static class ToggleUpgrade {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"card", "portraitImg"}
        )
        public static void Insert(SingleCardViewPopup __instance, AbstractCard card, @ByRef Texture[] portraitImg) {
            if (card instanceof CustomCard) {
                if (portraitImg[0] != null) {
                    portraitImg[0].dispose();
                }
                AbstractCard copy = card.makeStatEquivalentCopy();
                if (copy instanceof CustomCard) {
                    // isViewingUpgrade hasn't been toggled yet
                    if (!SingleCardViewPopup.isViewingUpgrade) {
                        copy.upgrade();
                    }
                    portraitImg[0] = CustomCard.getPortraitImage((CustomCard) copy);
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(SingleCardViewPopup.class, "isViewingUpgrade");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "openPrev"
    )
    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "openNext"
    )
    public static class OpenUpgrade {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"card", "tmp"}
        )
        public static void Insert(SingleCardViewPopup __instance, AbstractCard card, boolean tmp) {
            if (!card.upgraded) {
                AbstractCard copy = card.makeStatEquivalentCopy();
                if (copy instanceof CustomCard) {
                    copy.upgrade();
                    Texture tex = CustomCard.getPortraitImage((CustomCard) copy);
                    ReflectionHacks.setPrivate(__instance, SingleCardViewPopup.class, "portraitImg", tex);
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher firstMatcher = new Matcher.MethodCallMatcher(SingleCardViewPopup.class, "open");
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(SingleCardViewPopup.class, "isViewingUpgrade");
                return LineFinder.findInOrder(ctMethodToPatch, Collections.singletonList(firstMatcher), finalMatcher);
            }
        }
    }
}
