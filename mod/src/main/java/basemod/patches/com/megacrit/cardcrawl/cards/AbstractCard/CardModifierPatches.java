package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveFile.ModSaves;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.megacrit.cardcrawl.actions.unique.RestoreRetainedCardsAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.metrics.Metrics;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;
import com.megacrit.cardcrawl.screens.runHistory.TinyCard;
import com.megacrit.cardcrawl.screens.stats.RunData;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;
import org.clapper.util.classutil.*;
import com.evacipated.cardcrawl.modthespire.Loader;

import java.io.File;
import java.net.URISyntaxException;
import java.util.*;

public class CardModifierPatches
{
    @SpirePatch2(clz = CardHelper.class, method = "hasCardWithXDamage")
    public static class FixDamageRequirement {
        @SpireInstrumentPatch
        public static ExprEditor plz() {
            return new ExprEditor() {
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.getClassName().equals(AbstractCard.class.getName()) && f.getFieldName().equals("baseDamage")) {
                        f.replace("$_ = basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.CardModifierPatches.FixDamageRequirement.effectiveBase($0);");
                    }
                }
            };
        }

        public static int effectiveBase(AbstractCard card) {
            return BaseMod.cardDynamicVariableMap.get("D").modifiedBaseValue(card);
        }
    }


    @SpirePatch(
            clz = AbstractCard.class,
            method = "applyPowers"
    )
    @SpirePatch(
            clz = AbstractCard.class,
            method = "calculateCardDamage"
    )
    public static class UseModifiedBaseDamage {
        @SpireRawPatch
        public static void useModifiedBase(CtBehavior ctMethodToPatch) throws Exception {
            String varName;
            Exception ex = null;
            for (int i = 0; i < 9; ++i) {
                varName = "modifiedBase" + i;
                try {
                    ctMethodToPatch.addLocalVariable(varName, ctMethodToPatch.getDeclaringClass().getClassPool().get(int.class.getName()));
                } catch (CannotCompileException e) {
                    ex = e;
                    continue;
                }

                String finalVarName = varName;

                ctMethodToPatch.instrument(new ExprEditor() {
                    boolean initializedVar = false;

                    @Override
                    public void edit(MethodCall m) throws CannotCompileException {
                        if (!initializedVar) {
                            String methodCall = CardModifierManager.class.getName() + ".onModifyBaseDamage((float) baseDamage, this, "
                                    + (ctMethodToPatch.getName().equals("calculateCardDamage") ? "mo" : "null") + ");";

                            m.replace(finalVarName + " = (int) " + methodCall +
                                    "$_ = $proceed($$);");
                            initializedVar = true;
                        }
                    }

                    @Override
                    public void edit(FieldAccess f) throws CannotCompileException {
                        if (f.isReader() && f.getFieldName().equals("baseDamage")) {
                            f.replace("$_ = " + finalVarName + ";");
                        }
                    }
                });
                return;
            }
            throw ex;
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "applyPowers"
    )
    public static class CardModifierOnApplyPowers
    {
        //modifyBaseMagic
        public static void Prefix(AbstractCard __instance) {
            int magic = (int) CardModifierManager.onModifyBaseMagic(__instance.baseMagicNumber, __instance);
            if (magic != __instance.baseMagicNumber) {
                __instance.magicNumber = magic;
                __instance.isMagicNumberModified = true;
            }
        }

        //onApplyPowers
        public static void Postfix(AbstractCard __instance) {
            CardModifierManager.onApplyPowers(__instance);
        }

        //modifyDamage
        @SpireInsertPatch(
                locator = DamageLocator.class,
                localvars = {"tmp"}
        )
        public static void damageInsert(AbstractCard __instance, @ByRef float[] tmp) {
            tmp[0] = CardModifierManager.onModifyDamage(tmp[0], __instance, null);
        }

        private static class DamageLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[] {tmp[0]};
            }
        }

        @SpireInsertPatch(
                locator = MultiDamageLocator.class,
                localvars = {"tmp", "i"}
        )
        public static void multiDamageInsert(AbstractCard __instance, float[] tmp, int i) {
            tmp[i] = CardModifierManager.onModifyDamage(tmp[i], __instance, null);
        }

        private static class MultiDamageLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[] {tmp[2]};
            }
        }

        //modifyDamageFinal
        @SpireInsertPatch(
                locator = DamageFinalLocator.class,
                localvars = {"tmp"}
        )
        public static void damageFinalInsert(AbstractCard __instance, @ByRef float[] tmp) {
            tmp[0] = CardModifierManager.onModifyDamageFinal(tmp[0], __instance, null);
        }

        private static class DamageFinalLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[] {tmp[1]};
            }
        }

        @SpireInsertPatch(
                locator = MultiDamageFinalLocator.class,
                localvars = {"tmp", "i"}
        )
        public static void multiDamageFinalInsert(AbstractCard __instance, float[] tmp, int i) {
            tmp[i] = CardModifierManager.onModifyDamageFinal(tmp[i], __instance, null);
        }

        private static class MultiDamageFinalLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[] {tmp[3]};
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "calculateCardDamage"
    )
    public static class CardModifierCalculateCardDamage
    {
        //modifyBaseMagic
        public static void Prefix(AbstractCard __instance) {
            int magic = (int) CardModifierManager.onModifyBaseMagic(__instance.baseMagicNumber, __instance);
            if (magic != __instance.baseMagicNumber) {
                __instance.magicNumber = magic;
                __instance.isMagicNumberModified = true;
            }
        }

        //onCalculateCardDamage
        public static void Postfix(AbstractCard __instance, AbstractMonster mo) {
            CardModifierManager.onCalculateCardDamage(__instance, mo);
        }

        //modifyDamage
        @SpireInsertPatch(
                locator = DamageLocator.class,
                localvars = {"tmp"}
        )
        public static void damageInsert(AbstractCard __instance, AbstractMonster m, @ByRef float[] tmp) {
            tmp[0] = CardModifierManager.onModifyDamage(tmp[0], __instance, m);
        }

        private static class DamageLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[] {tmp[0]};
            }
        }

        @SpireInsertPatch(
                locator = MultiDamageLocator.class,
                localvars = {"tmp", "i", "m"}
        )
        public static void multiDamageInsert(AbstractCard __instance, AbstractMonster mo, float[] tmp, int i, ArrayList<AbstractMonster> m) {
            tmp[i] = CardModifierManager.onModifyDamage(tmp[i], __instance, m.get(i));
        }

        private static class MultiDamageLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[] {tmp[2]};
            }
        }

        //modifyDamageFinal
        @SpireInsertPatch(
                locator = DamageFinalLocator.class,
                localvars = {"tmp"}
        )
        public static void damageFinalInsert(AbstractCard __instance, AbstractMonster m, @ByRef float[] tmp) {
            tmp[0] = CardModifierManager.onModifyDamageFinal(tmp[0], __instance, m);
        }

        private static class DamageFinalLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[] {tmp[1]};
            }
        }

        @SpireInsertPatch(
                locator = MultiDamageFinalLocator.class,
                localvars = {"tmp", "i", "m"}
        )
        public static void multiDamageFinalInsert(AbstractCard __instance, AbstractMonster mo, float[] tmp, int i, ArrayList<AbstractMonster> m) {
            tmp[i] = CardModifierManager.onModifyDamageFinal(tmp[i], __instance, m.get(i));
        }

        private static class MultiDamageFinalLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[] {tmp[3]};
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "applyPowersToBlock"
    )
    public static class CardModifierApplyPowersToBlock
    {
        //baseBlock
        @SpireInstrumentPatch
        public static ExprEditor baseBlock() {
            return new ExprEditor() {
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.isReader() && f.getFieldName().equals("baseBlock")) {
                        f.replace("$_ = (int) " + CardModifierManager.class.getName() + ".onModifyBaseBlock((float) $proceed($$), this);");
                    }
                }
            };
        }

        //modifyBlock
        @SpireInsertPatch(
                locator = BlockLocator.class,
                localvars = {"tmp"}
        )
        public static void blockInsert(AbstractCard __instance, @ByRef float[] tmp) {
            tmp[0] = CardModifierManager.onModifyBlock(tmp[0], __instance);
        }

        private static class BlockLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        //modifyBlockFinal
        @SpireInsertPatch(
                locator = BlockFinalLocator.class,
                localvars = {"tmp"}
        )
        public static void blockFinalInsert(AbstractCard __instance, @ByRef float[] tmp) {
            tmp[0] = CardModifierManager.onModifyBlockFinal(tmp[0], __instance);
        }

        private static class BlockFinalLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(MathUtils.class, "floor");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "initializeDescription"
    )
    @SpirePatch(
            clz = AbstractCard.class,
            method = "initializeDescriptionCN"
    )
    public static class CardModifierOnCreateDescription
    {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.getClassName().equals(AbstractCard.class.getName()) && f.getFieldName().equals("rawDescription")) {
                        f.replace("$_ = " + CardModifierPatches.CardModifierOnCreateDescription.class.getName() + ".calculateRawDescription(this, $proceed($$));");
                    }
                }
            };
        }
        public static String calculateRawDescription(AbstractCard card, String rawDescription) {
            //card modifier logic
            rawDescription = CardModifierManager.onCreateDescription(card, rawDescription);
            //OnCreateDescription subscriber
            rawDescription = BaseMod.publishOnCreateDescription(rawDescription, card);
            return rawDescription;
        }
    }

    public static class CardModifierOnRenderCardTitle {
        //Constants used in AbstractCard for scale formatting
        private static final float IMG_WIDTH = 300.0F * Settings.scale;
        //Multipliers are slightly fudged to be more forgiving to longer names
        private static final float TITLE_BOX_WIDTH = IMG_WIDTH * 0.7F; //Was 0.6F
        private static final float TITLE_BOX_WIDTH_NO_COST = IMG_WIDTH * 0.8F; //Was 0.7F
        private static final GlyphLayout gl = new GlyphLayout();

        public static String buildName(AbstractCard card, boolean isSCV, BitmapFont renderFont) {
            //Build the name we will render
            String renderName = CardModifierManager.onRenderTitle(card, card.name);
            //If we changed the name we change the render font
            if (!renderName.equals(card.name)) {
                //Prep the font and determine width
                renderFont.getData().setScale(1.0F);
                gl.setText(renderFont, renderName, Color.WHITE, 0.0F, 1, false);
                if (isSCV) {
                    //SCV needs a 2x size allowance
                    renderFont.getData().setScale(Math.max(0.6f, Math.min(1, (card.cost == -2 ? TITLE_BOX_WIDTH_NO_COST * 2 : TITLE_BOX_WIDTH * 2) / gl.width)));
                } else {
                    //Cards need to multiply by their own scale
                    renderFont.getData().setScale(card.drawScale * Math.max(0.6f, Math.min(1, (card.cost == -2 ? TITLE_BOX_WIDTH_NO_COST : TITLE_BOX_WIDTH) / gl.width)));
                }
                gl.reset();
            }
            return renderName;
        }

        @SpirePatch2(clz = AbstractCard.class, method = "renderTitle")
        public static class CardModifierOnCardRender {
            @SpireInstrumentPatch
            public static ExprEditor patch() {
                return new ExprEditor() {
                    @Override
                    public void edit(MethodCall m) throws CannotCompileException {
                        if (m.getClassName().equals(FontHelper.class.getName()) && m.getMethodName().equals("renderRotatedText")) {
                            m.replace("{" +
                                    "$3 = " + CardModifierPatches.CardModifierOnRenderCardTitle.class.getName() + ".buildName(this, false, $2);" +
                                    "$proceed($$);" +
                                    "}");
                        }
                    }
                };
            }
        }

        @SpirePatch2(clz = SingleCardViewPopup.class, method = "renderTitle")
        public static class CardModifierOnSCVRender {
            @SpireInstrumentPatch
            public static ExprEditor patch() {
                return new ExprEditor() {
                    @Override
                    public void edit(MethodCall m) throws CannotCompileException {
                        if (m.getClassName().equals(FontHelper.class.getName()) && m.getMethodName().equals("renderFontCentered")) {
                            m.replace("{" +
                                    "$3 = " + CardModifierPatches.CardModifierOnRenderCardTitle.class.getName() + ".buildName(this.card, true, $2);" +
                                    "$proceed($$);" +
                                    "}");
                        }
                    }
                };
            }
        }
    }

    @SpirePatch2(clz = TinyCard.class, method = "getText")
    public static class CardModNamesInRunHistory {
        @SpireInstrumentPatch
        public static ExprEditor patch() {
            return new ExprEditor() {
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.getClassName().equals(AbstractCard.class.getName()) && f.getFieldName().equals("name")) {
                        f.replace("$_ = " + CardModifierManager.class.getName() + ".onRenderTitle($0, $proceed($$));");
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "makeStatEquivalentCopy"
    )
    public static class CardModifierStatEquivalentCopyModifiers
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"card"}
        )
        public static void Insert(AbstractCard __instance, AbstractCard card) {
            CardModifierManager.copyModifiers(__instance, card, false, true, false);
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "name");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "update"
    )
    public static class CardModifierUpdate
    {
        public static void Postfix(AbstractCard __instance) {
            CardModifierManager.onUpdate(__instance);
        }
    }

    @SpirePatch2(
            clz = AbstractCard.class,
            method = "render",
            paramtypez = {SpriteBatch.class, boolean.class}
    )
    @SpirePatch2(
            clz = AbstractCard.class,
            method = "renderInLibrary"
    )
    public static class CardModifierRender
    {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance, SpriteBatch sb) {
            CardModifierManager.onRender(__instance, sb);
        }
    }
    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "render"
    )
    public static class CardModifierSingleCardViewRender
    {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(SingleCardViewPopup __instance, SpriteBatch sb) {
            CardModifierManager.onSingleCardViewRender(__instance, sb);
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.MethodCallMatcher(Hitbox.class, "render");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "hasEnoughEnergy"
    )
    public static class CardModifierCanPlayCard
    {
        public static SpireReturn<Boolean> Prefix(AbstractCard __instance) {
            if (!CardModifierManager.canPlayCard(__instance)) {
                return SpireReturn.Return(false);
            } else {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "resetAttributes"
    )
    public static class CardModifierRemoveEndOfTurnModifiers
    {
        public static void Prefix(AbstractCard __instance) {
            CardModifierManager.removeEndOfTurnModifiers(__instance);
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = SpirePatch.CLASS
    )
    public static class CardModifierFields
    {
        public static SpireField<ArrayList<AbstractCardModifier>> cardModifiers = new SpireField<>(ArrayList::new);
        public static SpireField<Map<String, List<AbstractCardModifier>>> baseValueModifiers = new SpireField<>(HashMap::new);
    }

    @SpirePatch(
            clz = CardGroup.class,
            method = "moveToExhaustPile"
    )
    public static class CardModifierWhenExhausted
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(CardGroup __instance, AbstractCard c) {
            CardModifierManager.onCardExhausted(c);
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "triggerOnExhaust");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "draw",
            paramtypez = {int.class}
    )
    public static class CardModifierWhenDrawn
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"c"}
        )
        public static void Insert(AbstractPlayer __instance, int numCards, AbstractCard c) {
            CardModifierManager.onCardDrawn(c);
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "triggerWhenDrawn");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = UseCardAction.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class, AbstractCreature.class}
    )
    public static class CardModifierOnUseCard
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(UseCardAction __instance, AbstractCard card, AbstractCreature target) {
            if (!card.dontTriggerOnUseCard) {
                CardModifierManager.onUseCard(card, target, __instance);
                AbstractPlayer p = AbstractDungeon.player;
                for (AbstractCard c : p.hand.group) {
                    if (c != card) {
                        CardModifierManager.onOtherCardPlayed(c, card, p.hand);
                    }
                }
                for (AbstractCard c : p.drawPile.group) {
                    CardModifierManager.onOtherCardPlayed(c, card, p.drawPile);
                }
                for (AbstractCard c : p.discardPile.group) {
                    CardModifierManager.onOtherCardPlayed(c, card, p.discardPile);
                }
                CardModifierManager.removeWhenPlayedModifiers(card);
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "hand");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractCreature.class,
            method = "applyEndOfTurnTriggers"
    )
    public static class CardModifierAtEndOfTurn
    {
        public static void Postfix(AbstractCreature __instance) {
            AbstractPlayer p = AbstractDungeon.player;
            if (__instance == p) {
                for (AbstractCard c : p.drawPile.group) {
                    CardModifierManager.atEndOfTurn(c, p.drawPile);
                }
                for (AbstractCard c : p.discardPile.group) {
                    CardModifierManager.atEndOfTurn(c, p.discardPile);
                }
                for (AbstractCard c : p.hand.group) {
                    CardModifierManager.atEndOfTurn(c, p.hand);
                }
            }
        }
    }

    @SpirePatch(
            clz = RestoreRetainedCardsAction.class,
            method = "update"
    )
    public static class CardModifierOnRetained
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"e"}
        )
        public static void Insert(RestoreRetainedCardsAction __instance, AbstractCard e) {
            CardModifierManager.onCardRetained(e);
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "addToTop");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    public static class ModifierClassFilter implements ClassFilter {
        public boolean accept(ClassInfo info, ClassFinder finder) {
            if (info != null) {
                HashMap<String, ClassInfo> superClasses = new HashMap<>();
                finder.findAllSuperClasses(info, superClasses);
                if (superClasses.containsKey(AbstractCardModifier.class.getName())) {
                    return true;
                }
            }
            return false;
        }
    }

    public static RuntimeTypeAdapterFactory<AbstractCardModifier> modifierAdapter;

    public static void initializeAdapterFactory() {
        modifierAdapter = RuntimeTypeAdapterFactory.of(AbstractCardModifier.class, "classname");
        ClassFinder finder = new ClassFinder();
        for (ModInfo info : Loader.MODINFOS) {
            if (info.jarURL != null) {
                try {
                    finder.add(new File(info.jarURL.toURI()));
                } catch (URISyntaxException ignored) {

                }
            }
        }
        AndClassFilter filter = new AndClassFilter(
                new NotClassFilter(new AbstractClassFilter()),
                new ModifierClassFilter()
        );
        ArrayList<ClassInfo> cardModifiers = new ArrayList<>();
        finder.findClasses(cardModifiers, filter);
        for (ClassInfo info : cardModifiers) {
            try {
                Class c = Class.forName(info.getClassName());
                if (!c.isAnnotationPresent(AbstractCardModifier.SaveIgnore.class)) {
                    modifierAdapter.registerSubtype(c, info.getClassName());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static final String CARDMODS_KEY = "basemod:card_modifiers";
    public static HashMap<RunData, ModSaves.ArrayListOfJsonElement> cardmodDataMap = new HashMap<>();

    @SpirePatch2(clz = Metrics.class, method = "gatherAllData")
    public static class AddCardmodsToMetrics {
        public static ModSaves.ArrayListOfJsonElement createCardmodData() {
            //Master deck AbstractCardModifiers
            ModSaves.ArrayListOfJsonElement cardModifierSaves = new ModSaves.ArrayListOfJsonElement();
            GsonBuilder builder = new GsonBuilder();
            if (CardModifierPatches.modifierAdapter == null) {
                CardModifierPatches.initializeAdapterFactory();
            }
            builder.registerTypeAdapterFactory(CardModifierPatches.modifierAdapter);
            Gson gson = builder.create();
            for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                ArrayList<AbstractCardModifier> cardModifierList = CardModifierPatches.CardModifierFields.cardModifiers.get(card);
                ArrayList<AbstractCardModifier> saveIgnores = new ArrayList<>();
                for (AbstractCardModifier mod : cardModifierList) {
                    if (mod.getClass().isAnnotationPresent(AbstractCardModifier.SaveIgnore.class)) {
                        saveIgnores.add(mod);
                    }
                }
                if (!saveIgnores.isEmpty()) {
                    BaseMod.logger.warn("attempted to save un-savable card modifiers. Modifiers marked @SaveIgnore will not be saved on master deck.");
                    BaseMod.logger.info("affected card: " + card.cardID);
                    for (AbstractCardModifier mod : saveIgnores) {
                        BaseMod.logger.info("saveIgnore mod: " + mod.getClass().getName());
                    }
                    cardModifierList.removeAll(saveIgnores);
                }
                if (!cardModifierList.isEmpty()) {
                    cardModifierSaves.add(gson.toJsonTree(cardModifierList, new TypeToken<ArrayList<AbstractCardModifier>>(){}.getType()));
                } else {
                    cardModifierSaves.add(null);
                }
            }
            return cardModifierSaves;
        }

        @SpirePostfixPatch
        public static void addDataToMetrics(Metrics __instance) {
            ReflectionHacks.RMethod addDataMethod = ReflectionHacks.privateMethod(Metrics.class, "addData", Object.class, Object.class);
            addDataMethod.invoke(__instance, CARDMODS_KEY, createCardmodData());
        }
    }

    @SpirePatch2(clz = RunHistoryScreen.class, method = "refreshData")
    public static class DataFromGson {
        @SpirePrefixPatch
        public static void setupMap() {
            cardmodDataMap = new HashMap<>();
        }

        @SpireInsertPatch(locator = Locator.class, localvars = {"file", "data"})
        public static void loadDataFromGson(RunHistoryScreen __instance, FileHandle file, RunData data) {
            JsonObject reparsedData = (new Gson()).fromJson(file.readString(), JsonObject.class);
            ModSaves.ArrayListOfJsonElement runData = null;
            for (Map.Entry<String, JsonElement> entry : reparsedData.entrySet()) {
                if (entry.getKey().equals(CARDMODS_KEY) && entry.getValue().isJsonArray()) {
                    runData = new ModSaves.ArrayListOfJsonElement();
                    JsonArray a = entry.getValue().getAsJsonArray();
                    for (int i = 0 ; i < a.size() ; i++) {
                        runData.add(a.get(i));
                    }
                }
            }
            cardmodDataMap.put(data, runData);
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws PatchingException, CannotCompileException {
                Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "add");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList(), methodCallMatcher);
            }
        }
    }

    @SpirePatch2(clz = RunHistoryScreen.class, method = "reloadCards")
    public static class ApplyCardModsToCards {
        static int i = -1;
        static ModSaves.ArrayListOfJsonElement cardmodData = null;
        static GsonBuilder builder = null;
        static Gson gson = null;
        static final ArrayList<AbstractCardModifier> loadedMods = new ArrayList<>();
        static String idBackup = "";

        @SpirePrefixPatch
        public static void reset(RunData runData) {
            i = 0;
            cardmodData = cardmodDataMap.getOrDefault(runData, null);
            if (cardmodData != null) {
                builder = new GsonBuilder();
                if (CardModifierPatches.modifierAdapter == null) {
                    CardModifierPatches.initializeAdapterFactory();
                }
                builder.registerTypeAdapterFactory(CardModifierPatches.modifierAdapter);
                gson = builder.create();
            }
        }

        @SpireInsertPatch(locator = LocatorLoad.class, localvars = {"cardID"})
        public static void loadMods(@ByRef String[] cardID) {
            loadedMods.clear();
            idBackup = cardID[0];
            if (cardmodData != null) {
                AbstractCardModifier cardModifier;
                if (cardmodData.get(i).isJsonArray()) {
                    JsonArray array = cardmodData.get(i).getAsJsonArray();
                    for (JsonElement element : array) {
                        try {
                            cardModifier = gson.fromJson(element, new TypeToken<AbstractCardModifier>(){}.getType());
                            loadedMods.add(cardModifier);
                            cardID[0] += "&"+element;
                        } catch (Exception ignored) {
                            BaseMod.logger.warn("[Run History] - Unable to load cardmod: " + element);
                        }
                    }
                }
                i++;
            }
        }

        public static String getIdBackup() {
            return idBackup;
        }

        @SpireInstrumentPatch
        public static ExprEditor fixID() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(RunHistoryScreen.class.getName()) && m.getMethodName().equals("cardForName")) {
                        m.replace("$2 = "+ApplyCardModsToCards.class.getName()+".getIdBackup(); $_ = $proceed($$);");
                    }
                }
            };
        }

        @SpireInsertPatch(locator = LocatorApply.class, localvars = {"cardID","card"})
        public static void applyMods(RunData runData, @ByRef String[] cardID, AbstractCard card) {
            if (cardmodData != null && card != null) {
                CardModifierManager.removeAllModifiers(card, true);
                for (AbstractCardModifier mod : loadedMods) {
                    CardModifierManager.addModifier(card, mod.makeCopy());
                }
            }
        }

        public static class LocatorLoad extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.MethodCallMatcher(Hashtable.class, "containsKey");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }

        public static class LocatorApply extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.MethodCallMatcher(RunHistoryScreen.class, "cardForName");
                return new int[]{LineFinder.findInOrder(ctBehavior, m)[0]+1};
            }
        }
    }
}
