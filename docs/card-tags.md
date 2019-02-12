---
id: card-tags
title: Card Tags
---

Cards can be tagged with arbitrary tags to keep track of flags on cards.

To add a tag to a card, use `CardTags.addTags(card, tag)`.
You can check if a card has a tag with `CardTags.hasTag(card, tag)`.

## Default Tags
BaseMod comes with some tags by default:
* `BaseModTags.BASIC_STRIKE`
  * A card tagged with this counts as a basic Strike card for events such as The Vampires and Back to Basics.
* `BaseModTags.BASIC_DEFEND`
  * A card tagged with this counts as a basic Defend card for events such as Back to Basics.
* `BaseModTags.GREMLIN_MATCH`
  * A card tagged with this is added to the Gremlin Match Game event. A custom character **must** tag at least one card with this or the Gremlin Match Game event will crash.
* `BaseModTags.STRIKE`
  * A card tagged with this counts as a "Strike" for Perfected Strike.
  * Your basic Strike should be tagged with both `BASIC_STRIKE` and `STRIKE` if it is to work with Perfected Strike.
* `BaseModTags.FORM`
  * A card tagged with this counts like Demon Form, Wraith Form, and Echo Form for the My True Form custom modifier.

```Java
public TestStrike()
{
    super(ID, NAME, IMG, COST, DESCRIPTION, CardType.ATTACK, CardColor.RED, CardRarity.RARE, CardTarget.ENEMY);

    CardTags.addTags(this, BaseModTags.BASIC_STRIKE, BaseModTags.STRIKE);
    ...
}
```

## Custom Tags
You can define your own tags like so:
```Java
public class CustomTags
{
	@CardTags.AutoTag public static CardTags.Tag MY_TAG;
	@CardTags.AutoTag public static CardTags.Tag MY_OTHER_TAG;
	@CardTags.AutoTag public static CardTags.Tag LOOK_AT_ME;
}
```