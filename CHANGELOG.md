## Changelog ##
#### v1.0.0 ####
* Initial release

#### v1.0.1 ####
* Scale console by Settings.scale
* Prevent game hotkeys from activating while console is visible

#### v1.1.0 ####
* Add mod badges
* Add initial support for mod settings screens
* Add `relic` console command
* Add option to change console keybind on BaseMod settings screen

#### v1.1.1 #####
* Scale mod badges by Settings.scale
* Scale mod settings screens by Settings.scale

#### v1.1.2 #####
* Fix bug with IDs which contain spaces (FlipskiZ)
* Add `card` console command (FlipskiZ)
* Add `kill all` console command

#### v1.1.3 #####
* Initial support for each mod badge being tied to its own settings panel

#### v1.1.4 #####
* Add `gold` command
* Add `energy` command
* Remove bundled font and use one from the base game instead

#### v1.1.5 ####
* Add `energy inf` command
* Add `PostEnergyRechargeSubscriber` interface and related code

#### v1.2.0 ####
* Add `PostDrawSubscriber` interface and related code
* Add `CustomRelic` extension of AbstractRelic
* Add support for loading custom RelicStrings
* Fix a bug that prevented the character `D` from being input into the console
* Rename `card` command to `hand`
* Add `hand r all` command
* Add `deck` command
* Add `draw` command
* Add `float BaseMod.pathDensityMultiplier` property which can be used to modify map generation

#### v1.2.1 ####
* Add `PreStartGameSubscriber` interface and related code

#### v1.2.2 ####
* Add `AbstractDungeon` cleanup to diff, nothing changed yet.
* Add `relic list` command
* Fix crash when attempting to `deck add` an invalid card id
* Add upgrade support to `deck add` and `hand add`
* Add `PreMonsterTurnSubscriber` interface and related code

#### v1.2.3 ####
* Add `PostCampfireSubscriber` interface and related code
* Add proper support for `IntangiblePower` to be applied to players

#### v1.3.0 ####
* Update to support `[EARLY_ACCESS_013]`

#### v1.3.1 ####
* Fix bug that prevented the `deck r` command from working

#### v1.3.1a ####
* Pull in changes from `[EARLY_ACCESS_013]` hotfix 2

#### v1.3.2 ####
* A lot of behind the scenes work on settings pages.
* MODDERS: You shouldn't try to build complex settings pages right now, its probably going to get significantly cleaned up and reworked to be more simple soon.

#### v1.3.3 ####
* Fix non-id parameters on commands being cases sensitive
* Change backspace behavior on console
* Fix scaling for new settings panel elements introduced in v1.3.2

#### v1.3.4 ####
* Add `StartActSubscriber` interface and related code
* Add `PostDungeonInitializeSubscriber` interface and related code
* Add `StartGameSubscriber` interface and related code
* Fix bug that would prevent `publishPreStartGame()` from occuring in some cases
* Cleanup part 1
* Add IDEA project files
* Remove `_build.bat` since this can be automated in IDEA

#### v1.3.5 ####
* Add support for CustomRelic outline images

#### v1.3.6 ####
* Prevent Girya from stacking above 3 if `receivePostCampfire()` returns false

#### v1.4.0 ####
* Update to support `[EARLY_ACCESS_014]`
* Mostly switch to using SpirePatch for inserting hooks
* Speed up `kill all` command

#### v1.4.1 ####
* Add `kill self` command

#### v1.4.2 ####
* Add support for adding custom localization strings of any type
* Code cleanup

#### v1.4.3 ####
* Add `potion` command
* Add `fight` command
* Speed up `kill self` command

#### v1.5.0 ####
* Update to support `[EARLY_ACCESS_015]`

#### v1.5.1 (daviscook477) ####
* Code cleanup + bugfixes
* Add hooks for modifying the player's initial deck and initial relics
* Add hooks for changing the relics and potions sold by the shop
* Add methods for abstracting the manipulation of RelicLibrary
* Initial support for custom cards

#### v1.6.3  ####
This is a fast forward to v1.6.3 of daviscook477's fork with a few additional changes
* Update to support weekly patch 12 (daviscook477)
* Add hooks for adding custom player characters and the corresponding hooks for adding a new colors for custom cards (daviscook477)
* More code cleanup (daviscook477)
* Even more code cleanup

#### v1.6.4 ####
* Move changelog out of README
* Fix bug that caused `PublishPostDraw` to fire multiple times
* Fix bug that caused exhausted pile hotkey to work while console was visible
* Switch `AbstractCampfireOption` to use an instrument patch rather than overwriting base file
* Switch `CancelButton` to use an instrument patch rather than overwriting base file
* Switch `DiscardPilePanel` to use an instrument patch rather than overwriting base file
* Switch `DrawPilePanel` to use an instrument patch rather than overwriting base file
* Switch `EndTurnButton` to use an instrument patch rather than overwriting base file
* Switch `MapGenerator` to use an instrument patch rather than overwriting base file
* Switch `MonsterGroup` to use an instrument patch rather than overwriting base file
* Switch `TopPanel` to use an instrument patch rather than overwriting base file

#### v1.6.5 ####
* Switch `IntangiblePower` to use an instrument patch rather than overwriting base file
* Rewrite `IntangiblePower` changes to make it not mess up intents
* Remove diff, _diff.bat, _patch.bat since they are no longer needed

#### v1.6.6 ####
* Switch versioning system over to the one used in https://github.com/daviscook477/BaseMod/releases since he was using a different one
* Hook for exhausting a card
* Hook for finishing a battle
* Add support for Card Unlocks for custom characters
* Hook for using a card

#### v1.7.0 ####
* Add support for Saves for custom characters
* Fix some bugs with card unlock support

#### v1.7.1 ####
* Update to support weekly patch (week 13)

