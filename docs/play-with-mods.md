---
id: play-with-mods
title: Playing With Mods
---

This guide will help you get set up to play mods that people create for Slay the Spire.
Following the guide is also necessary for developing mods. If you are looking to develop mods see [Getting Started](getting-started.md)

### ModTheSpire

Download the latest release of [ModTheSpire](https://github.com/kiooeht/ModTheSpire/releases/latest).

Place `ModTheSpire.jar` inside your Slay the Spire installation directory. It should be alongside `desktop-1.0.jar`.

> Note: On Mac, the installation directory is inside `SlayTheSpire.app`. Open it with Right-click -> Show Package Contents or Ctrl+left-click -> Show Package Contents. Place `ModTheSpire.jar` in `SlayTheSpire.app/Contents/Resources/`

### Starting Script

Place the helper script inside your Slay the Spire installation directory.

**Windows**

Copy `MTS.cmd` to your Slay the Spire install directory.

**Linux**

Copy `MTS.sh` to your Slay the Spire install directory and make it executable.

### Folder Structure

Create a `mods` directory inside your Slay the Spire installation directory.
Place any mods inside the `mods` directory.

Your folder structure should look like:

```
SlayTheSpire/
    mods/
        Mod1.jar
        Mod2.jar
    desktop-1.0.jar
    ModTheSpire.jar
    ...
```

### Starting Slay The Spire with Mods

Use the starting scripts to start Slay the Spire with the mods in your `mods` folder. This will execute `ModTheSpire.jar` instead of `desktop-1.0.jar`.

For Windows, run `MTS.cmd`

For Linux, run `MTS.sh`

> You can also run `ModTheSpire.jar` with Java 8 if you do not wish to use the starting script

Now that you have Slay the Spire set up to run with mods, check out the current [list of known mods](known-mods.md) and play with some!

### Running Through Steam (Optional)

It is possible to run Slay the Spire with mods through Steam.

Rename `desktop-1.0.jar` to `SlayTheSpire.jar`.

Rename `ModTheSpire.jar` to `desktop-1.0.jar`.

Run Slay the Spire through Steam or run `SlayTheSpire.exe`