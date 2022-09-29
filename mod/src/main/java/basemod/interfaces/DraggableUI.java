package basemod.interfaces;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.io.IOException;
import java.util.ArrayList;

public interface DraggableUI {

    ArrayList<DraggableUI> elements = new ArrayList<>();
    DraggableUI[] grabbedUiElement = new DraggableUI[1];

    Hitbox getHitbox();

    float getCenterX();
    float getCenterY();

    void setCenterX(float x);
    void setCenterY(float y);

    SpireConfig draggableUiConfig = getConfig();


    // if true, allows registering multiple instances of the same class.
    // if you do this, be sure to dispose of the UI elements you won't use anymore with the dispose method, else they will stay loaded and waste memory
    // the saving of how the ui was moved also isn't supported in that case
    default boolean allowRegisteringMultipleInstances() {
        return false;
    }

    static void register(DraggableUI element) {
        if (element.allowRegisteringMultipleInstances()) {
            if (!elements.contains(element)) elements.add(element);
        } else {
            for (DraggableUI e : elements) {
                if (e.getClass().equals(element.getClass())) {
                    element.setCenterX(e.getCenterX());
                    element.setCenterY(e.getCenterY());
                    elements.set(elements.indexOf(e), element);
                    return;
                }
            }
            elements.add(element);
            loadConfig(element);
        }
    }

    static void dispose(DraggableUI element) {
        elements.remove(element);
    }

    static void update() {
        if (grabbedUiElement[0] == null) {
            for (DraggableUI e : elements) {
                Hitbox hb = e.getHitbox();
                if (hb.hovered && InputHelper.justClickedLeft) {
                    grabbedUiElement[0] = e;
                    break;
                }
            }
        }

        DraggableUI e = grabbedUiElement[0];
        if (e != null) {
            Hitbox hb = e.getHitbox();
            hb.x = InputHelper.mX - hb.width / 2f;
            hb.y = InputHelper.mY - hb.height / 2f;
            e.setCenterX(InputHelper.mX);
            e.setCenterY(InputHelper.mY);
            if (!e.allowRegisteringMultipleInstances() && draggableUiConfig != null) {
                draggableUiConfig.setFloat(e.getClass().getName() + "_x", e.getCenterX());
                draggableUiConfig.setFloat(e.getClass().getName() + "_y", e.getCenterY());
                try {
                    draggableUiConfig.save();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (InputHelper.justReleasedClickLeft) {
            grabbedUiElement[0] = null;
        }
    }

    static SpireConfig getConfig() {
        try {
            SpireConfig spireConfig = new SpireConfig("BaseMod","DraggableUI");
            spireConfig.load();
            return spireConfig;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void loadConfig(DraggableUI element) {
        if (draggableUiConfig != null) {
            if (!draggableUiConfig.has(element.getClass().getName() + "_x")) {
                draggableUiConfig.setFloat(element.getClass().getName() + "_x", element.getCenterX());
            }
            if (!draggableUiConfig.has(element.getClass().getName() + "_y")) {
                draggableUiConfig.setFloat(element.getClass().getName() + "_y", element.getCenterY());
            }
            if (draggableUiConfig.has(element.getClass().getName() + "_x") && draggableUiConfig.has(element.getClass().getName() + "_y")) {
                float x = draggableUiConfig.getFloat(element.getClass().getName() + "_x");
                float y = draggableUiConfig.getFloat(element.getClass().getName() + "_y");
                element.setCenterX(x);
                element.setCenterY(y);
                Hitbox hb = element.getHitbox();
                hb.x = x - hb.width / 2f;
                hb.y = y - hb.height / 2f;
            }
        }
    }

    static void resetConfig(DraggableUI element) {
        if (draggableUiConfig != null) {
            draggableUiConfig.remove(element.getClass().getName() + "_x");
            draggableUiConfig.remove(element.getClass().getName() + "_y");
        }
    }

}
