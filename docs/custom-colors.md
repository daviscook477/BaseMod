---
id: custom-colors
title: Custom Colors
---

## Description

In the course of trying to develop a new character for the game it is inevitable that you'll want to create a new color for that character. Just like The Ironclad is red and The Silent is green, your custom character will need a color, maybe cyan or orange or yellow.

## Enums

The base game denotes color using the `AbstractCard.CardColor` enum so in order to add a new color to the game you must patch the enum using `ModTheSpire`'s enum patching feature in addition to registering it with **BaseMod**. Here is an example of how to do so:
```java
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class AbstractCardEnum {

	@SpireEnum
	public static AbstractCard.CardColor MOD_NAME_COLOR;
	
}
```
and
```java
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.helpers.CardLibrary;

public class LibraryTypeEnum {

	@SpireEnum
	public static CardLibrary.LibraryType MOD_NAME_COLOR;
	
}


```

## How to Use
In addition to defining a patch to the `CardColor` enum you will need to register the custom color with BaseMod so it can properly implement the rest of the features related to that color. For example you'll need to specify a path to the energyOrb and to the card backgrounds. To do this you will call the `addColor` method detailed in the `API` section of this wiki page. However it is **very important** that `addColor` is called in your `@SpireInitializer` method. Do not call it in `postInitialize` or any of your other hooks. Generally you want to prefix your color with your mods name so that in case you are using the same color as someone else you won't run into unexpected compatibility problems.

## API
```Java
addColor(AbstractCard.CardColor color, Color bgColor, Color backColor, Color frameColor, Color frameOutlineColor, Color descBoxColor, Color trailVfxColor, Color glowColor, String attackBg, String skillBg, String powerBg, String energyOrb, String attackBgPortrait, String skillBgPortrait, String powerBgPortrait, String energyOrbPortrait, String cardEnergyOrb)
```

* `color` - this should be `MY_CUSTOM_COLOR`
* `bgColor` - background color
* `backColor` - back color
* `frameColor` - frame color
* `frameOutlineColor` - frame outline color
* `descBoxColor` - the description box color
* `glowColor` - glow color
* `attackBg` - path to your attack background image (paths start at the root of your jar)
* `skillBg` - path to your skill background image (paths start at the root of your jar)
* `powerBg` - path to your power background image (paths start at the root of your jar)
* `energyOrb` - path to your energy orb image (paths start at the root of your jar)
* `attackBgPortrait` - path to your attack background image for the card inspect view (paths start at the root of your jar)
* `skillBgPortrait` - path to your skill background image for the card inspect view (paths start at the root of your jar)
* `powerBgPortrait` - path to your power background image for the card inspect view (paths start at the root of your jar)
* `energyOrbPortrait` - path to your energy orb image for the card inspect view (paths start at the root of your jar)
* `cardEnergyOrb` - path to your small energy orb image for card descriptions

```Java
addColor(AbstractCard.CardColor color, Color everythingColor, String attackBg, String skillBg, String powerBg, String energyOrb, String attackBgPortrait, String skillBgPortrait, String powerBgPortrait, String energyOrbPortrait, String cardEnergyOrb)
```
* `everythingColor` - use the same color for all the color parameters of the previous version