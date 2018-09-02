---
id: mod-badges
title: Mod Badges
---

## Description

These are `32x32` images used to represent mods that display under the title on the main menu. Clicking one opens that mod's settings menu.
* `BaseMod.registerModBadge(Texture texture, String modName, String author, String description, ModPanel settingsPanel)` - register your Mod Badge with **BaseMod**.
* `ModPanel.addButton(float x, float y, Consumer<ModButton> clickEvent)` - add a button to your settings menu.
* `ModPanel.addLabel(String text, float x, float y, Consumer<ModLabel> updateEvent)` - add a label to your settings menu.
* There is some more functionality here, but it is likely to changed soon and/or cleaned up.

## Example

```java
ModPanel settingsPanel = new ModPanel();
settingsPanel.addLabel("", 475.0f, 700.0f, (me) -> {
    if (me.parent.waitingOnEvent) {
        me.text = "Press key";
    } else {
        me.text = "Change console hotkey (" + Keys.toString(DevConsole.toggleKey) + ")";
    }
});

settingsPanel.addButton(350.0f, 650.0f, (me) -> {
    me.parent.waitingOnEvent = true;
    oldInputProcessor = Gdx.input.getInputProcessor();
    Gdx.input.setInputProcessor(new InputAdapter() {
        @Override
        public boolean keyUp(int keycode) {
            DevConsole.toggleKey = keycode;
            me.parent.waitingOnEvent = false;
            Gdx.input.setInputProcessor(oldInputProcessor);
            return true;
        }
    });
});

Texture badgeTexture = new Texture(Gdx.files.internal("img/BaseModBadge.png"));
registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);
```

## Further Reading

Take a look at `basemod.BaseModInit` to see the code used to create the Mod Badge for **BaseMod**.