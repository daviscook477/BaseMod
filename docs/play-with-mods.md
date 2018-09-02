---
id: play-with-mods
title: Playing With Mods
---

To use ModTheSpire to play a mod:

1. [Download the latest release of ModTheSpire](https://github.com/kiooeht/ModTheSpire/releases/latest).
1. Place `ModTheSpire.jar` inside your Slay the Spire installation directory. It should be alongside `desktop-1.0.jar`.
    1. *Note: On Mac, the installation directory is inside `SlayTheSpire.app`. Open it with Right-click -> Show Package Contents or Ctrl+left-click -> Show Package Contents. Place `ModTheSpire.jar` in `SlayTheSpire.app/Contents/Resources/`*
1. Place the helper script inside your Slay the Spire installation directory.
    * For Windows, copy `MTS.cmd` to your Slay the Spire install directory.
    * For Linux, copy `MTS.sh` to your Slay the Spire install directory and make it executable.
1. Create a `mods` directory inside your Slay the Spire installation directory.
1. Place any mods inside the `mods` directory.
```
SlayTheSpire/
    mods/
        Mod1.jar
        Mod2.jar
    desktop-1.0.jar
    ModTheSpire.jar
    ...
```
6. Run `ModTheSpire.jar`.
    * For Windows, run `MTS.cmd`.
    * For Linux, run `MTS.sh`.
    * Or run `ModTheSpire.jar` with Java 8.
1. (Optional) Run ModTheSpire through `SlayTheSpire.exe`.
    1. Rename `desktop-1.0.jar` to `SlayTheSpire.jar`.
    1. Rename `ModTheSpire.jar` to `desktop-1.0.jar`.
    1. Run SlayTheSpire.exe or run Slay the Spire through Steam.

### Mac
On Mac, depending on your installation, `desktop-1.0.jar` might be located in either:
* `~/Applications/Slay the Spire.app/Contents/Resources`
* `~/Library/Application\ Support/Steam/steamapps/common/SlayTheSpire/SlayTheSpire.app/Contents/Resources`

Install ModTheSpire in whichever you find `desktop-1.0.jar`.
