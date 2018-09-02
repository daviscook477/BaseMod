---
id: custom-cards
title: Custom Cards
---

## CustomCard Constructor
`CustomCard(String id, String name, String img, int cost, String rawDescription, CardType type, CardColor color, CardRarity rarity, CardTarget target)`
* `id` - the card id
* `name` - the name of the card
* `img` - the path to the img for this card (image path starts at the root of your `jar`) (250px x 190px); the path to your larger version of the img (500 x 380p) should be img + "_p" so if img is "my_card.png" then the portrait version should be at "my_card_p.png"
* `cost` - the energy cost of the card
* `rawDescription` - the description for the card
* `type` - the card type, e.g. `ATTACK`, `SKILL`, `POWER`
* `color` - the color of the card; base game options are `RED`, `GREEN`, `COLORLESS`, `CURSE`, `STATUS` however you can use custom colors here too
* `rarity` - the card rarity, e.g. `COMMON`, `UNCOMMON`, `RARE`
* `target` - the type of target for the card, e.g. does it target `ENEMY`, `ALL_ENEMIES`, `SELF`, etc...

## Custom Per-Card Textures
The following methods can be called in the constructor, however, if you want to change the texture later you can call these at any point to change the texture.

### Card Background.
`setBackgroundTexture(String smallTexturePath, String largeTexturePath)`
* `smallTexturePath` - the path to the texture (512 x 512p).
* `largeTexturePath` - the path to the inspect view texture (1024 x 1024p).
### Card Energy Orb.
`setOrbTexture(String smallTexturePath, String largeTexturePath)`
* `smallTexturePath` - the path to the orb texture (512 x 512p).
* `largeTexturePath` - the path to the inspect view orb texture (164 x 164p).
### Card Rarity Banner.
`setBannerTexture(String smallTexturePath, String largeTexturePath)`
* `smallTexturePath` - the path to the texture (512 x 512p).
* `largeTexturePath` - the path to the inspect view texture (1024 x 1024p).

## Registering
In order to use the methods below to add or remove cards you must implement `EditCardsSubscriber` and then override the `receiveEditCards` method. Inside that method is when you should add or remove cards.

* `BaseMod.addCard(AbstractCard card)` (note: `CustomCard` extends `AbstractCard`).
* `BaseMod.removeCard(AbstractCard card)` removes a card from the game (note: removing a card used by an event is currently untested/undefined behavior)

## Example of CustomCard

Let's say you wanted to create a card called `Flare` that would deal `3` damage to an enemy and apply `1` stack of vulnerable to it unupgraded and would deal `6` damage to the enemy and apply `2` stacks of vulnerable when upgraded. 

If the card is going to be `RED` and have it's art located at `img/my_card_img.png` in you jar file, the following code would create this card:

```java
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;

import basemod.abstracts.CustomCard;

public class Flare
extends CustomCard {
    public static final String ID = "Flare";
    public static final String NAME = "Flare";
    public static final String DESCRIPTION = "Deal !D! damage. Apply !M! Vulnerable.";
    public static final String IMG_PATH = "img/my_card_img.png";
    private static final int COST = 0;
    private static final int ATTACK_DMG = 3;
    private static final int UPGRADE_PLUS_DMG = 3;
    private static final int VULNERABLE_AMT = 1;
    private static final int UPGRADE_PLUS_VULNERABLE = 1;

    public Flare() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
        		AbstractCard.CardType.ATTACK, AbstractCard.CardColor.RED,
        		AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.ENEMY);
        this.magicNumber = this.baseMagicNumber = VULNERABLE_AMT;
        this.damage=this.baseDamage = ATTACK_DMG;
        
        this.setBackgroundTexture("img/custom_background_small.png", "img/custom_background_large.png");

        this.setOrbTexture("img/custom_orb_small.png", "img/custom_orb_large.png");

        this.setBannerTexture("img/custom_banner_large.png", "img/custom_banner_large.png");
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    	AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.common.DamageAction(m,
				new DamageInfo(p, this.damage, this.damageTypeForTurn),
				AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
    	AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new VulnerablePower(m, this.magicNumber, false), this.magicNumber, true, AbstractGameAction.AttackEffect.NONE));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Flare();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeDamage(UPGRADE_PLUS_DMG);
            this.upgradeMagicNumber(UPGRADE_PLUS_VULNERABLE);
        }
    }
}
```

## Note about Inspect View

In the Card Library screen in the Compendium there is an inspect view that brings up a larger version of your card. This is found automatically based off of the original image location by adding a `_p` to the name. This art should have size 500px x 380px. An example of how the name changes works is `img/my_card.png` becomes `img/my_card_p.png`.




