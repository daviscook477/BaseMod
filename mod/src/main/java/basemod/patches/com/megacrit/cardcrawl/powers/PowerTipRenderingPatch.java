package basemod.patches.com.megacrit.cardcrawl.powers;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;

// This patch is obsolete due to fixed power rendering already being done in the base game.

//public class PowerTipRenderingPatch {
//
//    @SpirePatch(
//            clz = TipHelper.class,
//            method = "renderPowerTips"
//    )
//    public static class PartOne {
//
//        @SpireInsertPatch(
//                locator = Locator.class,
//                localvars = {"shift", "offset"}
//        )
//        public static void Insert(float x, float y, SpriteBatch sb, ArrayList<PowerTip> powerTips, @ByRef boolean[] shift, @ByRef float[] offset) {
//            shift[0] = false;
//            offset[0] = 0.0F;
//        }
//
//        private static class Locator extends SpireInsertLocator {
//            @Override
//            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
//                Matcher finalMatcher = new Matcher.FieldAccessMatcher(Settings.class, "scale");
//                int[] found = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
//                found[found.length - 2] -= 1;
//                return new int[]{found[found.length - 2]};
//            }
//        }
//    }
//
//    @SpirePatch(
//            clz = TipHelper.class,
//            method = "renderPowerTips"
//    )
//    public static class PartTwo {
//
//        @SpireInsertPatch(
//                locator = Locator.class,
//                localvars = {"shift", "offset", "originalY", "offsetLeft"}
//        )
//        public static void Insert(float x, float y, SpriteBatch sb, ArrayList<PowerTip> powerTips, @ByRef boolean[] shift, float offset, float originalY, boolean offsetLeft) {
//            if (offset >= Settings.HEIGHT * 0.33F) {
//                shift[0] = false;
//            }
//            else {
//                shift[0] = true;
//            }
//        }
//
//        private static class Locator extends SpireInsertLocator {
//            @Override
//            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
//                Matcher finalMatcher = new Matcher.FieldAccessMatcher(Settings.class, "scale");
//                int[] found = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
//                return new int[]{found[found.length - 3]};
//            }
//        }
//    }
//
//    @SpirePatch(
//            clz = TipHelper.class,
//            method = "queuePowerTips"
//    )
//    public static class PartThree {
//
//        @SpireInsertPatch(
//                locator = Locator.class
//        )
//        public static void Insert(float x, float y, final ArrayList<PowerTip> powerTips) {
//            float originalValue = (float) ReflectionHacks.getPrivateStatic(TipHelper.class, "drawY");
//            ReflectionHacks.setPrivateStatic(TipHelper.class, "drawY", originalValue + powerTips.size() * 20.0f * Settings.scale);
//        }
//
//        private static class Locator extends SpireInsertLocator {
//            @Override
//            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
//                Matcher finalMatcher = new Matcher.FieldAccessMatcher(TipHelper.class, "drawY");
//                int[] found = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
//                return new int[]{found[found.length - 1]};
//            }
//        }
//    }
//
//    public static class PartFour {
//
//        @SpirePatch(
//                clz = AbstractCreature.class,
//                method = "renderPowerTips"
//        )
//        public static class AbstractCreatureFix {
//
//            public static ExprEditor Instrument() {
//                return new ExprEditor() {
//                    @Override
//                    public void edit(MethodCall m) throws CannotCompileException {
//                        if (m.getMethodName().equals("queuePowerTips")) {
//                            m.replace(
//                                    "{" +
//                                            "if (" + Nested.class.getName() + ".doingExperiment()) {" +
//                                            "$_ = $proceed($1, this.hb.cY + " + Nested.class.getName() + ".calculateAdditionalOffset($3, this.hb.cY), $3);" +
//                                            "}" +
//                                            "else" +
//                                            "{" +
//                                            "$_ = $proceed($$);" +
//                                            "}" +
//                                            "}");
//                        }
//                    }
//                };
//            }
//        }
//
//        @SpirePatch(
//                clz = AbstractMonster.class,
//                method = "renderTip"
//        )
//        public static class AbstractMonsterFix {
//
//            public static ExprEditor Instrument() {
//                return new ExprEditor() {
//                    @Override
//                    public void edit(MethodCall m) throws CannotCompileException {
//                        if (m.getMethodName().equals("queuePowerTips")) {
//                            m.replace(
//                                    "{" +
//                                            "if (" + Nested.class.getName() + ".doingExperiment()) {" +
//                                            "$_ = $proceed($1, this.hb.cY + " + Nested.class.getName() + ".calculateAdditionalOffset($3, this.hb.cY), $3);" +
//                                            "}" +
//                                            "else" +
//                                            "{" +
//                                            "$_ = $proceed($$);" +
//                                            "}" +
//                                            "}");
//                        }
//                    }
//                };
//            }
//        }
//
//        public static class Nested {
//
//            public static boolean doingExperiment() {
//                return true;
//            }
//
//            public static float calculateAdditionalOffset(ArrayList<PowerTip> powerTips, float hBcY) {
//                float currentOffset = 0.0F;
//                float maxOffset = 0.0F;
//                for (PowerTip p : powerTips) {
//                    currentOffset += getSupposedPowerTipHeight(p);
//                    if (currentOffset > maxOffset) {
//                        maxOffset = currentOffset;
//                    }
//                    if (currentOffset >= Settings.HEIGHT * 0.33F) {
//                        currentOffset = 0.0F;
//                    }
//                }
//                if (powerTips.isEmpty()) {
//                    return 0.0F;
//                }
//                else {
//                    // I have no idea why I have to multiply by 2.5F, but it works. -BDWSSBB
//                    return (Settings.HEIGHT - hBcY) / Settings.HEIGHT * maxOffset * 2.5F - getSupposedPowerTipHeight(powerTips.get(0));
//                }
//            }
//
//            public static float getSupposedPowerTipHeight(PowerTip powerTip) {
//                return -FontHelper.getSmartHeight(FontHelper.tipBodyFont, powerTip.body, 280.0f * Settings.scale, 26.0f * Settings.scale) - 7.0f * Settings.scale + 32.0f * Settings.scale;
//            }
//        }
//    }
//}
