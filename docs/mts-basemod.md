---
id: mts-basemod
title: ModTheSpire & BaseMod
---

## What is ModTheSpire?

ModTheSpire allows modders to create patches to inject code into Slay the Spire. Patches are the fundamental buidling block of modding Slay the Spire. ModTheSpire is also the tool used to load mods into Slay the Spire so you can play your own mods and mods other people create.

## What is BaseMod?

BaseMod is a modding API for Slay The Spire that allows for modders to more easily hook into the functionality of the base game without having to go through the tedious process of creating lots of bytecode patches. 

It supports things like:
1. Adding custom relics
1. Adding custom cards (with entirely new card colors)
1. Adding custom player classes
1. Adding custom potions
1. Adding custom events
1. Adding custom monsters

Future plans are to expand this API to encompass even more parts of the game .

## Should I use ModTheSpire or BaseMod?

You should use both! BaseMod is built off of the functionality provided by ModTheSpire. ModTheSpire is what allows us to modify the base game to inject patches. BaseMod is designed to simplify the modding process by providing API hooks into the base game rather than requiring every mod to try and individually create patches. BaseMod reduces modding overhead and allows for better code reuse.