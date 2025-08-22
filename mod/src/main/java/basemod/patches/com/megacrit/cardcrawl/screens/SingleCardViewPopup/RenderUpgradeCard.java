package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CtBehavior;

import java.lang.reflect.Field;

public class RenderUpgradeCard
{
    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = SpirePatch.CLASS
    )
    public static class UpgradeStatus
    {
        public static SpireField<Boolean> preViewingUpgrade = new SpireField<>(() -> false);
        public static SpireField<Boolean> nowViewingUpgrade = new SpireField<>(() -> false);
        public static SpireField<AbstractCard> originalCard = new SpireField<>(() -> null);
    }

    @SpirePatch2(
            clz = SingleCardViewPopup.class,
            method = "render",
            paramtypez = {SpriteBatch.class}
    )
    public static class FixRenderUpgrade
    {
        @SpirePrefixPatch
        public static void prefix(SingleCardViewPopup __instance)
        {
            // disable isViewingUpgrade in this method
            UpgradeStatus.nowViewingUpgrade.set(__instance, SingleCardViewPopup.isViewingUpgrade);
            SingleCardViewPopup.isViewingUpgrade = false;
            if (UpgradeStatus.nowViewingUpgrade.get(__instance) != UpgradeStatus.preViewingUpgrade.get(__instance)) {
                Field cardField;
                try {
                    cardField = SingleCardViewPopup.class.getDeclaredField("card");
                    cardField.setAccessible(true);
                    if (UpgradeStatus.nowViewingUpgrade.get(__instance)) {
                        // isViewUpgrade just false -> true
                        AbstractCard card = (AbstractCard) cardField.get(__instance);
                        UpgradeStatus.originalCard.set(__instance, card.makeStatEquivalentCopy());
                        card.upgrade();
                        card.displayUpgrades();
                    } else {
                        // isViewUpgrade just true -> false
                        cardField.set(__instance, UpgradeStatus.originalCard.get(__instance));
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }

            }

        }

        @SpireInsertPatch(locator = Locator.class)
        public static void insert(SingleCardViewPopup __instance)
        {
            SingleCardViewPopup.isViewingUpgrade = UpgradeStatus.nowViewingUpgrade.get(__instance);
            UpgradeStatus.preViewingUpgrade.set(__instance, SingleCardViewPopup.isViewingUpgrade);
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher matcher = new Matcher.MethodCallMatcher(SpriteBatch.class, "setColor");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }

    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "close"
    )
    public static class CloseFix
    {
        @SpirePostfixPatch
        public static void postfix(SingleCardViewPopup __instance)
        {
            UpgradeStatus.nowViewingUpgrade.set(__instance, false);
            UpgradeStatus.preViewingUpgrade.set(__instance, false);
            if (UpgradeStatus.originalCard.get(__instance) != null) {
                UpgradeStatus.originalCard.set(__instance, null);
            }
        }
    }

}