#### v1.7.2 ####
* Support viewing custom cards in the card library (kiooeht)

#### v1.7.3 ####
* Fix slowdown when viewing upgrades in the card library

#### v1.7.4 ####
* Add support for inspect view or custom cards. This requires including a larger texture next to every card texture where if the original card was `my_card.png` you now need `my_card_p.png` too in order to support inspect view
* NOTE: this introduced a breaking change to the API in `addColor` that will require you to update your mods

#### v1.7.5 ####
* Add support to the dev console for checking events with `event [ID]`

#### v1.8.0 ####
* Custom potion support (Haashii)
* All new wiki

#### v1.9.0 ####
* Custom animations for characters (monster support to come soon!)
* Add support for custom energy orbs (Blank The Evil)
* Add support to the dev console for applying powers with `power [ID] [amount]`

#### v1.9.1 ####
* Custom animations no longer run after their associated model is destroyed
* Hook for modifying base damage on cards like Perfected Strike or Heavy Blade

#### v1.9.2 ####
* Fix campfire UI for weekly patch (week 14)

#### v1.9.3 ####
* Reorganize the image locations for the test mod

#### v1.9.4 ####
* Add support for Custom Keywords to BaseMod with the editKeywords callback that ensures adding keywords is timed properly

#### v1.9.5 ####
* Add a hook for when powers are modified

#### v1.9.6 ####
* Can now set up character specific relics for characters other than the ironclad and silent

#### v2.0.0 ####
* Dev console has been completely revamped
* Holding down keys triggers multiple keypresses
* The console remembers previous commands so use the up and down arrows to select between previously typed commands
* The console is multiline now in order to display the last few commands and their outputs
* Help text is displayed for commands if they are mistyped
* More commands! `hp` and `maxhp` allow you to change your current HP and current max HP

#### v2.0.1 ####
* Hotfix reverting multiple keypresses on holding down keys because it broke a bunch of things

#### v2.1.0 ####
* New UI features for Modders

#### v2.1.1 ####
* Hotfix to prevent wasting GPU cycles when there are no custom characters to render

#### v2.1.2 ####
* Update to week 15 patch

#### v2.1.3 ####
* Hotfix to fix the console on the week 15 patch

#### v2.2.0 ####
* Add text box support for mod settings with `BaseMod.openTextPanel`
* Merge pull request by BlankTheEvil to fix `CustomCardWithRender` appearing incorrectly in the CardLibrary screen

