package basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;

import basemod.patches.com.megacrit.cardcrawl.screens.mainMenu.ColorTabBar.ColorTabBarFix;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.ColorTabBar;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author kioeeht from branch custom-content on ModTheSpire
 * https://github.com/kiooeht/ModTheSpire/tree/custom-content
 *
 */
public class EverythingFix
{
    public static class Fields
    {
        public static Map<AbstractCard.CardColor, CardGroup> cardGroupMap = new HashMap<>();
        private static Set<AbstractCard.CardColor> noLibraryTypes = new HashSet<>();
    }

    @SpirePatch(
            clz=CardLibraryScreen.class,
            method="initialize"
    )
    public static class Initialize
    {
        @SpireInsertPatch(
                locator=Locator.class
        )
        public static void Insert(Object __obj_instance)
        {
            try {
                AbstractCard.CardColor[] colors = AbstractCard.CardColor.values();
                for (int icolor = AbstractCard.CardColor.CURSE.ordinal() + 1; icolor < colors.length; ++icolor) {
                    CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                    try {
                        group.group = CardLibrary.getCardList(CardLibrary.LibraryType.valueOf(colors[icolor].name()));
                        Fields.cardGroupMap.put(colors[icolor], group);
                    } catch (IllegalArgumentException e) {
                        NoLibraryType annotation = colors[icolor].getClass().getField(colors[icolor].name()).getAnnotation(NoLibraryType.class);
                        if (annotation != null) {
                            Fields.noLibraryTypes.add(colors[icolor]);
                        } else {
                            throw e;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardLibraryScreen.class, "calculateScrollBounds");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz=CardLibraryScreen.class,
            method="setLockStatus"
    )
    public static class setLockStatus
    {
        public static void Postfix(Object __obj_instance)
        {
            try {
                CardLibraryScreen screen = (CardLibraryScreen) __obj_instance;

                AbstractCard.CardColor[] colors = AbstractCard.CardColor.values();
                for (int icolor = AbstractCard.CardColor.CURSE.ordinal() + 1; icolor < colors.length; ++icolor) {
                    CardGroup group = Fields.cardGroupMap.get(colors[icolor]);
                    if (group == null && Fields.noLibraryTypes.contains(colors[icolor])) {
                        continue;
                    }

                    @SuppressWarnings("rawtypes")
                    Class[] cArg = new Class[1];
                    cArg[0] = CardGroup.class;
                    Method lockStatusHelper = screen.getClass().getDeclaredMethod("lockStatusHelper", cArg);
                    lockStatusHelper.setAccessible(true);
                    lockStatusHelper.invoke(screen, group);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @SpirePatch(
            clz=CardLibraryScreen.class,
            method="didChangeTab"
    )
    public static class DidChangeTab
    {
        @SpireInsertPatch(
                rloc=1,
                localvars={"visibleCards"}
        )
        public static void Insert(CardLibraryScreen __instance, ColorTabBar tabBar, ColorTabBar.CurrentTab newSelection, @ByRef CardGroup[] visibleCards)
        {
            if (newSelection == ColorTabBarFix.Enums.MOD) {
                visibleCards[0] = Fields.cardGroupMap.get(ColorTabBarFix.Fields.getModTab().color);
            }
        }
    }

    @SpirePatch(
            clz=CardLibraryScreen.class,
            method="open"
    )
    public static class Open
    {
        public static void Postfix(CardLibraryScreen __instance)
        {
            AbstractCard.CardColor[] colors = AbstractCard.CardColor.values();
            for (int icolor = AbstractCard.CardColor.CURSE.ordinal() + 1; icolor < colors.length; ++icolor) {
                CardGroup group = Fields.cardGroupMap.get(colors[icolor]);
                if (group == null && Fields.noLibraryTypes.contains(colors[icolor])) {
                    continue;
                }
                for (AbstractCard c : group.group) {
                    c.drawScale = MathUtils.random(0.2F, 0.4F);
                    c.targetDrawScale = 0.75F;
                }
            }
        }
    }
}