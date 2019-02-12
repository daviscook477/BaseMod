---
id: custom-potions
title: Custom Potions
---

* `ArrayList<String> getPotionsToRemove()` - list of potions that are being removed.
* `void removePotion(String potionID)` - remove a potion from the game.
* `void addPotion(Class potionClass, Color liquidColor, Color hybridColor, Color spotsColor, String potionID)` - add a new custom potion to the game.
* `void addPotion(Class potionClass, Color liquidColor, Color hybridColor, Color spotsColor, String potionID, AbstractPlayer.PlayerClass playerClass)` - add a new class specific custom potion to the game.
