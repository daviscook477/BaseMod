---
id: custom-relics
title: Custom Relics
---

## API
To add or remove relics you need to put your relic manipulation code inside the `receiveEditRelics` callback from `EditRelicsSubscriber`.

* `addRelic(AbstractRelic relic, RelicType type)` - this can be used to add custom relics to either the shared pool or the pool for the ironclad or the pool for the silent; this method **should not** be used to add relics specific to a *custom* character
* `removeRelic(AbstractRelic relic)` - same restrictions as above but removes the relic instead of adding it
* `addRelicToCustomPool(AbstractRelic relic, String color) - this can be used to add a relic to the relic pool for a custom character. **Do not** attempt to use this to add a relic to the shared pool or the ironclad/silent pool.

## CustomRelic Class
The `CustomRelic` class can be extended to make relics easier. It handles loading the texture for you (if you extend AbstractRelic you need to setup a workaround to load the texture). The constructor is:
* `CustomRelic(String id, Texture texture, RelicTier tier, LandingSound sfx)`

The size of the relic texture should be `128x128px` with the relic image itself only occupying the center ~48 to 64px box. The rest of the texture should be completely transparent.

## Example
Here's an example of how you could add a relic that gives the player max hp when they pick it up

```Java
public class Blueberries extends CustomRelic {
	public static final String ID = "Blueberries";
	private static final int HP_PER_CARD = 1;
	
	public Blueberries() {
		super(ID, MyMod.getBlueberriesTexture(), // you could create the texture in this class if you wanted too
				RelicTier.UNCOMMON, LandingSound.MAGICAL); // this relic is uncommon and sounds magic when you click it
	}
	
	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0] + HP_PER_CARD + DESCRIPTIONS[1]; // DESCRIPTIONS pulls from your localization file
	}
	
	@Override
	public void onEquip() {
		int count = 0;
		for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
			if (c.isEthereal) { // when equipped (picked up) this relic counts how many ethereal cards are in the player's deck
				count++;
			}
		}
		AbstractDungeon.player.increaseMaxHp(count * HP_PER_CARD, true);
	}
	
	@Override
	public AbstractRelic makeCopy() { // always override this method to return a new instance of your relic
		return new Blueberries();
	}
	
}
```