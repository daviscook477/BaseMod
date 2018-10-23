package basemod.patches.com.megacrit.cardcrawl.screens.mainMenu.ColorTabBar;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.ColorTabBar;
import com.megacrit.cardcrawl.screens.mainMenu.TabBarListener;
import javassist.CtBehavior;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

public class ColorTabBarFix
{
    public static class Enums
    {
        @SpireEnum
        public static ColorTabBar.CurrentTab MOD;
    }

    public static class ModColorTab
    {
        public AbstractCard.CardColor color;
        private Hitbox hb;

        private ModColorTab(AbstractCard.CardColor color, Hitbox hb)
        {
            this.color = color;
            this.hb = hb;
        }
    }

    public static class Fields
    {
        static int modTabIndex = 0;

        static ArrayList<ModColorTab> modTabs;

        public static ModColorTab getModTab()
        {
            return modTabs.get(modTabIndex);
        }
    }

    private static boolean colorExists(AbstractCard.CardColor color)
    {
        return BaseMod.getCardColors().contains(color);
    }

    @SpirePatch(
            clz=ColorTabBar.class,
            method=SpirePatch.CONSTRUCTOR
    )
    public static class Ctor
    {
        public static void Postfix(ColorTabBar __instance, TabBarListener delegate)
        {
            Fields.modTabs = new ArrayList<>();
            for (AbstractCard.CardColor color : BaseMod.getCardColors()) {
                Fields.modTabs.add(new ModColorTab(
                        color,
                        new Hitbox(235.0f * Settings.scale, 51.0f * Settings.scale)
                ));
            }
        }
    }

    @SpirePatch(
            clz=ColorTabBar.class,
            method="update"
    )
    public static class Update
    {
        public static void Postfix(ColorTabBar __instance, float y)
        {
            boolean anyHovered = false;
            for (int i = 0; i<Fields.modTabs.size(); ++i) {
                Fields.modTabs.get(i).hb.move(157.0f * Settings.scale, y - Render.SPACING * i * Settings.scale - 14.0f * Settings.scale);
                Fields.modTabs.get(i).hb.update();
                if (!anyHovered && Fields.modTabs.get(i).hb.justHovered) {
                    anyHovered = true;
                    CardCrawlGame.sound.playA("UI_HOVER", -0.4f);
                }
                if (InputHelper.justClickedLeft) {
                    if (Fields.modTabs.get(i).hb.hovered) {
                        try {
                            Field curTab = ColorTabBar.class.getDeclaredField("curTab");
                            curTab.setAccessible(true);
                            ColorTabBar.CurrentTab oldTab = (ColorTabBar.CurrentTab)curTab.get(__instance);
                            if (oldTab != Enums.MOD || Fields.modTabIndex != i) {
                                curTab.set(__instance, Enums.MOD);
                                Fields.modTabIndex = i;
                                Field f = ColorTabBar.class.getDeclaredField("delegate");
                                f.setAccessible(true);
                                TabBarListener delegate = (TabBarListener)f.get(__instance);
                                delegate.didChangeTab(__instance, Enums.MOD);
                            }
                        } catch (IllegalAccessException | NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @SpirePatch(
            clz=ColorTabBar.class,
            method="render"
    )
    public static class Render
    {
        private static final float SPACING = 64.0f;

        @SpireInsertPatch(
                locator=Locator.class,
                localvars={"curTab"}
        )
        public static void Insert(ColorTabBar __instance, SpriteBatch sb, float y, ColorTabBar.CurrentTab curTab)
        {
            for (int i = 0; i<Fields.modTabs.size(); ++i) {
                Color color = BaseMod.getTrailVfxColor(Fields.modTabs.get(i).color).cpy();
                if (curTab != Enums.MOD || Fields.modTabIndex != i) {
                    color = color.lerp(Color.GRAY, 0.5f);
                }
                sb.setColor(color);
                sb.draw(ImageMaster.COLOR_TAB_BAR, 40.0f * Settings.scale, y - SPACING * (i+1) * Settings.scale, 0, 0, 235.0F, 102.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1334, 102, false, false);

                Color textcolor = Settings.GOLD_COLOR;
                if (curTab != Enums.MOD || Fields.modTabIndex != i) {
                    textcolor = Color.GRAY;
                    sb.setColor(Color.GRAY);
                } else {
                    sb.setColor(Color.WHITE);
                }
                // Find character name
                AbstractPlayer.PlayerClass playerClass = null;
                for (AbstractPlayer character : CardCrawlGame.characterManager.getAllCharacters()) {
                    if (character.getCardColor() == Fields.modTabs.get(i).color) {
                        playerClass = character.chosenClass;
                        break;
                    }
                }
                String tabName = playerClass != null ? BaseMod.findCharacter(playerClass).getLocalizedCharacterName() : capitalizeWord(Fields.modTabs.get(i).color.toString());
                FontHelper.renderFontCentered(sb, FontHelper.bannerFont, tabName, 157.0f * Settings.scale, y - (SPACING * (i+1) * Settings.scale) + 50.0f * Settings.scale, textcolor, 0.6f);
            }

            for (int i = 0; i<Fields.modTabs.size(); ++i) {
                Fields.modTabs.get(i).hb.render(sb);
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ColorTabBar.class, "getBarColor");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz=ColorTabBar.class,
            method="getBarColor"
    )
    public static class GetBarColor
    {
        public static Color Postfix(Color __result, ColorTabBar __instance)
        {
            if (__result.equals(Color.WHITE)) {
                try {
                    Field f = ColorTabBar.class.getDeclaredField("curTab");
                    f.setAccessible(true);
                    if (f.get(__instance) == Enums.MOD) {
                        return BaseMod.getTrailVfxColor(Fields.getModTab().color);
                    }
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }

            return __result;
        }
    }

    private static String capitalizeWord(String str)
    {
        if (str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + (str.length() > 1 ? str.substring(1).toLowerCase() : "");
    }
}
