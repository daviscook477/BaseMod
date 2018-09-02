---
id:  custom-keywords
title: Custom Keywords
---

## Keywords
In the base base game keywords are things like "Block", "Retain", "Weak", "Vulnerable", etc... that are highlighted and have tooltip information associated with them.

## API
`addKeyword(String[] names, String description)` - only call this in the `receiveEditKeywords` callback that you get from `EditKeywordsSubscriber` or else your keywords might not be initialized properly

* `names` - This should be an array of similar words to the word you want. `(kinda like this {"block", "blocks"})`. The name must be all lowercase or it will not work.
* `description` - This should be the description for the keyword

## Example
In your EditKeywordsSubscriber method, let's say you want to create a keyword called "Ice", which has the text "Will apply a buff to the next attack with Ice in its description.":

```java
BaseMod.addKeyword({"ice"},"Will apply a buff to the next attack with Ice in its description.");
```

It's that simple! Sample and method provided/found by `@Blank The Evil`.