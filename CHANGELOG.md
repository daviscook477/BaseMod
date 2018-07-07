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

#### dev ####
* Add debug command (Blank the Evil)
* Fix Run History screen (kiooeht)
