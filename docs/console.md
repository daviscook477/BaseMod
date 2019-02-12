---
id: console
title: Console (BaseMod)
sidebar_label: Console
---

Use the `` ` `` key to open the console while playing Slay the Spire with BaseMod. This can be very helpful for debugging. The keybinding can be changed in the settings for BaseMod.

## Commands

### Deck Modification
* `deck add [id] {cardcount} {upgrades}` add card to deck (optional: integer # of times you want to add this card) (optional: integer # of upgrades)
* `deck remove [id]` remove card from deck
* `deck remove all` remove all cards from deck

### During Combat
* `draw [num]` draw cards
* `energy add [amount]` gain energy
* `energy inf` toggles infinite energy
* `energy remove [amount]` lose energy
* `hand add [id] {cardcount} {upgrades}` add card to hand with (optional: integer # of times to add the card) (optional: integer # of upgrades)
* `hand remove all` exhaust entire hand
* `hand remove [id]` exhaust card from hand
* `kill all` kills all enemies in the current combat
* `kill self` kills your character
* `power [id] [amount]` bring up a targetting reticle to apply amount stacks of a power to either the player or an enemy

### Outside of Combat
* `fight [name]` enter combat with the specified encounter
* `event [name]` start event with the specified name

### Anytime
* `gold add [amount]` gain gold
* `gold lose [amount]` lose gold
* `info toggle` Settings.isInfo
* `potion [pos] [id]` gain specified potion in specified slot (0, 1, or 2)
* `hp add [amount]` heal amount
* `hp remove [amount]` hp loss
* `maxhp add [amount]` gain max hp
* `maxhp remove [amount]` lose max hp
* `debug [true/false]` sets `Settings.isDebug`
### Relics
* `relic add [id]` generate relic
* `relic list` logs all relic pools
* `relic remove [id]` lose relic

### Unlocks
* `unlock always` always gain an unlock on death
* `unlock level [level]` set the gained unlock to be the specified level