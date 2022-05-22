package basemod.abstracts.events.phases;

import basemod.abstracts.events.PhasedEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;

public abstract class ImageEventPhase extends EventPhase {
    public abstract void optionChosen(int i);

    @Override
    public void hide(PhasedEvent event) {
        event.imageEventText.clearAllDialogs();
        GenericEventDialog.hide();
    }
}
