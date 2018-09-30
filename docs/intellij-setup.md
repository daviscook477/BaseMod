---
id: intellij-setup
title: IntelliJ IDEA Setup
---

IntelliJ will be the environment in which you will do all your modding. Download it [here](https://www.jetbrains.com/idea/download/)

## Starting a new project

Once you start IntelliJ for the first time. you'll be greeted by this very important theme selection window.

![](https://cdn.discordapp.com/attachments/478705112773034004/482040748037111818/unknown.png)

Using Darcula is advised as the light theme will burn your eyes after staring at it for too long.

After that IntelliJ will give you the option to `Create New Project`.

![](https://i.imgur.com/NxYFb6D.png)

Set the Project Type on the left to Maven and set your Project SDK to Java 1.8. (Navigate to the folder where you installed the JDK.)

![](https://i.imgur.com/mZDhWhS.png)

Go ahead and give your mod whatever ID you desire.

![](https://i.imgur.com/dWS14vM.png)

Then save it into your `my_mods` directory.

![](https://i.imgur.com/vdMDwW8.png)

It'll open up to your newly generated `pom.xml` file. Go ahead and copy paste this example `pom.xml` into that space. Then press Import Changes in the bottom right corner.

Example pom.xml: [Link](https://gist.github.com/alexdriedger/fb74397086ee80073417f19d6305bb05)

## Dependencies

Copy `ModTheSpire.jar` and `BaseMod.jar` into the `lib` folder you created earlier. Then, go to your Slay The Spire folder and copy `desktop-1.0.jar` into the lib folder too.

Now press Ctrl+Alt+Shift+S to open up Project Structure and click on Modules on the left. Go ahead and check off all your dependencies and hit `Apply`.

![](https://i.imgur.com/zcsFzzJ.png)

All your code will go into `your_mod\src\main\java`. Simply create a new file by right clicking on the directory and going to `New > Class`. You can create directories by doing `New > Directory`.

## Building

You can build your project by going to `View > Tool Windows > Maven Projects`. It will open up this side window:

![](https://i.imgur.com/7aaKFdc.png)

Expand `Lifecycle` and double click `package`

![](https://cdn.discordapp.com/attachments/398373038732738570/485632315901345802/idea64_2018-09-01_19-08-21.png)

It will then output a `.jar` you can use as a mod to the directory specified by your `pom.xml`.

```xml
<target>
    <copy file="target/ExampleMod.jar" tofile="../lib/ExampleMod.jar"/> // tofile location is where you can find your compiled .jar file
</target>
```
### Actually Modding!

You can start making your mod at the next part of the tutorial [here](getting-started.md#write-some-code)

