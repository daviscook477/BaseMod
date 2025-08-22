package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

public class RenderInLibrary {
    @SpirePatch(
            clz = AbstractCard.class,
            method = SpirePatch.CLASS
    )
    public static class UpgradeCard
    {
        public static SpireField<AbstractCard> upgradeCard = new SpireField<>(() -> null);
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "renderInLibrary",
            paramtypez = {SpriteBatch.class}
    )
    public static class FixRenderLibraryUpgrade
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractCard __instance, SpriteBatch sb)
        {
            if (SingleCardViewPopup.isViewingUpgrade) {
                if (UpgradeCard.upgradeCard.get(__instance) == null) {
                    UpgradeCard.upgradeCard.set(__instance, __instance.makeCopy());
                    UpgradeCard.upgradeCard.get(__instance).upgrade();
                    UpgradeCard.upgradeCard.get(__instance).displayUpgrades();
                }
                UpgradeCard.upgradeCard.get(__instance).current_x = __instance.current_x;
                UpgradeCard.upgradeCard.get(__instance).current_y = __instance.current_y;
                UpgradeCard.upgradeCard.get(__instance).drawScale = __instance.drawScale;

                UpgradeCard.upgradeCard.get(__instance).render(sb);
                return SpireReturn.Return();
            } else if (UpgradeCard.upgradeCard.get(__instance) != null) {
                UpgradeCard.upgradeCard.set(__instance, null);
            }
            return SpireReturn.Continue();
        }

    }

}
