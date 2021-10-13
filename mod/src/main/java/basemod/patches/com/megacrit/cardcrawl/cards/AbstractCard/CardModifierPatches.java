package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.megacrit.cardcrawl.actions.unique.RestoreRetainedCardsAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import org.clapper.util.classutil.*;
import com.evacipated.cardcrawl.modthespire.Loader;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

public class CardModifierPatches
{
    @SpirePatch(
            clz = AbstractCard.class,
            method = "calculateCardDamage"
    )
    public static class CardModifierCalculateCardDamage
    {
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
                localvars = {"tmp", "i"}
        )
        public static void multiDamageInsert(AbstractCard __instance, AbstractMonster m, float[] tmp, int i) {
            tmp[i] = CardModifierManager.onModifyDamage(tmp[i], __instance, m);
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
                localvars = {"tmp", "i"}
        )
        public static void multiDamageFinalInsert(AbstractCard __instance, AbstractMonster m, float[] tmp, int i) {
            tmp[i] = CardModifierManager.onModifyDamageFinal(tmp[i], __instance, m);
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
            method = "applyPowers"
    )
    public static class CardModifierOnApplyPowers
    {
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

    @SpirePatch(
            clz = AbstractCard.class,
            method = "render",
            paramtypez = {SpriteBatch.class}
    )
    public static class CardModifierRender
    {
        public static void Postfix(AbstractCard __instance, SpriteBatch sb) {
            CardModifierManager.onRender(__instance, sb);
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
}
