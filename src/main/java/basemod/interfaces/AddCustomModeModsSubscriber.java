package basemod.interfaces;

import com.megacrit.cardcrawl.screens.custom.CustomMod;

import java.util.List;

public interface AddCustomModeModsSubscriber extends ISubscriber {
    void receiveCustomModeMods(List<CustomMod> modList);
}
