package basemod.patches.com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import basemod.abstracts.CustomCard;
import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.EverythingFix;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import java.lang.reflect.Field;

/**
 * 
 * @author kioeeht from branch custom-content on ModTheSpire
 * https://github.com/kiooeht/ModTheSpire/tree/custom-content
 *
 */
public class OpenFix
{
    @SpirePatch(
        cls="com.megacrit.cardcrawl.screens.SingleCardViewPopup",
        method="open",
        paramtypes={"com.megacrit.cardcrawl.cards.AbstractCard","com.megacrit.cardcrawl.cards.CardGroup"}
    )
    public static class Open
    {
        public static void Prefix(Object __obj_instance, Object cardObj, @ByRef Object[] groupObj)
        {
            if (groupObj[0] == null) {
                AbstractCard card = (AbstractCard)cardObj;
                groupObj[0] = EverythingFix.Fields.cardGroupMap.get(card.color);
            }
        }
    }

    @SpirePatch(
        cls="com.megacrit.cardcrawl.screens.SingleCardViewPopup",
        method="open",
        paramtypes={"com.megacrit.cardcrawl.cards.AbstractCard","com.megacrit.cardcrawl.cards.CardGroup"}
    )
    @SpirePatch(
        cls="com.megacrit.cardcrawl.screens.SingleCardViewPopup",
        method="open",
        paramtypes={"com.megacrit.cardcrawl.cards.AbstractCard"}
    )
    public static class OpenTextureFix {
        public static void Postfix(Object __obj_instance, Object cardObj, Object groupObj) {
            Postfix(__obj_instance, cardObj);
        }
        public static void Postfix(Object __obj_instance, Object cardObj) {
            AbstractCard card = (AbstractCard) cardObj;
            SingleCardViewPopup popup = (SingleCardViewPopup) __obj_instance;
            Field portraitImageField;
            try {
                portraitImageField = popup.getClass().getDeclaredField("portraitImg");
                portraitImageField.setAccessible(true);
                if (portraitImageField.get(popup) == null && card instanceof CustomCard) {
                    portraitImageField.set(popup, CustomCard.getPortraitImage((CustomCard) card));
                }
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}