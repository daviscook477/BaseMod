package basemod.customacts.savefields;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.StartActSubscriber;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.ScoreBonusStrings;

import java.util.HashMap;
import java.util.Map;

public class Breadcrumbs implements CustomSavable<Map<Integer, String>>,
        PostInitializeSubscriber,
        EditStringsSubscriber,
        StartActSubscriber {

    private Map<Integer, String> breadcrumbs = new HashMap<>();
    private static Breadcrumbs bc;
    public static Map<Integer, String> getBreadCrumbs() {
        return bc.breadcrumbs;
    }

    public static void initialize() {
        BaseMod.subscribe(bc = new Breadcrumbs());
    }

    @Override
    public void receivePostInitialize() {
        BaseMod.addSaveField("ActLikeIt:breadCrumbs", bc);

        ElitesSlain.initialize(); //Doing this means I don't have to subscribe ElitesSlain to BaseMod.
    }


    private void loadLocStrings(String language) {
        BaseMod.loadCustomStringsFile(EventStrings.class, "localization/customacts/" + language + "/events.json");
        BaseMod.loadCustomStringsFile(ScoreBonusStrings.class, "localization/customacts/" + language + "/score_bonuses.json");
    }



    private String languageSupport() {
        switch (Settings.language) {
            default:
                return "eng";
        }
    }

    @Override
    public void receiveEditStrings() {
        String language = languageSupport();

        // Load english first to avoid crashing if translation doesn't exist for something. Blatantly stolen from Vex.
        loadLocStrings("eng");
        if(!Settings.language.equals("eng")) {
            try {
                loadLocStrings(language);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void receiveStartAct() {
        if(AbstractDungeon.id != null) {
            if (AbstractDungeon.actNum <= 1) {
                BaseMod.logger.info("breadcrumbs cleared!");
                breadcrumbs.clear();
            }
            switch (AbstractDungeon.id) {
                case Exordium.ID:
                case TheCity.ID:
                case TheBeyond.ID:
                case TheEnding.ID:
                    break;

                default:
                    //Add the ID of the dungeon into the map, keyd by the actnumber. This is for the score display at the end of the run.
                    BaseMod.logger.info("Adding to breadcrumbs: " + AbstractDungeon.id + " (" + AbstractDungeon.actNum + ")");
                    breadcrumbs.put(AbstractDungeon.actNum, AbstractDungeon.id);
                    break;
            }
        }
    }


    @Override
    public Map<Integer, String> onSave() {
        BaseMod.logger.info("Saving breadcrumbs Map with size: " + breadcrumbs.size());
        return breadcrumbs;
    }

    @Override
    public void onLoad(Map<Integer, String> loaded) {
        if (loaded != null) {
            breadcrumbs = loaded;
        } else {
            breadcrumbs = new HashMap<>();
        }
        BaseMod.logger.info("Loading breadcrumbs Map with size: " + breadcrumbs.size());
    }
}