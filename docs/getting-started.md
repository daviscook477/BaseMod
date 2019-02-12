---
id: getting-started
title: Getting Started
sidebar_label: Getting Started
---

Welcome to modding Slay the Spire! This page will help you build your first Slay the Spire mod.

> If you are not looking to develop mods, the guide to play other people's mods is found [here](play-with-mods.md)

## Setting Up Your Development Environment

### Install Maven
Maven is used for dependency management and packaging any mods you create. Download it [here](https://maven.apache.org/download.cgi) and install it.

> If you are having trouble installing Maven, try following [this guide](intellij-setup.md) which uses IntelliJ IDEA's built in Maven support

### Download the Skeleton Repository
Download the [STSModSetUp](https://github.com/Kobting/STSModSetup) repository as a zip. Unzip where you want to set up your dev environment.

*Do not clone or fork unless you really want to go through the hassle of changing git things later*

### Adding Dependencies
The `lib` folder is where we will keep our dependencies. This is very useful if you want to have multiple STS modding projects.

Download the latest release `.jar` for each project and place in the lib folder
- [ModTheSpire](https://github.com/kiooeht/ModTheSpire/releases/latest)
- [BaseMod](https://github.com/daviscook477/BaseMod/releases/latest)

The final dependency you will need is the actual game. There is a file called `desktop-1.0.jar` located where your game is installed. Copy `desktop-1.0.jar` into the `lib` folder.

> ModTheSpire and BaseMod only support the main branch of the game (i.e. not the beta branch).

> When Slay The Spire updates, you will need to replace `desktop-1.0.jar` with the latest version.

### Import Project Into an IDE
This set up should be IDE agnostic (i.e. you can use whatever IDE you like). Here are steps for importing your project into two of the more popular IDEs

**IntelliJ**

*File -> Project from Existing Sources -> Select ExampleMod folder or your own mod that follows the structure of the ExampleMod -> Select Maven -> Press next until your project is built*.

Click on the Maven Projects tab on the right of the editor and click the refresh icon to load your dependencies from the pom.xml into your project. (If no Maven projects tab on the right *View -> Tool Windows -> Maven Projects*)

**Eclipse**

*File -> Import -> Maven -> Existing Maven Projects -> Browse and select ExampleMod folder or your own mod that follows the structure of the ExampleMod*.

You may need to right-click your project *Maven -> Update Project -> Select your current project -> Click OK*.

## Setting up Slay The Spire for Mods
If you have played Slay the Spire with mods or have set up ModTheSpire, you can skip this step. In this tutorial, the directory that Slay the Spire is installed in will be refered to as the Slay the Spire directory.

Follow the steps on the [Playing With Mods](play-with-mods.md) page.

Copy `BaseMod.jar` from your `lib` folder into your `mods` folder in your Slay the Spire directory.

## Writing Your First Mod

### Package the Skelaton App
If you have followed the steps so far, you should be able to package the mod that you downloaded in the skelaton repository.

Open a terminal in the `ExampleMod` directory and run `mvn package`. This should package ExampleMod into `ExampleMod.jar`. You can find it in `/_ModTheSpire/mods/ExampleMod.jar`.

Copy `ExampleMod.jar` into the `mods` folder in your Slay the Spire directory.

You should be able to run Slay the Spire by running `MTS.cmd` (`MTS.sh` on a mac).

### Write Some Code
You've probably noticed our ExampleMod didn't do anything. Let's change that!

Right now, `ModInitializer.java` should look like this

```Java
@SpireInitializer
public class ModInitializer {


    public ModInitializer(){
        //Use this for when you subscribe to any hooks offered by BaseMod.
        BaseMod.subscribe(this)
    }

    //Used by @SpireInitializer
    public static void initialize(){

        //This creates an instance of our classes and gets our code going after BaseMod and ModTheSpire initialize.
        ModInitializer modInitializer = new ModInitializer();
    }

}
```

BaseMod uses listeners to run code when certain events happen. Let's implement the `PostDrawSubscriber`.

```Java
public class ModInitializer implements PostDrawSubscriber {
```

```Java
@Override
public void receivePostDraw(AbstractCard card) {
    System.out.println(card.name + " was drawn!");
}
```

Run `mvn package` and copy the packaged `.jar` file into your mods folder and run your game. You should notice the card name being printed out in the ModTheSpire console.

## Going Further 

### What Other Listeners Exist

Most mods will probably be more complicated than printing something when a card is drawn. A full list of listeners can be found in `src/main/java/basemod/interfaces` in BaseMod.

### Looking at the Game's Source Code

What else can I do with `AbstractCard`? What about `AbstractDungeon`? How does Slay the Spire work under the hood. A lot of these questions can be answered by looking at the game's source code. You can do this by decompiling the game. Follow [this guide](decompiling.md) to do that.

### Can I Make My Own Listener

You bet you can. Check out how listeners are made in BaseMod and check out the documentation about `SpirePatch` in the [ModTheSpire docs](https://github.com/kiooeht/ModTheSpire/wiki/SpirePatch)

### Join The Modding Community

Come hang out with us on [Discord](https://discord.gg/aj3jQp). It's a pretty active community and everyone is super friendly!

Happy Modding!
