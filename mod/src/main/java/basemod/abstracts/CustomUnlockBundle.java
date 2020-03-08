package basemod.abstracts;

import com.megacrit.cardcrawl.unlock.AbstractUnlock;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;

public class CustomUnlockBundle {
	private ArrayList<String> unlocks;
	private ArrayList<CustomUnlock> actualUnlocks;
	private boolean initialized = false;
	public AbstractUnlock.UnlockType unlockType;

	public CustomUnlockBundle(String unlock1, String unlock2, String unlock3) {
		this(AbstractUnlock.UnlockType.CARD, unlock1, unlock2, unlock3);
	}

	public CustomUnlockBundle(AbstractUnlock.UnlockType type, String unlock1, String unlock2, String unlock3) {
		unlockType = type;
		unlocks = new ArrayList<>();
		actualUnlocks = new ArrayList<>();
		unlocks.add(unlock1);
		unlocks.add(unlock2);
		unlocks.add(unlock3);

		if (type == AbstractUnlock.UnlockType.CARD) {
			UnlockTracker.addCard(unlock1);
			UnlockTracker.addCard(unlock2);
			UnlockTracker.addCard(unlock3);
		} else {
			UnlockTracker.addRelic(unlock1);
			UnlockTracker.addRelic(unlock2);
			UnlockTracker.addRelic(unlock3);
		}
	}

	public ArrayList<String> getUnlockIDs()
	{
		return unlocks;
	}
	
	public ArrayList<CustomUnlock> getUnlocks() {
		if (!initialized) {
			actualUnlocks.add(new CustomUnlock(unlockType, unlocks.get(0)));
			actualUnlocks.add(new CustomUnlock(unlockType, unlocks.get(1)));
			actualUnlocks.add(new CustomUnlock(unlockType, unlocks.get(2)));
			initialized = true;
		}

		return actualUnlocks;
	}
}
