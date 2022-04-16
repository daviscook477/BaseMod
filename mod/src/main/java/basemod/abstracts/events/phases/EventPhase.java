package basemod.abstracts.events.phases;

import basemod.abstracts.events.PhasedEvent;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class EventPhase {
    public abstract void transition(PhasedEvent event);
    public abstract void hide(PhasedEvent event);

    public void update() {

    }
    public void render(SpriteBatch sb) {

    }
    public void renderAboveTopPanel(SpriteBatch sb) {

    }

    public boolean reopen(PhasedEvent phasedEvent) {
        return false;
    }
}
