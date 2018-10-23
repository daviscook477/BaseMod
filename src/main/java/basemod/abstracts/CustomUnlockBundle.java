package basemod.abstracts;

import com.megacrit.cardcrawl.unlock.AbstractUnlock;

import java.util.ArrayList;

public class CustomUnlockBundle {
	private ArrayList<String> unlocks;
	private ArrayList<CustomUnlock> actualUnlocks;
	private boolean initialized = false;
	private AbstractUnlock.UnlockType unlockType;

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
	}
	
	public ArrayList<CustomUnlock> getUnlocks() {
		if (!initialized) {
			actualUnlocks.add(new CustomUnlock(unlockType, unlocks.remove(0)));
			actualUnlocks.add(new CustomUnlock(unlockType, unlocks.remove(0)));
			actualUnlocks.add(new CustomUnlock(unlockType, unlocks.remove(0)));
			initialized = true;
		}

		return actualUnlocks;
	}
}
