package basemod.patches.com.megacrit.cardcrawl.ui.panels.TopPanel;

import basemod.ClickableUIElement;
import basemod.DailyModsDropdown;
import basemod.patches.com.megacrit.cardcrawl.helpers.TopPanel.TopPanelHelper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CtBehavior;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class TopPanelPatches
{
    public static boolean renderDailyModsAsDropdown = true;
    public static String ascensionString;
    private static DailyModsDropdown dailyModsDropdown;
    private static ClickableUIElement ascensionIcon;
    private static float floorX;
    private static float titleY = Settings.HEIGHT - 28.0F * Settings.scale;
    private static float TIP_Y = Settings.HEIGHT - 120.0F * Settings.scale;
    private static float TIME_TIP_X = 1550.0f * Settings.scale - (192.0f + 45.0f) * Settings.scale;
    private static float TIME_X_POS = Settings.WIDTH - (380.0f + 192.0f + 45.0f) * Settings.scale;

    @SpirePatch(
            clz = TopPanel.class,
            method = "render",
            paramtypez = {SpriteBatch.class}
    )
    public static class RenderPatch
    {
        private static AbstractDungeon.CurrentScreen saveScreen;
        private static boolean saveStopClock;

        @SpirePrefixPatch
        public static void Prefix(TopPanel __instance, SpriteBatch sb)
        {
            saveScreen = AbstractDungeon.screen;
            saveStopClock = CardCrawlGame.stopClock;
            AbstractDungeon.screen = AbstractDungeon.CurrentScreen.COMBAT_REWARD;
            CardCrawlGame.stopClock = false;
        }

        @SpirePostfixPatch
        public static void Postfix(TopPanel __instance, SpriteBatch sb)
        {
            AbstractDungeon.screen = saveScreen;
            CardCrawlGame.stopClock = saveStopClock;

            if (!Settings.hideTopBar) {
                // Run timer
                if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP || CardCrawlGame.stopClock) {
                    __instance.timerHb.update();
                    sb.setColor(Color.WHITE);
                    sb.draw(ImageMaster.TIMER_ICON, TIME_X_POS, Settings.HEIGHT - 64.0f * Settings.scale, 64.0f * Settings.scale, 64.0f * Settings.scale);
                    Color clockColor = CardCrawlGame.stopClock ? Settings.GREEN_TEXT_COLOR : Settings.GOLD_COLOR;
                    FontHelper.renderFontLeftTopAligned(sb, FontHelper.panelNameTitleFont,
                            CharStat.formatHMSM(CardCrawlGame.playtime), TIME_X_POS + 60.0f * Settings.scale, titleY, clockColor);

                    if (__instance.timerHb.hovered) {
                        TipHelper.renderGenericTip(TIME_TIP_X, TIP_Y, TopPanel.LABEL[5], TopPanel.MSG[7]);
                    }
                    __instance.timerHb.render(sb);
                }

                // Mod panel items
                if (TopPanelHelper.topPanelGroup.size() > 0) {
                    TopPanelHelper.topPanelGroup.render(sb);
                }
            }
        }
    }

    @SpirePatch(
            clz = TopPanel.class,
            method = "renderDungeonInfo",
            paramtypez = {SpriteBatch.class}
    )
    public static class RenderDungeonInfo
    {
        public static SpireReturn<Void> Prefix(TopPanel __instance, SpriteBatch sb)
        {
            float ascensionIconX = floorX + 25.0F * Settings.scale;

            if (AbstractDungeon.floorNum > 0) {
                String message;
                if (Settings.usesOrdinal) {
                    message = AbstractDungeon.floorNum + TopPanel.getOrdinalNaming(AbstractDungeon.floorNum) + TopPanel.TEXT[0];
                } else {
                    message = AbstractDungeon.floorNum + TopPanel.TEXT[0];
                }
                ascensionIconX += FontHelper.getWidth(FontHelper.panelNameTitleFont, message, Settings.scale);
                FontHelper.renderFontLeftTopAligned(sb, FontHelper.panelNameTitleFont, message, floorX, titleY + 3.0F * Settings.scale, Settings.GOLD_COLOR);
            }

            if (AbstractDungeon.isAscensionMode) {
                ascensionIcon.setX(ascensionIconX);
                Color color = Color.WHITE;
                if (AbstractDungeon.ascensionLevel >= 20) {
                    color = Settings.RED_TEXT_COLOR;
                }
                ascensionIcon.render(sb, color);
                FontHelper.renderFontLeftTopAligned(sb, FontHelper.panelNameTitleFont, String.valueOf(AbstractDungeon.ascensionLevel), ascensionIconX + 35.0F * Settings.scale, titleY + 3.0F * Settings.scale, Settings.RED_TEXT_COLOR);
            }

            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(
            clz = TopPanel.class,
            method = "updateAscensionHover"
    )
    public static class UpdateAscensionHoverPatch
    {
        public static SpireReturn<Void> Prefix(TopPanel __instance)
        {
            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(
            clz = TopPanel.class,
            method = "update"
    )
    public static class UpdatePatch
    {
        @SpirePrefixPatch
        public static void Prefix(TopPanel __instance)
        {
            if (AbstractDungeon.screen != null && AbstractDungeon.screen != AbstractDungeon.CurrentScreen.UNLOCK) {
                if (TopPanelHelper.topPanelGroup.size() > 0) {
                    TopPanelHelper.topPanelGroup.update();
                }
                if (dailyModsDropdown != null) {
                    dailyModsDropdown.update();
                }
                ascensionIcon.update();
            }
        }
    }

    @SpirePatch(
            clz = TopPanel.class,
            method = "setPlayerName"
    )
    public static class SetPlayerNamePatch
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(TopPanel __instance)
        {
            __instance.timerHb.move(TIME_X_POS + 70.0f * Settings.scale, Settings.HEIGHT - 32.0f * Settings.scale);
            try {
                Field gl = TopPanel.class.getDeclaredField("gl");
                gl.setAccessible(true);
                Field name_f = TopPanel.class.getDeclaredField("name");
                name_f.setAccessible(true);

                String name = (String) name_f.get(__instance);

                float nameWidth = FontHelper.getWidth(FontHelper.panelNameFont, name, Settings.scale);
                float origNameWidth = nameWidth;
                String origName = name;
                boolean tooLong = false;
                while (nameWidth > 125f * Settings.scale) {
                    tooLong = true;
                    name = name.substring(0, name.length() - 1);
                    nameWidth = FontHelper.getWidth(FontHelper.panelNameFont, name, Settings.scale);
                }
                if (tooLong) {
                    name = name + "...";
                    // Remove the "..." if adding it makes the name longer than before
                    if (FontHelper.getWidth(FontHelper.panelNameFont, name, Settings.scale) > origNameWidth) {
                        name = origName;
                    }
                }
                GlyphLayout layout = (GlyphLayout) gl.get(__instance);
                layout.setText(FontHelper.panelNameFont, name);

                gl.set(__instance, layout);
                name_f.set(__instance, name);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception
            {
                Matcher matcher = new Matcher.FieldAccessMatcher(GlyphLayout.class, "width");
                return LineFinder.findInOrder(ctBehavior, new ArrayList<>(), matcher);
            }
        }

        public static void Postfix(TopPanel __instance)
        {
            if (ModHelper.enabledMods != null && !ModHelper.enabledMods.isEmpty()) {
                if (renderDailyModsAsDropdown) {
                    __instance.modHbs = new Hitbox[0];
                    dailyModsDropdown = new DailyModsDropdown(ModHelper.enabledMods, 1180f, -64f);
                }
            }

            try {
                Field floorX_f = TopPanel.class.getDeclaredField("floorX");
                floorX_f.setAccessible(true);
                floorX = floorX_f.getFloat(__instance) - 125.0F * Settings.scale;
                floorX = AbstractDungeon.ascensionLevel < 11 ? floorX + 44 * Settings.scale : floorX;
                Field ascensionString_f = TopPanel.class.getDeclaredField("ascensionString");
                ascensionString_f.setAccessible(true);
                ascensionString = (String) ascensionString_f.get(__instance);
                TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("powers/powers.atlas"));
                TextureAtlas.AtlasRegion ascensionImage = atlas.findRegion("48/minion");
                ascensionIcon = new ClickableUIElement(ascensionImage, 0, 0, ascensionImage.packedWidth + 40.0f, ascensionImage.packedHeight)
                {
                    @Override
                    protected void onHover()
                    {
                        TipHelper.renderGenericTip(InputHelper.mX + 50.0F * Settings.scale, TIP_Y, CharacterSelectScreen.TEXT[8], ascensionString);
                    }

                    @Override
                    protected void onUnhover() {}

                    @Override
                    protected void onClick() {}

                    @Override
                    public void render(SpriteBatch sb)
                    {
                        if (AbstractDungeon.ascensionLevel == 20) {
                            sb.setColor(Color.RED);
                        } else {
                            sb.setColor(Color.WHITE);
                        }
                        sb.draw(this.region, this.x, this.y, this.region.packedWidth * Settings.scale, this.region.packedHeight * Settings.scale );
                        renderHitbox(sb);
                    }
                };
                ascensionIcon.setY(titleY - 20.0F * Settings.scale);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    @SpirePatch(
            clz = TopPanel.class,
            method = "renderDailyMods",
            paramtypez = {SpriteBatch.class}
    )
    public static class RenderDailyModsPatch
    {
        public static SpireReturn<Void> Prefix(TopPanel __instance, SpriteBatch sb)
        {
            if (renderDailyModsAsDropdown) {
                dailyModsDropdown.render(sb);
                return SpireReturn.Return(null);
            } else {
                return SpireReturn.Continue();
            }
        }

    }
}
