package basemod.customacts.savefields;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;

import java.util.HashMap;
import java.util.Map;

public class ElitesSlain implements CustomSavable<Map<Integer, Integer>> {

    private Map<Integer, Integer> killedElites = new HashMap<>();
    private static ElitesSlain bc;
    public static Map<Integer, Integer> getKilledElites() {
        return bc.killedElites;
    }

    public static void initialize() {
        BaseMod.addSaveField("ActLikeIt:elitesSlain", bc = new ElitesSlain());
    }


    @Override
    public Map<Integer, Integer> onSave() {
        BaseMod.logger.info("Saving ElitesSlain Map with size: " + killedElites.size());
        return killedElites;
    }

    @Override
    public void onLoad(Map<Integer, Integer> loaded) {
        if (loaded != null) {
            killedElites = loaded;
        } else {
            killedElites = new HashMap<>();
        }
        BaseMod.logger.info("Loading ElitesSlain Map with size: " + killedElites.size());
    }
}