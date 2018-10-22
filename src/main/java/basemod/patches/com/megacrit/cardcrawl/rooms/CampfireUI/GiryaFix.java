package basemod.patches.com.megacrit.cardcrawl.rooms.CampfireUI;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import com.megacrit.cardcrawl.vfx.campfire.CampfireLiftEffect;

import java.util.ArrayList;

@SpirePatch(cls="com.megacrit.cardcrawl.rooms.CampfireUI", method="initializeButtons")
public class GiryaFix {
    public static void Postfix(Object meObj) {
        CampfireUI me = (CampfireUI) meObj;
        if (AbstractDungeon.player.hasRelic("Girya")) {
            int liftCount = AbstractDungeon.player.getRelic("Girya").counter;
            boolean isLifting = AbstractDungeon.effectList.stream().filter((fx) -> fx instanceof CampfireLiftEffect).findFirst().orElse(null) != null;

            if ((liftCount == 2) && isLifting) {
                @SuppressWarnings("unchecked")
                ArrayList<AbstractCampfireOption> campfireButtons = (ArrayList<AbstractCampfireOption>) ReflectionHacks.getPrivate(me, CampfireUI.class, "buttons");
                if (campfireButtons != null) {
                    campfireButtons.remove(campfireButtons.size() - 1);
                    if (campfireButtons.size() == 3) {
                        campfireButtons.get(campfireButtons.size() - 1).setPosition(950.0F * Settings.scale, 450.0F * Settings.scale);
                    }

                    ReflectionHacks.setPrivate(me, CampfireUI.class, "buttons", campfireButtons);
                }
            }
        }
    }
}