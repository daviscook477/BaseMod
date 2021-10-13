package basemod.patches.com.megacrit.cardcrawl.helpers.TipHelper;

import basemod.ReflectionHacks;
import basemod.helpers.CardPowerTip;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.AlternateCardCosts;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;

public class CardPowerTipsInTipHelperPatch {
    private static Float boxW;
    private static Float boxEdgeH;

    @SpirePatch(
            clz = TipHelper.class,
            method = "getPowerTipHeight"
    )
    public static class PowerTipHeightPatch {
        public static SpireReturn<Float> Prefix(PowerTip powerTip) {
            if (powerTip instanceof CardPowerTip && (powerTip.header == null || powerTip.body == null)) {
                if (boxEdgeH == null) {
                    boxEdgeH = (float)ReflectionHacks.getPrivateStatic(TipHelper.class, "BOX_EDGE_H") * 3.15f;
                }
                return SpireReturn.Return((AbstractCard.IMG_HEIGHT * ((CardPowerTip)powerTip).cardScale) + (boxEdgeH / 2.0f));
            }
            return SpireReturn.Continue();
        }

        public static float Postfix(float __result, PowerTip powerTip) {
            if (powerTip instanceof CardPowerTip && powerTip.header != null && powerTip.body != null) {
                ((CardPowerTip)powerTip).textHeight = __result;
                if (boxEdgeH == null) {
                    boxEdgeH = (float)ReflectionHacks.getPrivateStatic(TipHelper.class, "BOX_EDGE_H") * 3.15f;
                }
                return __result + (AbstractCard.IMG_HEIGHT * ((CardPowerTip)powerTip).cardScale) + (boxEdgeH / 2.0f);
            }
            return __result;
        }
    }

    @SpirePatch(
            clz = TipHelper.class,
            method = "renderPowerTips"
    )
    public static class SkipTipBoxPatch {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(TipHelper.class.getName()) && m.getMethodName().equals("renderTipBox")) {
                        String manager = AlternateCardCosts.class.getName();
                        String energy = EnergyPanel.class.getName();
                        m.replace("if (tip.header != null && tip.body != null) {" +
                                    "$proceed($$);" +
                                "} else {" +
                                    "tip.header = \"\";" +
                                "}"
                        );
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz = TipHelper.class,
            method = "renderPowerTips"
    )
    public static class RenderCardPatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"tip"}
        )
        public static void Insert(float x, float y, SpriteBatch sb, ArrayList<PowerTip> powerTips, PowerTip tip) {
            if (tip instanceof CardPowerTip) {
                if (boxW == null) {
                    boxW = ReflectionHacks.getPrivateStatic(TipHelper.class, "BOX_W");
                }
                AbstractCard card = ((CardPowerTip)tip).card;
                card.current_x = x + (boxW / 2.0f);
                card.current_y = y - (((CardPowerTip)tip).textHeight + ((AbstractCard.IMG_HEIGHT / 2.0f) * ((CardPowerTip)tip).cardScale));
                if (((CardPowerTip)tip).body != null) {
                    if (boxEdgeH == null) {
                        boxEdgeH = (float)ReflectionHacks.getPrivateStatic(TipHelper.class, "BOX_EDGE_H") * 3.15f;
                    }
                    card.current_y -= (boxEdgeH / 4.0F) * 3.0F;
                }
                card.drawScale = ((CardPowerTip)tip).cardScale;
                card.render(sb);
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(GlyphLayout.class, "setText");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    @SpirePatch(
            clz = TipHelper.class,
            method = "renderPowerTips"
    )
    public static class FixOffsetPatch {
        @SpireInsertPatch(
                locator = OffsetLocator.class,
                localvars = {"tip", "offsetChange"}
        )
        public static void Insert(float x, float y, SpriteBatch sb, ArrayList<PowerTip> powerTips, PowerTip powerTip, @ByRef float[] offsetChange) {
            if (powerTip instanceof CardPowerTip && (powerTip.body == null || powerTip.header == null)) {
                if (boxEdgeH == null) {
                    boxEdgeH = (float)ReflectionHacks.getPrivateStatic(TipHelper.class, "BOX_EDGE_H") * 3.15f;
                }
                offsetChange[0] -= boxEdgeH;
            }
        }
    }

    private static class OffsetLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(GlyphLayout.class, "setText");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
