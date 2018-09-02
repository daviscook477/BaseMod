---
id: modal-card-choices
title:  Modal Card Choices
---

Modal choice cards allow cards to present a choice to the player when used.

For example, if you wanted a card that gave the player a random red, green, or colorless card and wanted to give the player the choice what color card they got, it's possible with a modal choice card (https://streamable.com/gxx7t). The code for this example card is available at the bottom of this page.

## API ##

### ModalChoiceBuilder ###

To create a modal choice, you must use the `ModalChoiceBuilder` class.

#### `setTitle` ####
Sets the banner title of the modal choice. If not used, banner title defaults to "Choose an Option".

#### `addOption` ####
There are three options for adding an option:
* `addOption(String description, AbstractCard.CardTarget target)`
    * Adds an option card using a description and target. Option title will be "Option X" where X is the option's number. If chosen, calls the callback. Affected by `setColor` and `setCallback`.
* `addOption(String title, String description, AbstractCard.CardTarget target)`
    * Adds an option card using a title, description, and target. If chosen, calls the callback. Affected by `setColor` and `setCallback`.
* `addOption(AbstractCard card)`
    * Adds a custom card type. The `use()` method of the card is called if the option is chosen.

#### `setColor` ####
Sets the card color of any future options cards for this modal choice.

#### `setCallback` ####
Sets the callback of any future options cards for this modal choice.

#### `create` ####
Finalizes and returns the `ModalChoice` object.

## ModalChoice ##

#### `open` ####
Opens to modal choice screen, providing the options to the player. Generally used in your card's `use()` method.

#### `generateTooltips` ####
Automatically generates tooltips for the modal options. To be used in `getCustomTooltips()` if your card inherits from `CustomCard`.

#### `Callback` ####
A callback must implement the `ModalChoice.Callback` interface. When automatic option cards are chosen, their callback is called with the chosen card's index.

## Example Modal Card Code ##

```Java
import basemod.abstracts.CustomCard;
import basemod.helpers.ModalChoice;
import basemod.helpers.ModalChoiceBuilder;
import basemod.helpers.TooltipInfo;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.List;

public class ModalTest extends CustomCard implements ModalChoice.Callback
{
    public static final String ID = "ModalTest";
    public static final String NAME = "Modal Test";
    public static final String DESCRIPTION = "Choose Red, Green, or Colorless. NL Gain a random card of that color.";
    private static final int COST = 0;
    private ModalChoice modal;

    public ModalTest()
    {
        super(ID, NAME, null, COST, DESCRIPTION, CardType.SKILL, CardColor.COLORLESS, CardRarity.BASIC, CardTarget.NONE);

        modal = new ModalChoiceBuilder()
                .setCallback(this) // Sets callback of all the below options to this
                .setColor(CardColor.RED) // Sets color of any following cards to red
                .addOption("Add a random Red card to your hand.", CardTarget.NONE)
                .setColor(CardColor.GREEN) // Sets color of any following cards to green
                .addOption("Add a random Green card to your hand.", CardTarget.NONE)
                .setColor(CardColor.COLORLESS) // Sets color of any following cards to colorless
                .addOption("Add a random Colorless card to your hand.", CardTarget.NONE)
                .create();
    }

    // Uses the titles and descriptions of the option cards as tooltips for this card
    @Override
    public List<TooltipInfo> getCustomTooltips()
    {
        return modal.generateTooltips();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m)
    {
        modal.open();
    }

    // This is called when one of the option cards us chosen
    @Override
    public void optionSelected(AbstractPlayer p, AbstractMonster m, int i)
    {
        CardColor color;
        switch (i) {
            case 0:
                color = CardColor.RED;
                break;
            case 1:
                color = CardColor.GREEN;
                break;
            case 2:
                color = CardColor.COLORLESS;
                break;
            default:
                return;
        }

        AbstractCard c;
        if (color == CardColor.COLORLESS) {
            c = AbstractDungeon.returnTrulyRandomColorlessCard(AbstractDungeon.cardRandomRng).makeCopy();
        } else {
            c = CardLibrary.getColorSpecificCard(color, AbstractDungeon.cardRandomRng).makeCopy();
        }
        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(c, true));
    }

    @Override
    public void upgrade()
    {
        if (!upgraded) {
            upgradeName();
        }
    }

    @Override
    public AbstractCard makeCopy()
    {
        return new ModalTest();
    }
}
```