#### v2.3.0 ####
* Support ModTheSpire v2.5.0 and now save the console hotkey
* Indicate that BaseMod is built specifically for the week 15 patch
* The ModPanel UI backend has been made better and more extensible. A few methods have been deprecated but there are no breakign changes.
* Added ToggleButton - idea and some code courtesy of twanvl (https://github.com/twanvl)
* Added a LabeledToggleButton which is a ToggleButton with a text label

#### v2.3.1 ####
* Cleanup README
* Fix character select screen memory leak (kiooeht)
* Revamp subscription system
* Deprecate old subscription system
* Fix issue #24
* Fix issue #29
* Support animations done in spriter - these have a much smaller performance impact (kiooeht)
* Prep for MTS v2.6.0 when it releases
* Add a new badge icon for BaseMod
* Support week 17 patch

#### v2.4.0 ####
* **Requires** ModTheSpire v2.6.0 now
* Support **week 18** patch
* Convert CardLibraryScreen patches to Locator patches
* Convert CardCrawlGame patches to Locator patches
* Modal choice cards! (kiooeht) (https://github.com/daviscook477/BaseMod/wiki/Modal-Choice-Cards)
* Fix issue #40
* Fix issue #41
* Remove issue #38
* Add feature #31
* Add feature #39

#### v2.4.1 ####
* Fix multiple bugs with start act, pre start game, and start game subscribers
* Add support for per-card energy orb graphics (BlankTheEvil)

#### v2.5.0 ####
* Support **week 20** patch (kiooeht)
* Add support for custom dynamic variables (kiooeht)
* Merge CustomCardWithRender features into CustomCard - breaking change - should only affect BlackMageMod
* Add feature CardBasic (1st functional version) (DemoXinMC)
* Add support for per-card banners (BlankTheEvil)

#### v2.6.0 ####
* Support **week 21** patch

#### v2.7.0 ####
* Support **week 22** patch (kiooeht)
* Allow mods to use the same card and relic IDs without conflict (kiooeht)
* Modal option improvements (kiooeht)

#### v2.8.0 ####
* Support **week 23** patch (kiooeht)
* Fix bugs in last week's ID conflict resolver (robojumper)
* Support energy symbols using [E] instead of color specific symbols (kiooeht)

#### v2.9.0 ####
* Support week 24 patch (kiooeht)
* Disable mod ID on cards and relics as it has problems

#### v2.9.1 ####
* Fix duplicate rendering of custom characters (again)

#### v2.10.0 ####
* Support week 25 patch (kiooeht)
* Actually set hybridColor and spotsColor for custom potions (twanvl)

#### v2.11.0 ####
* Support week 27 (kiooeht)
* Fix power command (kiooeht)
* Fix abandon run to abandon modded character runs (kiooeht)
* Bug fix: Use skill background for skill cards in portrait view (twanvl)
* Fix modal choice cards to not counts towards cards played count (kiooeht)
* Death hook (alexdriedger)


#### v2.12.0 ####
* Support week 28 (kiooeht)
* Add OrbStrings to localization support (kiooeht)

#### v2.13.0 ####
* Support for week 29 (kiooeht)
* Add AutoComplete to DevConsole (Skrelpoid)

#### v2.14.0 ####
* Support for week 30 (kiooeht)
* Deprecate starting deck and relic hooks (kiooeht)

#### v2.15.0 ####
* Support for week 31 (kobting)
* Add modded characters to the Custom mode screen (kiooeht)

#### v2.16.0 ####
* Add debug command (Blank The Evil)
* Fix Run History screen (kiooeht)
* StartBattleHook (kobting)
* Diverse includes modded character's cards (Moocowsgomoo)
* Patch events and relics that care about Strikes and Defends (twanvl)
* Fix portrait image in right-click card previews (twanvl)
* Custom event support (Blank The Evil)
* Character select screen pages with many characters (kobting)
* Allow custom energy symbols on cards (twanvl)
* Fix [E] rendering in portait mode (twanvl)
* Dev console: Discard command (twanvl)
* Dev console: Set card numbers command (twanvl)

#### v2.17.0 ####
* Support for week 34 (kiooeht)
* Fix RelicViewScreen not scrolling far enough with mods that add relics (kiooeht)
* Potions can be specific to a character (twanvl)

#### v2.18.0 ####
* Identify what mod relics, cards, and powers are from (kiooeht)
* Allowing adding relics to blue pool (kiooeht)
* Make relic and power onEvokeOrb actually usable (kiooeht)
* Fix ascension for modded characters (kiooeht)
* Fix crash if Note For Yourself event (kiooeht)
* Fix card rendering to not draw curse texture (Blank The Evil)

#### v2.19.0 ####
* Support for week 37 (kiooeht)
* Fix Ascension 14 for modded characters (kiooeht)
* Fix Shiny modifier for modded characters (kiooeht)
* Fix Insanity modifier for modded characters (kiooeht)
* Anti-alias modded art assets automatically (kiooeht)

#### v3.0.0 ####
* Custom monsters (kiooeht)
* Custom monster fights (kiooeht)
* Custom bosses (kiooeht)
* Card tags (kiooeht)
* Fix and improve custom events API (kiooeht)
* Custom bottle-style relics (kiooeht)
* Make StartBattleHook work when loading a save (gogo81745)
* Automatically give player an orb slot when channeling on a character without orb slots (kiooeht)
* Render colored outline for custom character relics (kiooeht)

#### v3.0.1 ####
* Fix OnStartBattle hook (kiooeht)

#### v3.0.2 ####
* Fix Ascension mode for modded characters after v3.0 (kiooeht)
* Fix Gremlin Match tag for modded characters (kiooeht)

#### v3.1.0 ####
* Let basic Strike/Defend events work with not CustomCards (kiooeht)
* Make "deck add" command mark cards as seen (kiooeht)
* Fix small orb icons followed by a period in tooltips (kiooeht)
* Refactor CustomBottleRelic to be an interface (kiooeht)
* Render custom card tooltips in SingleCardViewPopup (kiooeht)
* Fix Red/Green/Blue/Colorless Cards modifiers to work with modded characters (kiooeht)
* Add custom mode modifiers hook (alexdriedger)
* Automatically add custom mode modifiers for modded character cards (kiooeht)
* Max HP change hook (kiooeht)

#### v3.1.1 ####
* Hotfix crash when starting modded character runs (kiooeht)

#### v3.2.0 ####
* Support for week 41 (kiooeht)
* Add custom monsters to console suggestions (kiooeht)
* Make card compendium only use added colors (kiooeht)
  * Fixes FruityMod breaking compendium with incomplete character
* Use character name for tabs in card compendium (kiooeht)
* Use character name on run history screen (kiooeht)
* Support nice monster names on run history screen (kiooeht)

#### v3.2.1 ####
* Fix ascension level select for modded characters (kiooeht)
* Update README

#### v3.2.2 ####
* Fix crash when unlocking ascension as base game character (kiooeht)

#### v3.3.0 ####
* Fix crash if any mod patches AbstactPower (kiooeht)
* Fix crash when adding custom monster that uses setHp (kiooeht)
* Fix fight command after a monster room (twanvl)
* Mod items in top panel (kobting)

#### v3.4.0 ####
* Support for week 43 (kiooeht)
* Fix TopPanel positions on not 16:9 aspect ratios (kiooeht)
* Fix modified card costs not rendering when not in hand (Moocowsgomoo)
* Fix ModLabeledToggleButton privacy (Blank The Evil)

#### v3.5.0 ####
* Support for week 44 (kiooeht)
* Fix Start Game hook triggering when looping in Endless mode (kiooeht)
* Fix Ascension 20 double boss when custom bosses have been added (kiooeht)
* Allow custom characters to add relic unlocks (kiooeht)

#### v4.0.0 ####
* Support for week 45 (kiooeht)
* Fix PostPotionUse hook (kiooeht)
* Fix drawn cards entering modal choice options (kiooeht)
* Sort modded characters alphabetically by name (kiooeht)
* Shrink card description font for cards with lots of text (kiooeht)
* Allow max hand size to be changed (kiooeht)
* Fix ModSlider not allowing x position to be set (kiooeht)
* Fix custom character card and seen count (kiooeht)

#### v4.0.1 ####
* Fix Modal Choice Cards when double-played (kiooeht)
* Don't shorten name if adding "..." makes it longer than it was (kiooeht)
* Fix custom character card modifiers (kiooeht)
* Allow passing EnergyOrbInterface to CustomPlayer (kiooeht)
* Slow energy orb spin when out of energy (kiooeht)

#### v4.0.2 ####
* Fix Discovery not obeying new max hand size (kiooeht)
* Fix ModSlider using y position as x position (kiooeht)

#### v5.0.0 ####
* Support for week 46 (kiooeht)
* Fix custom monsters being more weighted (kiooeht)
* Fix custom monsters not being used for the first strong enemy (kiooeht)
* More fixing ModSlider position (kiooeht)

#### v5.0.1 ####
* Fix NPE in base game GetAllInBattleInstances (kiooeht)

#### v5.1.0 ####
* Support for week 47 (kiooeht)
* Add PreRoomRender hook (Blank The Evil)
* Custom save fields for cards and relics (twanvl)

#### v5.2.0 ####
* Make Spriter animation public (kiooeht)
* Spriter animations for monsters (kiooeht)
* Multi-word keywords (kiooeht)
* What mod is this from? for events (kiooeht)

#### v5.2.1 ####
* Fix crash from multi-word keywords on some relics (kiooeht)

#### v5.2.2 ####
* Easier SpineAnimation option for custom characters (kiooeht)
* Fix Diverse adding base character cards twice (kiooeht)

#### v5.3.0 ####
* DevConsole AutoComplete: The active suggestion is now highlighted. Suggestions are now filled up to ":" (Skrelpoid)
* Customizable Colors for CustomDynamicVariable (Skrelpoid)
* Fix softlock with Modal cards (kiooeht)
* Many bug fixes for the console (Skrelpoid)
* Blight command (kiooeht)

#### v5.3.1 ####
* Fix for week 50 drawing colorless icon over custom character cards in single card view (kiooeht)

#### v5.3.2 ####
* Fix custom character name color in stats screen (kiooeht)
* Allow Spriter animations to flip (support Spear & Shield fight) (kiooeht)
* Fix dialog position being reset on custom characters when entering a room (kiooeht)
* Fix PrePotionUse hook being called without targeting a monster (kiooeht)
* Fix crash if custom character has no character specific relics (kiooeht)
* Fix hand rendering craziness when the hand size gets out of control (kiooeht)
* Fix custom card tooltips going off the bottom of the screen (kiooeht)
* Fix max HP change hook triggering on ANY creature changing max HP (kiooeht)

#### v5.3.3 ####
* Fix Whatmod appearing on heart screen and in event combats (kiooeht)
* Maybe fix unlock crash on victory with custom characters (kiooeht)

#### v5.3.4 ####
* Fix startup crash if base character is fully (level 6) unlocked (kiooeht)

#### v5.3.5 ####
* Fix PostEnergyRecharge hook (kiooeht)
* Fix crash when toggling beta art on modded cards (kiooeht)
* Fix crash if modded card is missing _p texture (kiooeht)

#### v5.4.0 ####
* Fix unlock crash on victory with custom characters (kiooeht)
* Fix PostDeath hook not triggering on killing Heart (kiooeht)
* Tag the basegame basic cards with BASIC_STRIKE, BASIC_DEFEND, and FORM (kiooeht)
* Custom victory cutscenes for custom characters (kiooeht)
* Custom victory effects for custom characters (kiooeht)

#### v5.5.0 ####
* Add OnPlayerLoseBlockSubscriber hook (NellyDevo)
* Added function to remove relics from custom characters' pools (Moocowsgomoo)

#### v5.6.0 ####
* Custom Rewards (Blank The Evil)

#### v5.7.0 ####
* Allow Dynamic Variables to show in smith view upgraded (kiooeht)
* Make card description patches work for Chinese:
  * Shrink long descriptions (kiooeht)
  * [E] in small card descriptions (kiooeht)
  * [E] in SingleCardViewPopup (kiooeht)
  * Custom Dynamic variables (kiooeht)

#### v5.7.1 ####
* Fix Dynamic Variables showing in smith view breaking things (kiooeht)

#### v5.7.2 ####
* Fix too many characters going offscreen on the custom mode screen (kiooeht)

#### v5.8.0 ####
* Steam Rich Presence (kiooeht)
* Fix whatmod purple color code not working in Chinese (kiooeht)
* Fix whatmod not working on linked relics (kiooeht)
* Add option for Custom Mode button (NellyDevo)
* Fix custom character unlocks (MichaelMayhem)
* Fix power console command for some powers (admiralbolt)
* Fix power console command not stacking powers correctly (admiralbolt)
* Fix Twitch integration position in Top Panel (kiooeht)

#### v5.9.0 ####
* Make whatmod API public (kiooeht)
  * `WhatMod.findModName`
  * `WhatMod.findModID`
* Add outline color to ModColorDisplay (kiooeht)
* Fix Spriter animations being tied to framerate (NellyDevo)

#### v5.10.0 ####
* Unique keywords (NellyDevo)
* Fix hex code card text coloration in CN languages (NellyDevo)

#### v5.11.0 ####
* Fix incorrect line breaks when using unique keywords (Celicath)
* Fix unique keywords in relic descriptions (NellyDevo)
* Fix energy tooltip appearing in SingleCardViewPopup (kiooeht)
* Add simple API for getting keyword title/description (kiooeht)
* Fix typo in MaxHPChangeSubscriber (kiooeht)

#### v5.12.0 ####
* Make base game powers cloneable (Reina)
* Fix RelicGetSubscriber not being called (kiooeht)
* Fix RelicGetSubscriber being called a bunch during game startup (kiooeht)
* Fix blight add command crashing (fiiiiilth)
* Fix colored card text line length in CN mode (NellyDevo)
* Fix unique keyword linebreaks in CN mode (NellyDevo)
* Add OnPlayerDamagedSubscriber (Rin Camelia)
* Allow Smart Texts to remove spaces between 2 words using a special keyword [REMOVE_SPACE] (JohnnyBazooka89)
* Add AddAudioSubscriber (Alchyr)

#### v5.12.1 ####
* Fix not auto-registering bottle relics when added to custom pool (kiooeht)
* Fix to Shrink Long Description feature, when description font size was not correct after card was upgraded (JohnnyBazooka89)
* Fix locked cards saying "Unknown" instead of "Locked" (Seeonee)

#### v5.13.0 ####
* Allow CustomCards to use base game card atlas (kiooeht)
* Allow CustomCards to customize how their portrait images are loaded (kiooeht)
* Allow CustomCards to change name font size (kiooeht)
* Fix tooltips covering up cards in SingleCardViewPopup (kiooeht)
* Fix custom card libraries being incorrectly scaled (Seeonee)
* Fix unlock screen visual bug with CustomRelics (Seeonee)
* Fix event command while in combat (fiiiiilth)
* Fix keyword tooltips to wrap if too long (bugsniper)
* Fix DamageAllEnemiesAction to stop crash if monsters change (Alchyr)

#### v5.14.0 ####
* Support for Slay the Spire v1.1 (kiooeht)
* Fix energy tooltip rendering red orb under custom orb (kiooeht)
* Fix ClickableUIElement x render position not being calculated correctly (kobting)
* Custom GridCardSelectScreen that takes CardGroup and callback (alexdriedger)
* Fix to [REMOVE_SPACE] feature: commas, dots and similar should no longer go to the next line (JohnnyBazooka89)"
* Updated dev console code (Raz)

#### v5.14.1 ####
* Fix not rendering cost of playable status/curse cards regression (kiooeht)
* Fix unique keywords misaligning card descriptions (kiooeht)

#### v5.14.2 ####
* Fix calculateModifiedCardDamage (kiooeht)

#### v5.15.0 ####
* Allow custom card tooltips before normal tooltips (kiooeht)
* Fix ConsoleCommand.addCommand (kiooeht)

#### v5.15.1 ####
* Support for Slay the Spire 07-17-2019 update (kiooeht)
* Fix custom energy icons in card descriptions not working in SingleCardViewPopup (kiooeht)

#### v5.16.0-beta ####
* Support for Slay the Spire 09-09-2019 BETA (kiooeht)

#### v5.16.0-beta.2 ####
* Fix colored text being offset badly (kiooeht)
* Fix multi-word keywords not removing underscores (kiooeht)
* Fix adding and removing Watcher cards (kiooeht)
* Make Vampires event use standard remove function (kiooeht)
* Move card preview in SingleCardView to not cover whatmod (kiooeht)

#### v5.16.0-beta.3 ####
* Fix RitualPower cloneable patch crash (kiooeht)

#### v5.16.0-beta.4 ####
* Don't show card colors in compendium that contain no cards to avoid crash (kiooeht)
* Fix `potion list` command changing potion rng (kiooeht)
* Fix `potion list` command not including potions from other characters (kiooeht)
* Fix `potion` command not giving potions from other characters (kiooeht)
* Fix `potion` command not autocompleting potions from other characters (kiooeht)
* Add whatmod support for potions (kiooeht)
* Fix custom boss map icons being black square if used a second time (kiooeht)
* Fix boss map icons leaking memory (kiooeht)
* Fix dynamic variables always rendering fully opaque (kiooeht)
* Fix multiword keywords that use underscores (JohnnyBazooka89)

#### v5.17.0 ####
* Deprecate BASIC_STRIKE and BASIC_DEFEND tags, use STARTER_STRIKE and STARTER_DEFEND instead (kiooeht)
* Tag Deva Form with FORM (kiooeht)
* Fix My True Form double adding base game form cards (kiooeht)
* Fix Smoke Bomb causing player dialog to render in the wrong position (kiooeht)
* Fix custom character name not visible in compendium for some languages (Celicath)
* Fix button positions on character select when using 4:3 resolution (kiooeht)
* Display all characters on character select if only one modded character is loaded (kiooeht)
* Hide long offscreen starter relic descriptions on character select (kiooeht)
* Fix `relic add` command not showing Watcher only relics (JohnnyBazooka89)
* Fix `relic list rare` command incorrectly showing boss relics (JohnnyBazooka89)
* Fix BaseMod altering encounter rng compared to basegame (kiooeht)
* Fix colored text issues (Celicath)

#### v5.18.0 ####
* Move rewards scrolling code from Hubris to BaseMod (kiooeht/erasels)

#### v5.18.1 ####
* Make certain Watcher cards work with max hand size changes (kiooeht)
  * Scrawl
  * Foreign Influence
  * Meditate
  * Flicker (deprecated)
* Fix many actions to work with max hand size changes (kiooeht)
* Fix bias in boss selection RNG (dbjorge)

#### v5.19.0 ####
* AutoAdd API for auto-adding cards (kiooeht)
* Fix character filter in run history (Celicath)
* Fix history command (Celicath)
* Hide unnecessary unlock bar (Alchyr)
* Fix SafeDamageAllEnemies crash (Alchyr)
* Fix duplicate options when using event command (Alchyr)

#### v5.20.0 ####
* Fix scrolling tooltips in single-card view omitting some tips when many exist (dbjorge)
* Custom card frames (Alchyr)
* Show correct unlock stats on character select screen (Alchyr)
* Save dev console history across game restarts (kiooeht)

#### v5.20.1 ####
* Fix crash if mods add unlocks badly (kiooeht)

#### v5.21.0 ####
* Fix hi-res relic art not being used on mod relics that have it (kiooeht)
* Custom save fields for potions (kiooeht)
* CardModifier (NellyDevo)

#### v5.21.1 ####
* Fix CustomRelic using small relic art as large relic art (kiooeht)
* Remove broken onDiscarded CardModifier hook (NellyDevo)
* Fix crash on very long card text caused by CardMod (NellyDevo)

#### v5.22.0 ####
* Add ModLabeledButton (LordAddy)
* Make CardModifiers save and load on master deck (NellyDevo)
* Refactor CardModifier alternate cost system (NellyDevo)
* Much more options for how events spawn (Alchyr)

#### v5.23.0 ####
* Color tiny cards in Run History for modded cards (kiooeht)
* Fix Expunger description reverting to X (kiooeht)
* Add not-package filter for AutoAdd (BlankTheEvil)
* Fix "Hide unnecessary unlock bar" to work on Heart kill (Celicath)
* Change event utils default behavior to override if no type specified (Alchyr)
* OnCreateDescriptionSubscriber (NellyDevo)
* XCostModifier interface for changing X cost effect (NellyDevo)
* CardModifier (NellyDevo)
  * Make X costs work with alternate costs
  * Add annotation for non-savable CardModifiers
  * Fix save/load duplicating cardmods
  * Fix non-splittable post-energy resources
  * Fix hasModifier crash on modifiers without identifiers
* Remove JavaFX dependency (kiooeht)

#### v5.23.1 ####
* Fix crash when opening the card compendium (kiooeht)

#### v5.23.2 ####
* Re-add getColor(float) to stop crash because GOG version of StS is out of date (kiooeht)

#### v5.23.3 ####
* Allow modded character card modifiers to be localized (kiooeht)
* ZHS translation of modded character card modifiers (Rita-B)
* Fix Discovery basegame bugs (kiooeht)
  * Fix Discovery spamming the log and possibly lagging the game when played
  * Fix returnTrulyRandomCardInCombat marking (almost) every card as seen
* Make CardModifierManager.modifiers() public (kiooeht)
* Fix Potion Lab scroll bounds (kiooeht)

#### v5.24.0 ####
* Sensory Stone event text for custom characters (kiooeht)
* Fix upgrade count 0 in hand and deck commands not working (Celicath)
* Fix ZHS, ZHT, and JPN not displaying color in tooltips (Celicath)

#### v5.25.0 ####
* Card descriptors (kiooeht)

#### v5.26.0 ####
* Fix Vigor gain from Attacks (kiooeht)
* ReflectionHacks improvements (kiooeht)
  * Change get methods to generic return types
  * Cache Fields for faster access
  * Javadoc comments
  * Update setPrivateInherited to search all superclasses
  * Add getPrivateInherited
  * Add privateMethod
  * Add privateStaticMethod
* Fix modded character selection background animation (kiooeht)
* Fix alternate card cost logic to better fit all situtations (NellyDevo)

#### v5.27.0 ####
* Support for Slay the Spire 11-27-2020 BETA (kiooeht)

#### v5.28.0 ####
* Add get/set X/Y methods to mod config ui elements (erasels)
* Add ModMinMaxSlider (kiooeht/erasels)

#### v5.28.1 ####
* Fix DiscoveryAction for more than 2 copies (kiooeht)
* Refactor CloneablePower code (Raz)
* Add CloneablePower to base game powers introduced with v2.0 (Raz)
* Fix crash with act command (Raz)

#### v5.29.0 ####
* Improve typing for dev console (Alchyr)
* Fix crash to do with CardModifier with SaveIgnore in the master deck (NellyDevo)
* New CardModifier hook: shouldApply (NellyDevo)
* CardPowerTips (NellyDevo)
  * Display a card in a tooltip
* CardBorderGlowManager (NellyDevo)
  * Allows card to have multiple glow colors at once
* No longer cull potion rewards (erasels)
  * BaseMod allows more than 5 combat rewards
* Fix custom reward rendering on ultra-wide resolutions (NotInTheFace)
* Fix character select screen on ultra-wide resolutions (NotInTheFace)
* VfxBuilder (NotInTheFace)

#### v5.30.0 ####
* More reliable input canceling (Alchyr)
* VfxBuilder features (NotInTheFace)
  * New hook to call after rendering
  * New hooks for animation phases starting/ending
  * More interpolations

#### v5.30.1 ####
* Fix Console being openable while it's not enabled
* Fix CardModifier atEndOfTurn triggering at the end of monster turns (NellyDevo)
* Fix VFXBuilder to work with packed atlases

#### v5.31.0 ####
* New CardModifier hook: calculateCardDamage (NellyDevo)
* Change CardModifier copy applying to new copied card after upgrades (NellyDevo)
* * Fix EventUtil override bonus condition crash (Alchyr)

#### v5.32.0 ####
* Fix all CardMod onUse related hooks to not trigger when unplayable and autoplayed (NellyDevo)
* Add standard common CardMods (Vex)
* Make character select screen page arrows bigger (NotInTheFace)

#### v5.32.1 ####
* Fix VfxBuilder.fadeOut not working for fade times greater than 1 second (kiooeht)
* Fix whatmod tooltip placement on ultrawide resolutions (kiooeht)
* Fix whatmod appearing in SCV and SRV when whatmod is disabled (kiooeht)
* Fix card preview position in SCV on ultrawide resolutions (kiooeht)

#### v5.32.2 ####
* Fix Transient crashing after turn 7 if Fading is removed (kiooeht)
* Fix mod config panel placement on ultrawide resolutions (kiooeht)

#### v5.32.3 ####
* Fix possible crash with initializePowerMap and WhatMod (kiooeht)
* Fix SingleCardView variable width with additional character (Alchyr)

#### v5.33.0 ####
* Fix AbstractPotion.addToTop being an infinite loop (kiooeht)
* Reset seen events every 3 acts so they return to the pool in endless (Alchyr)
* Add NoLibraryType annotation for CardColor SpireEnums (kiooeht)
* Fix incorrect unlock count in character select screen (codewarrior0)
* Fix Purple Cards run modifier not working for custom characters (codewarrior0)

#### v5.33.1 ####
* Fix scrolling tooltips on ultrawide resolutions (kiooeht)

#### v5.34.0 ####
* Fix custom mode mods sometime persisting between runs (NellyDevo)
* Console command for manipulating variables (Alchyr)

#### v5.34.1 ####
* Fix crash with custom mods being null

#### v5.34.2 ####
* Fix card border glow crash in library (NellyDevo)

#### v5.34.3 ####
* Fix monsters not passing to multitarget card modifiers (NellyDevo)

#### v5.35.0 ####
* Add fadeOutFromAlpha and fadeOutFromOriginalAlpha to VFXBuilder (EricB)
* Dynamic text blocks on cards for pluralization cases (Mistress Alison)

#### v5.35.1 ####
* Fix weird possible crash with dynamic text blocks (kiooeht)

#### v5.36.0 ####
* Add OnPlayerTurnStartSubscriber (kiooeht/Alchyr/herbix)
* Add OnPlayerTurnStartPostDrawSubscriber (kiooeht/Alchyr/herbix)
* Fix crash when no shrine events are available (modargo)
* Fix some issues with basic cardmods (erasels)
  * Capitalize cardmod keywords
  * Move Exhaust cardmod to end of description

#### v5.37.0 ####
* Fix crash in card power tips (NellyDevo)
* Add controller support for CustomCharacterSelectScreen pages (kobting)

#### v5.37.1 ####
* Fix main menu settings crash (herbix)
* Fix cardmods not respecting action queue with automatic removal (NellyDevo)

#### v5.38.0 ####
* Add !Turn! to dynamic text (Mistress Alison)
* Add modifyName to CardMods (Mistress Alison)
* Fix card tintColor (herbix)

#### v5.39.0 ####
* Make dynamic text work in upgrades and upgrade previews (Mistress Alison)
* Add CardModifier support to WhatMod (kiooeht)
* Fix crash in ZHT with modded keywords (modargo)

#### v5.40.0 ####
* Add option to disable BaseMod gameplay fixes, allowing a more vanilla experience (kiooeht)
  * Automatically give orb slot
  * Allow more than 5 combat rewards
* Add tooltip option to ModLabeledToggleButton (kiooeht)
* Remove Note For Yourself patch that is no longer necessary (kiooeht)

#### v5.40.1 ####
* Fix Conspire being unable to load (kiooeht)

#### v5.41.0 ####
* Phased events (Alchyr)
* Fix incorrect height scaling of TinyCard in run history screen (Mistress Alison)
* Fix log spam when viewing curse cards with cost (Alchyr)
* Fix CN text when magic number is at start of line (akdream)
* Add multi-card previews to AbtractCard (NellyDevo)
* Fix order of rewards and custom rewards when saving and reloading (herbix)
* Add NoCompendium annotation to remove cards from compendium (NellyDevo)
* Add NoPools annotation to remove cards from reward pools (NellyDevo)
* Add modify base block and base damage methods to CardMods (NellyDevo)
* Fix energy icon rendering in card descriptions (Alchyr)
  * Fix position when card is scaled
  * Fix rotation and position when card is rotated
  * Fix scale when card description gets shrunk for being long

#### v5.42.0 ####
* Fix CN card descriptions not shrinking (Alchyr)
* CardModifiers (NellyDevo)
  * Apply base stuff on initial application
  * Render in SCV
* Dynamic text block fixes (Mistress Alison)
  * Reinitialize description if any dynamic variables updated
  * Remove issues needing manual initializeDescription
  * Remove need for -1 case for uninitialized values in compendium
* Fix CN keywords added by basic CardModifiers (herbix)
* Add option for custom events that end with a reward screen (herbix)
* Add screen-space rendering postprocess (herbix/kiooeht)

#### v5.42.1 ####
* Fix screen shake (herbix)
* Add safety checks to post-processing initialization (kiooeht)

#### v5.42.2 ####
* Fix post-processing blend function to not apply alpha again (kiooeht)
* Fix post-processing rendering causing a crash on Mac (kiooeht)

#### v5.43.0 ####
* Fixrendering on super ultrawide (kiooeht)
* Add custom multi-page FTUE (herbix)

#### v5.44.0 ####
* kill all command now actually always definitely kills (kiooeht)
* Fix custom dynamic variables not being colored in the Armaments preview (kiooeht)
* debug console command can now be used without arguments to toggle debug mode (kiooeht)
* info console command can now be used with arguments to set true/false (kiooeht)
* Add evalcode console command for running code via the console (kiooeht)
* Fix CustomReward generating an extra card reward, altering rare change and affecting save/load (modargo)
* Dynamic text blocks (Mistress Alison)
  * Add support for custom dynamic text block checks
  * Add extra Location cases for compendium and cards not owned by the player
* Add modifyTitle support to TinyCards (Mistress Alison)

#### v5.44.1 ####
* Fix possible crash in other mods when using CustomReward (kiooeht)

#### v5.45.0 ####
* Fix kill all command softlocking Darklings and not stopping the Heart music (kiooeht)
* Fix right-click release also triggering left-click release (kiooeht)
  * This fixes bugs like SRV instantly closing when right-clicking the shop relic in shops
* Let CustomReward accept TextureRegions for the icon (kiooeht)
* Fix ReflectionHacks.getCachedField memory churn (kiooeht)
* Allow Ctrl-V to paste into the console (kiooeht)
* Fix evalcode command breaking if code doesn't have a return value (kiooeht)
* Add beta art support for CustomCard (Alchyr)
* Make anti-log spam patch slightly more interactable (Alchyr)
* Add DraggableUI interface (Pandemonium)
* Add controller navigation support to card library (herbix)
* Update SafeDamageAllEnemies (Alchyr)
* Fix memory churn caused by the constant in-combat particle effects (kiooeht)
* Allow CustomCard to have different card art in SCV on upgrade (kiooeht/Vex)

#### v5.46.0 ####
* Dear ImGui (kiooeht)
* CustomScreen (kiooeht)
* Fix black screen when using post-processing effects and cursor is hidden (kiooeht)
* Don't use energy when infinite energy is enabled (kiooeht)
* Fix beta art defaulting to on (Alchyr)
* Remove unnecessary log in memory churn patch (kiooeht)

#### v5.46.1 ####
* Fix crash when running multiple instances of the game (kiooeht)

#### v5.47.0 ####
* Allow evalcode command to access private fields and methods (kiooeht)
* Add genericScreenOverlayReset helper to CustomScreen (kiooeht)
* Fix Dear ImGui trying to load even when not enabled (kiooeht)
* Refactor memory churn patch (kiooeht)
* CardMods (Mistress Alison)
  * Fix modifyBaseDamage rendering in combat and master deck
  * Fix modifyBaseBlock rendering in combat and master deck
  * Add modifyBaseMagic
* Fix Chinese text rendering issues (Somdy)

#### v5.48.0 ####
* Minor fix to Chinese text rendering (Somdy)
* Fix card energy font in single card popup (herbix)
* Allow multiple player classes for an event (Alchyr)

#### v5.49.0 ####
* Fix unnecessary line deletion when rendering Chinese text (Somdy)
* Fix functuation and tipbox height when rendering Chinese text (Somdy)
* Add render hooks to CardMods when in SCV (NellyDevo)
* Fix texture leak in CustomOrb (Alchyr)
* Fix getPotion log spam (erasels)
* Allow dynamic variables to check card during upgrade coloring (Nyoxide)
* Fix GlowInfo priority not being accessible (kiooeht)

#### v5.50.0 ####
* Add additionalTooltips method to CardMods (Pandemonium)

#### v5.51.0 ####
* Fix CustomBottleRelic rendering on cards not scaling with resolution (kiooeht)
* Don't require modid prefixes to be all lowercase (Alchyr)
* Fix modifed base damage multi-upgrade cards showing inflated values in SCV (NellyDevo)
* Fix modifyBaseMagic (Mistress Alison)
* Add CardMods to Run History (Mistress Alison)
* Fix CardMods crashing on load when they no longer exist (NellyDevo)
* Add card descriptors to CardMods (NellyDevo)
* Output stacktrace instead of silently accepting potion initialization errors (Alchyr)
* Make ethereal exhaust order seeded (Alchyr)

#### v5.51.1 ####
* Fix card descriptor frame not accounting for CardMods in SCV (kiooeht)
* Fix card descriptor dynamic frame for all cards, not just CustomCard (kiooeht)

#### v5.52.0 ####
* Fix basegame bug: SCV not displaying locked card art correctly (kiooeht)
* Fix possible crash if SCV card name font ends up null (kiooeht)
* Fix crash if other mods enable pedantic shaders (kiooeht)
* Add convenience methods for phased event card selections (Alchyr)
* Add ability to color dropdown menus (Pandemonium)
* Fix incorrect width of custom dynamic variables in CN languages (Mwalls)
* Add horizontal only toggle to  MultiCardPreview (NellyDevo)
* Fix possible crash when removing CardMods (NellyDevo)
* Add onCardModified hook for CardMods (NellyDevo)
* Fix CardMod crash in run history (NellyDevo)
* Add border glows to CardMods (NellyDevo)
* Add onLoadedMisc hook to CustomCard (NellyDevo)
* Add ModRadioButtonGroup (Mindbomber2)
* Add support for large relic art to CustomRelic (Alchyr)
* Fix offset of capitalized keyword prefixes (Alchyr)
* Refactor CardMod base values (Alchyr)
* Allow Ctrl, Shift, and Alt to be used as modifiers for opening the console (kiooeht)
* Add hotkey config ui for opening ImGui (kiooeht/Piña Colada)
* Stop ImGui hotkey activating while console is open (kiooeht)

#### v5.52.1 ####
* Fix CardMods getting removed when they shouldn't be (Mistress Alison/NellyDevo)
* Fix base value modifier upgrade preview (Alchyr)

#### v5.52.2 ####
* Allow Ctrl, Shift, and Alt to be used on their own again for opening the console (kiooeht)

#### v5.52.3 ####
* Fix CardMod base value changes (Mistress Alison)
* Fix border glow manager not testing for CardMod glows (NellyDevo)

#### v5.53.0 ####
* Make multiline relic descriptions work on character select screen (JohnnyBazooka89)
* Don't fade limbo when clearing card queue (Alchyr)
* Add repeat to dynamic text blocks (herbix)
* Allow bonusCondition and spawnCondition to be repeated in AddEventParams (Alchyr)
* Fix Choke power description not updating (SandTag)
* Remove check for Focus when updating orb descriptions (SandTag)
* Add scrolling tooltips for relics on SingleRelicView (SandTag)
* Fix card descriptors and custom card frames not working together (Enbeon)
* Add PostShopInitializeSubscriber (DandyLion)
* Add pagination to shop relic grid (DandyLion)
* Card in use now counts as "in hand" for dynamic text (Mistress Alison)
* Fix CardMod render not working in SCV (Mistress Alison)
* Add overload for addCharacter (Alchyr)
* Nelly gets to keep her purple license
  * Fix inconsistant CardMod application (NellyDevo)
  * Add on battle start hook for CardMods (NellyDevo)
  * Add addToBot and addToTop to CardMods (NellyDevo)
  * Improve error handling with unserializable CardMods (NellyDevo)
* Fix for ModRadioButtonGroup (Mindbomber2)
* Fix magic number not resetting if a CardMod was applied once (herbix)
* Fix TwitchVoter crashing sometimes (Alchyr)
* Fix soft lock with endsWithRewardsUI events (Alchyr)
* Fix queueExtraCard crash (erasels/kiooeht)
* Fix provided CardMod localization (Rucodeby)

#### v5.53.1 ####
* Revert changes
  * Add PostShopInitializeSubscriber (DandyLion)
  * Add pagination to shop relic grid (DandyLion)

#### dev ####
