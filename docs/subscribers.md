---
id: subscribers
title: Subscribers (BaseMod)
sidebar_label: Subscribers
---

BaseMod uses a subscription model to handle hooks

## New Subscription Example
* `BaseMod.subscribe(this)` where `this` is an instance of some subscriber, this method will subscribe `this` to **all** hooks it implements subscribers for.
* `BaseMod.unsubscribe(this)` where `this` is an instance of some subscriber, this method will unsubscribe `this` from **all** hooks it implements subscribers for.
* `BaseMod.subscribe(this, Class<? extends ISubscriber> toAddClass)` where `this` is an instance of some subscriber, this method will subscribe `this` to **only** the `toAddClass` hook.
* `BaseMod.unsubscribe(this, Class<? extends ISubscriber> toRemoveClass)` where `this` is an instance of some subscriber, this method will unsubscribe `this` from **only** the `toRemoveClass` hook.

## Old Subscription Example (Deprecated API)
* `BaseMod.subscribeTo...(this)`
* `BaseMod.unsubscribeFrom...(this)`

## UnsubscribeLater
Sometimes you need to unsubscribe from an event after it's triggered once. Maybe you only want to listen for the first time a card is played or the first time a dungeon is set up. In that case you're going to want to `unsubscribe` from a hook **in** the callback method for the hook. However if you were to directly call `BaseMod.unsubscribe` in your `receiveSomeReallyCoolEvent` method you will throw a `ConcurrentModificationException` which is what happens in Java when you try and modify a list at the same time it is being iterated over. To avoid this exception you need to use `BaseMod.unsubscribeLater` which will register your subscriber to be unsubscribed **after** the bit of code that could throw the exception.

# List of Hooks

This is **NOT A COMPLETE LIST** of hooks. Check [src/main/java/basemod/interfaces](https://github.com/daviscook477/BaseMod/tree/master/src/main/java/basemod/interfaces) for a complete list.

## Cards
* `void receivePostDraw(AbstractCard)` - After a card is drawn.
* `void receivePostExhaust(AbstractCard)` - After a card is exhausted.
* `void receiveCardUse(AbstractCard)` - Directly after a card is used (can be used to add additional functionality to cards on use).

## Energy
* `void receivePostEnergyRecharge()` - At the start of every player turn, after energy has recharged.

## Rendering
* `void receiveRender(SpriteBatch)` - Under tips and the cursor, above everything else.
* `void receivePostRender(SpriteBatch)` - Above everything.

## Gameplay
* `void receivePostInitialize()` - One time only, at the end of `CardCrawlGame.initialize()`.
* `void receivePostDungeonInitialize()` - After dungeon initialization completes.
* `boolean receiveStartAct()` - After a new act is started - only occurs when the player transitions between acts **OR** starts a new game and begins the Exordium act.
* `boolean receivePostCampfire()` - After a campfire action is performed. Returning false will allow another action to be performed.
* `void receivePreStartGame()` - When starting a new game or continuing, before generating/loading the player.
* `void receiveStartGame()` - When starting a new game or continuing, after generating/loading the player and before dungeon generation.
* `void receivePreUpdate()` - Immediately after input is read.
* `void receivePostUpdate()` - Immediately before input is disposed.

## Starting Deck / Relics
* `boolean receivePostCreateStartingDeck(cardsToAdd)` - Immediately after the character's starting deck is created. Returning true will remove all the cards from the default starting deck. Add the cards you want to be in the starting deck to `cardsToAdd`.
* `boolean receievePostCreateStartingRelics(relicsToAdd)` - Immediately after the character's starting relics are created. Returning true will remove all the default relics from the player. Add the relics you want to be in the starting deck to `relicsToAdd`.

## Shop
* `void receivePostCreateShopRelics(relics, sceenInstance)` - Immediately after the shop generates its relics. Modifying `relics` will change the relics in the shop. `screenInstance` is an instance of the `ShopScreen`.
* `void receivePostCreateShopPotions(potions, screenInstance)` - Immediately after the shop generates its potions. Modifying `potions` will change the potions in the shop. `screenInstance` is an instance of the `ShopScreen`.

## Potions
* `void receivePostPotionUse(AbstractPotion potion)` - Immediately after a potion is used.
* `void receivePrePotionUse(AbstractPotion potion)` - Immediately before a potion is used.
* `void receivePotionGet(AbstractPotion potion)` - When the player gets a potion.

## Custom Content
* `void receiveEditCards` - When you should register any cards to add or remove with `BaseMod.addCard` and `BaseMod.removeCard`. Do **NOT** initialize any cards or register any cards to add or remove outside of this handler. Slay the Spire needs some things to be done in certain orders and this handler ensures that happens correctly. Note that removing any cards involved in game events is **undefined behavior** currently.
* `void receiveEditRelics` - When you should register any relics to add or remove with `BaseMod.addRelic` and `BaseMod.removeRelic`. Do **NOT** initialize any relics or register any relics to add or remove outside of this handler. Slay the Spire needs some things to be done in certain orders and this handler ensures that happens correctly. Note that removing any relics involved in game events is **undefined behavior** currently.
* `void receiveEditCharacters` - When you should register any characters to add or remove with `BaseMod.addCharacter` and `BaseMod.removeCharacter`. Do **NOT** initialize any characters or register any relics to add or remove outside of this handler. Slay the Spire needs some things to be done in certain orders and this handler ensures that happens correctly. Note that removing the default characters **IS NOT** supported at this time.
* `void receiveSetUnlocks` - When you should register any custom unlocks. Note that removing any unlocks that exist in the base game won't work (it shouldn't crash but it won't do anything). Do **NOT** set up any custom unlocks outside of this handler. Slay The Spire needs some things to be done in certain orders and this handler ensures that happens correctly.