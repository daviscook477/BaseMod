package basemod.abstracts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public abstract class AbstractCardModifier implements Comparable<AbstractCardModifier> {
    public int priority = 0;

    /**
     * Methods determine times when the card mod will be removed from the card.
     * Any number of these can be set to return true, and are implemented as methods
     * to give the user more control of the case-by-case.
     */
    public boolean removeOnCardPlayed(AbstractCard card) {
        return false;
    }

    //note: this method behaves the same way the game treats temporary costs. Any time a "costForTurn" would be reset, this is called.
    public boolean removeAtEndOfTurn(AbstractCard card) {
        return false;
    }

    /**
     * method determines whether this modifier should be counted as a permanent part of the card. For example, if you
     * create a modifier that would be applied to a card in its own constructor, and it's to be considered an immutable
     * part of it, not removable by effects that can remove card modifiers, have this method return true.
     */
    public boolean isInherent(AbstractCard card) {
        return false;
    }

    /**
     * Methods modify the relevant stat as it's being calculated on the game's end, without
     * interfering with normal card stats. The return is the new value used by the game.
     * all card parameters pass the instance of the card which the mod is applied to.
     * AbstractMonster parameters will pass null when called from ApplyPowers.
     */
    //called before related power functions, for flat increases, to happen before vulnerable.
    public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage;
    }

    //called before "final" power functions, to apply percentage increases, but before things like intangible.
    public float modifyDamageFinal(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage;
    }

    //called before power functions, for flat increases.
    public float modifyBlock(float block, AbstractCard card) {
        return block;
    }

    //called after power functions, for percentage and immutable(panic button-like) changes.
    public float modifyBlockFinal(float block, AbstractCard card) {
        return block;
    }

    /**
     * Intercepts the creation of a card's description at the time that initializeDescription is called.
     * rawDescription can be manipulated freely at this point, and then the game will parse it as normal.
     */
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription;
    }

    /**
     * Called when the card is used, same timing as onUseCard hooks in powers and relics.
     * If the card has no target, target will be null.
     */
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {

    }

    /**
     * called when the card is drawn.
     */
    public void onDrawn(AbstractCard card) {

    }

    /**
     * called when the card is exhausted.
     */
    public void onExhausted(AbstractCard card) {

    }

    /**
     * called when the mod is initially applied to the card, including when
     * a new instance of a card is created, and mods are copied to that new card.
     */
    public void onInitialApplication(AbstractCard card) {

    }

    public void onRemove(AbstractCard card) {

    }

    /**
     * called whenever applyPowers is called on a card. Useful for if you want an
     * effect to be tied to the existence of a power, or otherwise somehow dynamic.
     */
    public void onApplyPowers(AbstractCard card) {

    }

    public void onCalculateCardDamage(AbstractCard card, AbstractMonster mo) {

    }

    /**
     * update/render hooks. Can be used to attach particle systems to a card, or a bottle icon, or similar.
     */
    public void onUpdate(AbstractCard card) {

    }

    public void onRender(AbstractCard card, SpriteBatch sb) {

    }

    /**
     * triggers at the end of the player's turn. The group passed is the current location of the card at the
     * time that this method is called.
     */
    public void atEndOfTurn(AbstractCard card, CardGroup group) {

    }

    /**
     * triggers when another card is played.
     * @param card - the card which this modifier is applied to
     * @param otherCard - the card that was played
     * @param group - the current location of the card at the time this method is called.
     */
    public void onOtherCardPlayed(AbstractCard card, AbstractCard otherCard, CardGroup group) {

    }

    /**
     * return false to make the card unplayable.
     */
    public boolean canPlayCard(AbstractCard card) {
        return true;
    }

    /**
     * triggers whenever the card is retained. Same conditions as AbstractCard.onRetained.
     */
    public void onRetained(AbstractCard card) {

    }

    /**
     * needs to be overridden by all CardModifiers, since CardModifiers are, by default, copied between instances.
     */
    public abstract AbstractCardModifier makeCopy();

    /**
     * for use with hasModifier and getModifier methods. If you have no reason to use those methods,
     * then there's no need to establish an ID.
     */
    public String identifier(AbstractCard card) {
        return "";
    }

    /**
     * return false to make the modifier not apply to the passed card.
     */
    public boolean shouldApply(AbstractCard card) {
        return true;
    }

    /**
     * lower number = calculates first. For list sorting purposes. Don't override. Or do, I'm a comment, not a cop.
     */
    @Override
    public int compareTo(AbstractCardModifier other) {
        return priority - other.priority;
    }

    /**
     * By default, card modifiers are saved when placed on a master deck card. This does, however, mean that if a field
     * exists in a modifier that cannot be saved, the game will crash as soon as you try to load any game, no matter if
     * that modifier is used anywhere. To prevent this issue, a Card Modifier can be flagged as not savable with this
     * annotation.
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SaveIgnore
    {
    }
}
