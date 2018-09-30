---
id:  custom-monsters
title: Custom Monsters
---

## Adding a Monster

You don't actually add "monsters" you add "encounters". Adding an encounter is a two step process, first you must register the monster, then add the encounter to a dungeon.

This API should be called in `postReceiveInitialize`.

### Register a Monster

`addMonster(String encounterID, BaseMod.GetMonster monster)`

`addMonster(String encounterID, String name, BaseMod.GetMonster monster)`

`addMonster(String encounterID, BaseMod.GetMonsterGroup group)`

`addMonster(String encounterID, String name, BaseMod.GetMonsterGroup group)`

* `encounterID`: ID for this set of monster(s).
* `name` (optional): The name the monster(s) should be known as in the Run History screen. If not given, BaseMod automatically makes a name for you.
* `monster`: A lambda that returns a newly constructed instance of your monster.
* `group`: A lambda that returns a newly constructed instance of `MonsterGroup`, containing your monster(s).

### Add Encounter to Dungeon

`addMonsterEncounter(String dungeonID, MonsterInfo encounter)`

`addStrongMonsterEncounter(String dungeonID, MonsterInfo encounter)`

`addEliteEncounter(String dungeonID, MonsterInfo encounter)`

* `dungeonID`: The ID of the dungeon whose pool you want to add the encounter to. (e.g. `Exordium.ID`, `TheCity.ID`, `TheBeyond.ID`).
* `encounter`: A `MonsterInfo` containing the `encounterID` you registered and the probability of the encounter appearing.


### Example
```Java
public void receivePostInitialize()
{
    // Add a single monster encounter
    BaseMod.addMonster(MyMonster.ID, () -> new MyMonster());
    // Add a multi-monster encounter
    BaseMod.addMonster("MyMonsters", () -> new MonsterGroup(new AbstractMonster[] {
            new MyMonster(),
            new MyMonster2()
    }));

    BaseMod.addMonsterEncounter(TheCity.ID, new MonsterInfo(MyMonster.ID, 5));
    BaseMod.addStrongMonsterEncounter(TheBeyond.ID, new MonsterInfo("MyMonsters", 10));
}
```

## Adding a Boss

Adding a new boss requires registering it the same way as you register monsters (see "Register a Monster" above).

After registering the boss, add it to a dungeon with:

`addBoss(String dungeon, String bossID, String mapIcon, String mapIconOutline)`

* `dungeon`: The ID of the dungeon whose pool you want to add the encounter to. (e.g. `Exordium.ID`, `TheCity.ID`, `TheBeyond.ID`).
* `bossID`: The ID used to register the boss with `addMonster`.
* `mapIcon`: The path to an image to be used as the boss's icon on the map.
* `mapIconOutline`: The path to an image to be used as the boss icon outline on the map.

### Example
```Java
public void receivePostInitialize()
{
    BaseMod.addMonster(MyBoss.ID, () -> new MyBoss());

    BaseMod.addBoss(Exordium.ID, MyBoss.ID,
            "images/mymod/ui/map/boss/myBoss.png",
            "images/mymod/ui/map/bossOutline/myBoss.png");
}
```