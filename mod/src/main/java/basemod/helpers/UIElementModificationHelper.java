package basemod.helpers;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;

public class UIElementModificationHelper {
    public static void moveHitboxByOriginalParameters(Hitbox hb, float x, float y) {
        hb.move(x + hb.width / 2.0F, y + hb.height / 2.0F);
    }
}
