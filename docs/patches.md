---
id: patches
title: Patches (MTS)
sidebar_label: Patches
---

ModTheSpire uses SpirePatch to allow mods to patch their own code into Slay The Spire. When loading a mod, ModTheSpire searches through every class in the mod for any that have the `SpirePatch` annotation. For each method you want to patch with your mod, you must create a new class and annotate it with `SpirePatch`.

ModTheSpire currently supports the follow patch types:
* [Prefix](#prefix)
* [Postfix](#postfix)
* [Insert](#insert)
* [Instrument](#instrument)
* [Replace](#replace)
* [Raw](#raw)

## General Rules

* A patch class must be a __public static__ class.
* A patch method must be a __public static__ method.
* Use the `@SpirePatch` annotation on the patch class.
* Patch methods are passed all the arguments of the original method as well as the instance if original method is not static (instance first, then parameters).

```Java
public static void [PatchMethod]([InstanceType] __instance, [parameters]...) { ... }
```

### @SpirePatch Parameters

* __clz__ Defines the class that contains the method to be patched.
* __cls__ **(Old way)** Defines the class that contains the method to be patched. Must be the complete class path and class name.
* __method__ Defines the method to be patched.
  * Use `SpirePatch.CONSTRUCTOR` to target a constructor.
  * Use `SpirePatch.STATICINITIALIZER` to target a static initializer.
* __paramtypez__ Defines the parameter types of the method to be patched (only necessary if multiple methods with the same name exist).
* __paramtypes__ **(Old way)** Defines the parameter types of the method to be patched (only necessary if multiple methods with the same name exist). Type names must be complete class path and class name.

```Java
@SpirePatch(
	clz=AbstractPlayer.class,
	method="useCard",
	paramtypez={
		AbstractCard.class,
		AbstractMonster.class,
		int.class
	}
)
public static class ExamplePatch
{
	...
}
```

### Patching Order

Patches are applied first in order of type, then in order of mod. Patch type is ordered: Insert, Instrument, Replace, Prefix, Postfix, Raw. This means all Insert patches will be applied before any Instrument patches are applied, and so on. If two or more mods are loaded and define patches of the same type, those patches will be loaded in the order the mods were ordered in the launcher. If a single mod defines multiple patches of the same type, they will be applied in an arbitrary order.

## Prefix

Prefix patching inserts a call to your `Prefix` method at the start of the method you are patching.

You may also use the `@SpirePrefixPatch` annotation to denote a method to be a Prefix.

```Java
public static void Prefix(Ironclad __instance)
{
	...
}

@SpirePrefixPatch
public static void Foobar(Ironclad __instance)
{
	...
}
```

#### Features
* [@ByRef](https://github.com/kiooeht/ModTheSpire/wiki/@ByRef)
* [SpireReturn](https://github.com/kiooeht/ModTheSpire/wiki/SpireReturn)

## Postfix

Postfix patching inserts a call to your `Postfix` method at the end of the method you are patching. Postfix patches can also change the return value of the patched method. You can do this by adding a return value to your `Postfix` method. If you also add another parameter as the first parameter to your `Postfix` method of the same type as your `Postfix` returns, you will be passed the original return value of the patched method.

You may also use the `@SpirePostfixPatch` annotation to denote a method to be a Postfix.

```Java
public static void Postfix(Ironclad __instance)
// or
public static ArrayList<String> Postfix(ArrayList<String> __result, Ironclad __instance)
{
	__result.add("Example Card");
	return __result;
}

@SpirePostfixPatch
public static void Foobar(Ironclad __instance)
```

## Insert

Insert patching inserts a call to your `Insert` method in middle of the method you are patching. An `Insert` method must be accompanied by the @SpireInsertPatch.

You may also use the `@SpireInsertPatch` annotation to denote a method to be an Insert.

### @SpireInsertPatch Parameters

Either `loc` or `rloc` or `locator` _must_ be given. The patch method will be called directly before the line number specified. You can find the line numbers by using a java decompiler. [JD-GUI](https://github.com/java-decompiler/jd-gui/releases) seems to work best for getting correct line numbers. If JD-GUI can't decompile a class, use [Luyten](https://github.com/deathmarine/Luyten/releases) and turn on debug line numbers (Settings > Show Debug Line Numbers). The line numbers you want appear as comments at the start of each line in Luyten (ex: `/*SL:27*/`).

Example, with `loc=121`:

```Java
120:  System.out.println("A");
      // Code inserted here
121:  System.out.println("A");
122:  System.out.println("A");
```

If you use `rloc` there are a few things to keep in mind. A patch with `rloc=0` inserts before the first line with a debug line number in the body of the method you are patching. So, to get a `rloc` line number you can simply subtract the debug line number that would get `rloc=0` from the debug line number of the position you want to insert at.

Example (122 - 120 = `rloc=2`):

```Java
      public void print() {
        // rloc=0 would insert a patch here
120:    System.out.println("A");

        // rloc=2 would insert a patch here
122:    System.out.println("B");
      }
```

If you want the same patch to be inserted at multiple places you have the option of specifying `locs` or `rlocs` which are arrays of line numbers that your patch will inserted at. Example with `locs = {121, 123}`. If you specify `locs` or `rlocs` you _do not_ have to specify `loc` or `rloc`.

```Java
120:  System.out.println("A");
      // Code inserted here
121:  System.out.println("A");
122:  System.out.println("A");
			// Code **also** inserted here
123:  System.out.println("A");
```

* __loc__ Defines the absolute line number to insert at, absolute to the start of the file.
* __rloc__ Defines the line number to insert at, relative to the start of the method to be patched.
* __locs__ Defines an additional array of line numbers to insert at, absolute to the start of the file.
* __rlocs__ Defines an additional array of line numbers to insert at, relative to the start of the method to be patched.
* __localvars__ Used to capture any local variables and pass them to the patch method. Captured variables are passed as arguments, appearing the in parameter list after the original method's parameters.

```Java
@SpireInsertPatch(
	loc=123,
	localvars={"example"}
)
// or
@SpireInsertPatch(
	rloc=4,
	localvars={"example"}
)
public static void Insert(Ironclad __instance, String param1, String param2, int example)
{
	...
}
```

#### Features
* [@ByRef](https://github.com/kiooeht/ModTheSpire/wiki/@ByRef)
* [SpireReturn](https://github.com/kiooeht/ModTheSpire/wiki/SpireReturn)

### Locator

Since Slay The Spire is on a weekly update schedule, line numbers are subject to a lot of change. As such `rloc` is almost always preferable to `loc` since it is resilient to other areas of the file being patched changing. However, if you want a patch that is even more resilient to the weekly patches than `rloc`, you can use a `Locator`. A `Locator` is a function that is passed the raw `CtBehavior` from the Javassist API for the method your @SpireInsertPatch is patching. The `Locator` returns an array of line numbers that indicate where the patch should be applied. To specify a Locator, use the `locator` parameter of `@SpireInsertPatch`. When using a `Locator` you _should not_ specify `loc`, `rloc`, `locs`, or `rlocs` on your `@SpireInsertPatch` because the `Locator` will handle finding the line number.

The reason why `Locator` is preferable to `rloc` is because it can patch based on the _game logic_. For example, the fact that the game creates a `new AbstractDungeon` to start the game isn't likely to change ever, _however_, the location of the line where the game creates the `new AbstractDungeon` could change if the developers make some unrelated changes to the calling method. This change by the devs shouldn't prevent the patch from working but if the patch was done with `rloc` is very likely to fail now. Instead, with a `Locator` you can explicitly specify that you want to patch the line before the creation of `new AbstractDungeon` so no matter where that line changes to, the patch will always find it.

ModTheSpire provides an API that can assist you in finding the lines that match your expected location. The `LineFinder` provides two methods `findInOrder` and `findAllInOrder` that can be used to find the _first_ or find _all_ the lines matching a description specified by a `List<Matcher> expectedMatches` and `Matcher finalMatch`. This uses the `Matcher` type which is a simple way to express that you want to for example match a `new` declaration or a method call. Example for finding the line at which the `end` method is called on the `SpireBatch` in the `render` method on `CardCrawlGame`.

```Java
@SpirePatch(
  clz=CardCrawlGame.class,
  method="render"
)
public class PostRenderHook {

  @SpireInsertPatch(
    locator=Locator.class,
    localvars={"sb"}
  )
  public static void Insert(CardCrawlGame __instance, SpriteBatch sb) {
    // draw things right before the SpriteBatch has `end` called
  }

  // ModTheSpire searches for a nested class that extends SpireInsertLocator
  // This class will be the Locator for the @SpireInsertPatch
  // When a Locator is not specified, ModTheSpire uses the default behavior for the @SpireInsertPatch
  private static class Locator extends SpireInsertLocator {
    // This is the abstract method from SpireInsertLocator that will be used to find the line
    // numbers you want this patch inserted at
    public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
      // finalMatcher is the line that we want to insert our patch before -
      // in this example we are using a `MethodCallMatcher` which is a type
      // of matcher that matches a method call based on the type of the calling
      // object and the name of the method being called. Here you can see that
      // we're expecting the `end` method to be called on a `SpireBatch`
      Matcher finalMatcher = new Matcher.MethodCallMatcher(
          "com.badlogic.gdx.graphics.g2d.SpriteBatch", "end");

      // the `new ArrayList<Matcher>()` specifies the prerequisites before the line can be matched -
      // the LineFinder will search for all the prerequisites in order before it will match the finalMatcher -
      // since we didn't specify any prerequisites here, the LineFinder will simply find the first expression
      // that matches the finalMatcher.
      return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
    }
  }

}
```

If you want to write your own line finders to find more specific scenarios, like for example to find the last time a method is called, take a look at the `com.evacipated.cardcrawl.modthespire.finders.MatchFinderExprEditor` API and look at the sample implementations of `InOrderFinder` and `InOrderMultiFinder` here on Github.

## Instrument

Instrument patching is much more powerful that the previous types of patching, but also more complex. Instrument patching gives you more access to javassist's API, allowing you to alter code in the method you are patching. For example, removing or replacing all method calls inside the method you are patching. See the javassist tutorial and documentation:
https://jboss-javassist.github.io/javassist/tutorial/tutorial2.html#alter

```Java
import org.javassist.expr.ExprEditor;

public static ExprEditor Instrument()
{
	return new ExprEditor() {
		...
	};
}
```

## Replace

A Replace patch will completely replace a method with your own. None of the original method's code will be called, calling your replace patch's code instead.
For example, the following patch will replace the method body of `CardLibary.getCardList`:

```Java
@SpirePatch(
    clz=CardLibrary.class,
    method="getCardList"
)
public class GetCardList
{
    public static Object Replace(LibraryType type)
    {
        ...
    }
}
```

**Note** Because of the order patches are applied, Replace patching a method will override any Insert or Instrument patches on the same method. 

**Warning: DO NOT use a Replace patch unless absolutely necessary. The destructive nature of Replace patches mean you override any other patches applied to the method you're patching.**

## Raw

Raw patches give you access to the underlying Javassist API to make lower-level changes. This gives you must more flexibility in the changes you can make.

Raw patches are passed the `CtBehavior` object for the method you're patching. It is then up to you to use the Javassist API to make any changes you want. Starting places to learn Javassist: [CtBehavior](https://jboss-javassist.github.io/javassist/html/javassist/CtBehavior.html) and [Javassist tutorial](https://jboss-javassist.github.io/javassist/tutorial/tutorial2.html#alter).

```Java
public static void Raw(CtBehavior ctMethodToPatch)
{
    ...
}
```