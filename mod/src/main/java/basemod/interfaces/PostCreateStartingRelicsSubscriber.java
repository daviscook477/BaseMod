package basemod.interfaces;

import com.megacrit.cardcrawl.characters.AbstractPlayer;

import java.util.ArrayList;

@Deprecated
public interface PostCreateStartingRelicsSubscriber extends ISubscriber {
	void receivePostCreateStartingRelics(AbstractPlayer.PlayerClass chosenClass, ArrayList<String> addRelicsToMe);
}
