---
id: custom-events
title: Custom Events
---

## Creating an Event.

The first thing you want to do is create a class that extends AbstractEvent (or any abstract class that extends it) 

Here is a very simple example of an event.

```java
public class MyFirstEvent extends AbstractImageEvent {

    //This isn't technically needed but it becomes useful later
    public static final String ID = "My First Event";

    public MyFirstEvent(){
        super(ID, "My body text", "img/events/eventpicture.png");
        
        //This is where you would create your dialog options
        this.imageEventText.setDialogOption("My Dialog Option"); //This adds the option to a list of options
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        //It is best to look at examples of what to do here in the basegame event classes, but essentially when you click on a dialog option the index of the pressed button is passed here as buttonPressed.
    }
}
```

A more advanced example of a custom event can be found [Here](https://github.com/GraysonnG/InfiniteSpire/blob/master/src/main/java/infinitespire/events/EmptyRestSite.java)

## Adding the Event

Now that you have created your event, you will want to add that event to one of the games event pools. Thankfully that process is now handled by BaseMod.

All you need to do is call this method in receivePostInitialize: 

`BaseMod.addEvent(String eventID, Class<? extends AbstractEvent> class, String dungeonID)`
* `eventID` : The ID of the event. This should be the same as in your eventStrings.json
* `class` : The class of the event you are adding.
* `dungeonID` : The ID of the dungeon whose pool you want to add the event to. (e.g. `Exordium.ID`, `TheCity.ID`, `TheBeyond.ID`). Leaving off this argument adds the event to all pools.

A quick example using the method above would be:

```java
@Override
public void receivePostInitialize() {
    BaseMod.addEvent(MyFirstEvent.ID, MyFirstEvent.class);
    BaseMod.addEvent(MySecondEvent.ID, MySecondEvent.class, TheCity.ID);
}
```